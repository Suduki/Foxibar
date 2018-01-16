#version 430

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 modelviewMatrix;

layout(binding = 0) uniform sampler2D heightTexture;

out vec2 vsTexCoord;
out vec3 vsNormal;

void main()
{
	vec4 pos = vec4(position, 1);
	pos.y = texture(heightTexture, texCoord).x;  
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsTexCoord = texCoord;
	vsNormal = normal;
}