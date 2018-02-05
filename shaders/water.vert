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

float calculateSample(sampler2D sampler, vec2 texCoord, float minWater)
{
	ivec2 ts = textureSize(sampler, 0);
	vec2 du = vec2(2.0/ts.x, 0.0);
	vec2 dv = vec2(0.0,      2.0/ts.y);
	
	float h = 0;
	int i = 0;
	vec4 s0 = texture(sampler, texCoord+du);
	vec4 s1 = texture(sampler, texCoord-du);
	vec4 s2 = texture(sampler, texCoord+dv);
	vec4 s3 = texture(sampler, texCoord-dv);
	
	if (s0.z > minWater) { h += dot(s0,vec4(1,1,1,0)); i += 1; }
	if (s1.z > minWater) { h += dot(s1,vec4(1,1,1,0)); i += 1; }
	if (s2.z > minWater) { h += dot(s2,vec4(1,1,1,0)); i += 1; }
	if (s3.z > minWater) { h += dot(s3,vec4(1,1,1,0)); i += 1; }
	
	if (i > 0) {
		h /= float(h);
	}
	
	return h;
}

void main()
{
	vec4 s = texture(heightTexture, texCoord);
	
	vec4 pos = vec4(position, 1);
	
	if (s.z < 0.1)
	{
		pos.y = calculateSample(heightTexture, texCoord, 0.1);
	}
	else
	{
		pos.y = s.x + s.y + s.z;
	}
	
	
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsTexCoord = texCoord;
	worldEyeDir = -normalize((worldEyePos - pos).xyz);
}