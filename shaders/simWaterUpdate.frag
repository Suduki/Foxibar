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
const float kE = 0.05; // Evaporation constant

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

	float dWu = (fLeft.y - fCenter.x + fCenter.y - fRight.y);
	float dWv = (fUp.w - fCenter.z + fCenter.w - fDown.z);
	
// Calculate Velocity.
	float meanWater = (heights.z+newWater)*0.5;
	float u = dWu/(lu*meanWater);
	float v = dWv/(lv*meanWater);

	vec2 vel = vec2(u,v);
	
// Erode
	float K = 0.0001;

	if (newWater > 0)
	{
		float stoneHeight = heights.x;
		float h = stoneHeight*normalizingFactor;	
		vec4 stoneColor = texture(strataTexture, vec2(0,h));
		float stoneStrength = pow((stoneColor.r+stoneColor.g+stoneColor.b)/3.0, 2.0);
		
		float waterSpeed = length(vel);
		//heights.x -= stoneStrength*K*(1.0-(1.0/(1.0+waterSpeed)));
		heights.x -= stoneStrength*K*waterSpeed*meanWater;
	}
	
	heights.z = newWater*(1.0-kE*dt);	
	outHeights = heights;
	outFlux    = fCenter;
	outVelocity = vec4(vel,0,0);
}