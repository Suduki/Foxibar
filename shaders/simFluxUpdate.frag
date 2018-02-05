#version 430

layout(binding = 0) uniform sampler2D substanceTexture;
layout(binding = 1) uniform sampler2D fluxTexture;

in vec2 texCoord;

layout(location = 0) uniform float d;
layout(location = 2) uniform float dt = 1.0/30.0;
layout(location = 3) uniform float rain = 0.0008;

layout(location = 0) out vec4 outHeights;
layout(location = 1) out vec4 outFlux;


const float A = 1.0; // Pipe cross-section.
const float g = 10.0; // Gravity.
const float pipeLength = 1; // Pipe length.

void main()
{
	vec4 sCenter = texture(substanceTexture, texCoord);
	
	/*
	sCenter += vec4(0,0,rain,0);
	*/
	if (length(texCoord-vec2(0.5)) < 0.25)
	{
	 	sCenter += vec4(0,0,rain,0);
	}
	
	vec4 sLeft   = texture(substanceTexture, texCoord - vec2(d, 0));
	vec4 sRight  = texture(substanceTexture, texCoord + vec2(d, 0));
	vec4 sUp     = texture(substanceTexture, texCoord + vec2(0, d));
	vec4 sDown   = texture(substanceTexture, texCoord - vec2(0, d));
	vec4 inFlux  = texture(fluxTexture, texCoord);
	
	float centerHeight = sCenter.x+sCenter.y+sCenter.z;
	
	vec4 h;
	h.x = centerHeight - (sLeft.x  + sLeft.y  + sLeft.z);
	h.y = centerHeight - (sRight.x + sRight.y + sRight.z);
	h.z = centerHeight - (sUp.x    + sUp.y    + sUp.z);
	h.w = centerHeight - (sDown.x  + sDown.y  + sDown.z);
	
	vec4 newFlux;
	newFlux.x = max(0, inFlux.x + dt*A*g*h.x/pipeLength);
	newFlux.y = max(0, inFlux.y + dt*A*g*h.y/pipeLength);
	newFlux.z = max(0, inFlux.z + dt*A*g*h.z/pipeLength);
	newFlux.w = max(0, inFlux.w + dt*A*g*h.w/pipeLength);
	
	float totalNewFlux = (newFlux.x + newFlux.y + newFlux.z + newFlux.w);
	
	if (dt*totalNewFlux > sCenter.z)
	{
		float K = clamp(sCenter.z / (dt*totalNewFlux), 0.0, 1.0);
		newFlux *= K;
	}	
	
	outHeights = sCenter;
	outFlux    = newFlux;
}