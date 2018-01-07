#version 430

layout(binding = 0) uniform sampler2D diffuseTexture;

in vec2 vsTexCoord;
in vec3 vsNormal;

void main()
{
	gl_FragColor = texture(diffuseTexture, vsTexCoord);
}
