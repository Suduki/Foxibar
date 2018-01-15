#version 430

layout(binding = 0) uniform sampler2D diffuseTexture;

in vec2 vsTexCoord;
in vec3 vsNormal;

void main()
{
	float shade = dot(vsNormal, vec3(0,1,0));
	gl_FragColor = texture(diffuseTexture, vsTexCoord) * shade;
}
