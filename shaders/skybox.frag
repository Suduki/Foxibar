#version 430

layout(binding = 0) uniform samplerCube skyboxTexture;

in vec3 vsTexCoord;

void main()
{
	vec4 skySample = texture(skyboxTexture, normalize(vsTexCoord));
	
	gl_FragColor = skySample;	
}
