#version 430

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 normal;

layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 viewMatrix;
layout(location = 3) uniform mat4 worldMatrix;

out vec2 vsTexCoord;
out vec3 vsNormal;

void main()
{
	gl_Position = projMatrix * viewMatrix * worldMatrix * vec4(position, 1);
	vsTexCoord = texCoord;
	vsNormal = normal;	
}