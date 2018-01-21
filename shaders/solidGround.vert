#version 430

layout(binding = 0) uniform sampler2D substanceTexture;

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;


layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 modelviewMatrix;

out vec2 vsTexCoord;
out vec3 vsNormal;
out float vsGroundHeight;

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
	vec4 s = texture(substanceTexture, texCoord);  
	vec4 pos = vec4(position, 1);
	pos.y = s.x + s.y;
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsTexCoord = texCoord;
	vsNormal = calculateNormal(substanceTexture, texCoord);
}