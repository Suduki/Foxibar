#version 430

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 modelviewMatrix;
layout(location = 2) uniform vec4 worldEyePos;

layout(binding = 0) uniform sampler2D heightTexture;

out vec2 vsTexCoord;
out vec3 vsNormal;
out vec3 worldEyeDir;

vec4 calculateSample(sampler2D sampler, vec2 texCoord)
{
	ivec2 ts = textureSize(sampler, 0);
	vec2 du = vec2(2.0/ts.x, 0.0);
	vec2 dv = vec2(0.0,      2.0/ts.y);
	return (texture(sampler, texCoord) + texture(sampler, texCoord+du) + texture(sampler, texCoord-du) + texture(sampler, texCoord+dv) + texture(sampler, texCoord-dv))* (1.0/5.0);
}

void main()
{
	vec4 s = texture(heightTexture, texCoord);
	
	vec4 pos = vec4(position, 1);
	pos.y = s.x + s.y + s.z;
	
	
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsTexCoord = texCoord;
	worldEyeDir = -normalize((worldEyePos - pos).xyz);
}