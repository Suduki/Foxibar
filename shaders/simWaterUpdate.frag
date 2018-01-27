#version 430


layout(binding = 0) uniform sampler2D stuffTexture;
layout(binding = 1) uniform sampler2D fluxTexture;
layout(binding = 2) uniform sampler2D strataTexture;

in vec2 texCoord;

layout(location = 0) uniform float d;
layout(location = 2) uniform float dt = 1.0/30.0;
layout(location = 3) uniform float normalizingFactor;

layout(location = 0) out vec4 outHeights;
layout(location = 1) out vec4 outFlux;
layout(location = 2) out vec4 outVelocity;

const float lu = 1.0; // Pipe length.
const float lv = 1.0; // Pipe length.

const float kC = 0.01; // Water sediment capacity constant.
const float kS = 0.01; // Sediment solution constant.
const float kD = 0.03; // Sediment deposition constant;

vec3 calculateNormal(sampler2D sampler, vec2 texCoord)
{
	ivec2 ts = textureSize(sampler, 0);
	vec2 du = vec2(1.0/ts.x, 0.0);
	vec2 dv = vec2(0.0,      1.0/ts.y);
	float L = texture(sampler, texCoord+du).x;
	float R = texture(sampler, texCoord-du).x;
	float U = texture(sampler, texCoord+dv).x;
	float D = texture(sampler, texCoord-dv).x;
	
	vec3 dU = vec3(2, R-L, 0);
	vec3 dV = vec3(0, U-D, 2);
	
	return normalize(cross(dV, dU));
}

struct Stuff
{
	float stone;
	float gravel;
	float water;
	float sediment;
};

struct Flux
{
	float left;
	float right;
	float up;
	float down;
};

Stuff sampleStuff(sampler2D tex, vec2 tc)
{
	vec4 s = texture(tex, tc);
	return Stuff(s.x, s.y, s.z, s.w);
}

Flux sampleFlux(sampler2D tex, vec2 tc)
{
	vec4 s = texture(tex, tc);
	return Flux(s.x, s.y, s.z, s.w); 
}

vec4 toVec4(Stuff s)
{
	return vec4(s.stone, s.gravel, s.water, s.sediment);
}

vec4 toVec4(Flux f)
{
	return vec4(f.left, f.right, f.up, f.down);
}

void main()
{	
	Flux fCenter = sampleFlux(fluxTexture,   texCoord);
	Flux fLeft   = sampleFlux(fluxTexture,   texCoord - vec2(d, 0));
	Flux fRight  = sampleFlux(fluxTexture,   texCoord + vec2(d, 0));
	Flux fUp     = sampleFlux(fluxTexture,   texCoord + vec2(0, d));
	Flux fDown   = sampleFlux(fluxTexture,   texCoord - vec2(0, d));
	
	Stuff sCenter = sampleStuff(stuffTexture, texCoord);
	Stuff sLeft   = sampleStuff(stuffTexture, texCoord - vec2(d, 0));
	Stuff sRight  = sampleStuff(stuffTexture, texCoord + vec2(d, 0));
	Stuff sUp     = sampleStuff(stuffTexture, texCoord + vec2(0, d));
	Stuff sDown   = sampleStuff(stuffTexture, texCoord - vec2(0, d));
// Transport sediment.

	float sedimentIn = 0;
	float sedimentOut = 0; 

	if (sLeft.water > 0)  sedimentIn += fLeft.right * sLeft.sediment / sLeft.water;
	if (sRight.water > 0) sedimentIn += fRight.left * sRight.sediment / sRight.water;
	if (sUp.water > 0)    sedimentIn += fUp.down * sUp.sediment / sUp.water;
	if (sDown.water > 0)  sedimentIn += fDown.up * sDown.sediment / sDown.water;
	
	if (sCenter.water > 0) sedimentOut = (fCenter.left + fCenter.right + fCenter.up + fCenter.down)*sCenter.sediment/sCenter.water;
	float sedimentDiff = sedimentIn - sedimentOut;
	sCenter.sediment += dt*dt*sedimentDiff;

// Update Water Height.

	float waterIn  = fLeft.right + fRight.left + fUp.down + fDown.up;
	float waterOut = fCenter.left + fCenter.right + fCenter.up + fCenter.down;
	float dWaterVolume = dt * (waterIn - waterOut);
	
	float newWater = max(0, sCenter.water + dWaterVolume/(lu*lv)); 

	float dWu = 0.5*(fLeft.right - fCenter.left + fCenter.right - fRight.left);
	float dWv = 0.5*(fUp.down - fCenter.up + fCenter.down - fDown.up);
	
// Calculate Velocity.
	vec2 vel = vec2(0,0);
	
	float meanWater = (sCenter.water+newWater)*0.5;
	if (meanWater > 0)
	{
		float u = dWu/(lu*meanWater);
		float v = dWv/(lv*meanWater);	
		vel = vec2(u,v);
	}
	
// Erode and deposit
	vec3 normal = calculateNormal(stuffTexture, texCoord);
	float slope = (1.0 - dot(normal, vec3(0,1,0)));			
	float speed = length(vel);
	//float transportCapacity = kC*speed*slope*(1.0+sCenter.water); // includes water volume
	float transportCapacity = kC*speed*slope;  
	
	/*
	if (sCenter.sediment > 0) {
		float x = sCenter.sediment*0.05;
		sCenter.sediment -= x;
		sCenter.gravel += x;
	}
	*/
	
	if (transportCapacity > sCenter.sediment)
	{
		float dC = (transportCapacity - sCenter.sediment);
		float diff = kS*dC;
		
		if (diff > sCenter.gravel)
		{
			sCenter.sediment += sCenter.gravel;
			diff -= sCenter.gravel;
			sCenter.gravel = 0;
			
			float stoneFactor = pow(dot(vec4(1,1,1,0), texture(strataTexture, vec2(0,normalizingFactor*sCenter.stone)))/3.0, 0.5);
			sCenter.stone -= diff*stoneFactor;
			sCenter.sediment += diff*stoneFactor;
		}
		else
		{
			sCenter.gravel -= diff;
			sCenter.sediment += diff;
		}
	}
	else 
	{
		float dC = (sCenter.sediment - transportCapacity);
		sCenter.gravel += kD*dC;
		sCenter.sediment -= kD*dC;
	}
	
	
	sCenter.water = newWater;	
	outHeights = toVec4(sCenter);
	outFlux    = toVec4(fCenter);
	outVelocity = vec4(vel,0,0);
}