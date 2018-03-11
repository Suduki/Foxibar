#version 430

layout(binding = 0) uniform sampler2D substanceTexture;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inNormal;
layout(location = 2) in vec2 inTexCoord;
layout(location = 3) in vec3 inInstance;
layout(location = 4) in vec2 inTex;

layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 modelviewMatrix;
layout(location = 4) uniform float flatness;

out vec3 vsNormal;
out vec2 vsTexCoord;
out float vsHeight;

void main()
{
	vec4 S = texture(substanceTexture, inTex.xy + inTexCoord*flatness);
	float height = S.x;
	vec4 pos = vec4(inPosition, 1);
	pos.x += inInstance.x;
	pos.z += inInstance.z;
	pos.y *= height;
	pos.y += inInstance.y;
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsNormal = inNormal;
	vsTexCoord = inTexCoord*0.99 + inTex.xy;
	vsHeight = pos.y;
}