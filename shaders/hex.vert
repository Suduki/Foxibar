#version 430

layout(binding = 0) uniform sampler2D substanceTexture;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inNormal;
layout(location = 2) in vec4 inInstance;

layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 modelviewMatrix;

out vec3 vsNormal;
out vec2 vsTexCoord;
out float vsHeight;

void main()
{
	vec4 S = texture(substanceTexture, inInstance.zw);
	float height = S.x;
	vec4 pos = vec4(inPosition, 1);
	pos.x += inInstance.x;
	pos.z += inInstance.y;
	pos.y *= height;
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsNormal = inNormal;
	vsTexCoord = inInstance.zw;
	vsHeight = pos.y;
}