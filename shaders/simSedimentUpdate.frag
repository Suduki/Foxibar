#version 430


layout(binding = 0) uniform sampler2D heightTexture;
layout(binding = 1) uniform sampler2D velocityTexture;

in vec2 texCoord;

layout(location = 0) uniform float d;
layout(location = 2) uniform float dt = 1.0/30.0;
layout(location = 3) uniform float normalizingFactor;

layout(location = 0) out vec4 outHeights;
layout(location = 1) out vec4 outVelocity;

const float kE = 0.08; // Water evaporation constant

void main()
{
	vec4 heights  = texture(heightTexture,   texCoord);
	vec4 velocity = texture(velocityTexture, texCoord);
	
	vec4 h2 = texture(heightTexture, texCoord - dt*velocity.xy);
	
	heights.z *= (1.0-kE*dt);
	heights.w = h2.w;
	outHeights  = heights;
	outVelocity = velocity;
}