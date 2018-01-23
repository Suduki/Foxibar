#version 430


layout(binding = 0) uniform sampler2D heightTexture;
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

const float kC = 0.001; // Water sediment capacity constant.
const float kS = 0.02; // Sediment solution constant.
const float kD = 0.005; // Sediment deposition constant;

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

void main()
{
	vec4 heights = texture(heightTexture, texCoord);
	
	vec4 fCenter = texture(fluxTexture,   texCoord);
	vec4 fLeft   = texture(fluxTexture,   texCoord - vec2(d, 0));
	vec4 fRight  = texture(fluxTexture,   texCoord + vec2(d, 0));
	vec4 fUp     = texture(fluxTexture,   texCoord + vec2(0, d));
	vec4 fDown   = texture(fluxTexture,   texCoord - vec2(0, d));

// Update Water Height.

	float waterIn  = fLeft.y + fRight.x + fUp.w + fDown.z;
	float waterOut = fCenter.x + fCenter.y + fCenter.z + fCenter.w;
	float dWaterVolume = dt * (waterIn - waterOut);
	
	float newWater = max(0, heights.z + dWaterVolume/(lu*lv)); 

	float dWu = 0.5*(fLeft.y - fCenter.x + fCenter.y - fRight.y);
	float dWv = 0.5*(fUp.w - fCenter.z + fCenter.w - fDown.z);
	
// Calculate Velocity.
	float meanWater = (heights.z+newWater)*0.5;
	float u = dWu/(lu*meanWater);
	float v = dWv/(lv*meanWater);

	vec2 vel = vec2(u,v);
	
// Erode and deposit
	vec3 normal = calculateNormal(heightTexture, texCoord);
	float slope = 1;//(1.0 - dot(normal, vec3(0,1,0)));			
	float speed = length(vel);
	float transportCapacity = kC*speed*slope*(1.0+heights.z); 
	if (transportCapacity > heights.w)
	{
		float dC = (transportCapacity - heights.w);
		float diff = kS*dC;
		
		if (diff > heights.y)
		{
			heights.w += heights.y;
			diff -= heights.y;
			heights.y = 0;
			
			float stoneFactor = pow(dot(vec4(1,1,1,0), texture(strataTexture, vec2(0,normalizingFactor*heights.x)))/3.0, 0.5);
			heights.x -= diff*stoneFactor;
			heights.w += diff*stoneFactor;
		}
		else
		{
			heights.y -= diff;
			heights.w += diff;
		}
	}
	else
	{
		float dC = (heights.w - transportCapacity);
		heights.y += kD*dC;
		heights.w -= kD*dC;
	}
	
	heights.z = newWater;	
	outHeights = heights;
	outFlux    = fCenter;
	outVelocity = vec4(vel,0,0);
}