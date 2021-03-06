#version 430


layout(binding = 0) uniform sampler2D heightTexture;
layout(binding = 1) uniform sampler2D velocityTexture;
layout(binding = 2) uniform sampler2D fluxTexture;

in vec2 texCoord;

layout(location = 2) uniform float dt = 1.0/30.0;

layout(location = 0) out vec4 outHeights;
layout(location = 1) out vec4 outVelocity;

const float kE = 0.005; // Water evaporation constant

void main()
{
/*
// Flux based.
	vec4 heights  = texture(heightTexture,   texCoord);
	
	vec4 fCenter = texture(fluxTexture,   texCoord);
	vec4 fLeft   = texture(fluxTexture,   texCoord - vec2(d, 0));
	vec4 fRight  = texture(fluxTexture,   texCoord + vec2(d, 0));
	vec4 fUp     = texture(fluxTexture,   texCoord + vec2(0, d));
	vec4 fDown   = texture(fluxTexture,   texCoord - vec2(0, d));
	
	vec4 h2 = texture(heightTexture, texCoord - dt*velocity.xy);
	
	heights.z *= (1.0-kE*dt);
	heights.w = h2.w;
	outHeights  = heights;
	outVelocity = velocity;
*/

// Velocity based.
	vec4 heights  = texture(heightTexture,   texCoord);
	vec4 velocity = texture(velocityTexture, texCoord);
	
	vec4 h2 = texture(heightTexture, texCoord - dt*velocity.xy);
	
	heights.z *= (1.0-kE*dt);
//	heights.w = h2.w;
	outHeights  = heights;
	outVelocity = velocity;
}
