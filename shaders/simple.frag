#version 430

layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D strataTexture;
layout(binding = 2) uniform sampler2D detailTexture;

in vec2 vsTexCoord;
in vec3 vsNormal;

layout(location = 2) uniform float q;
layout(location = 3) uniform float yScale;

uniform float WATER_LIMIT = 0.4;
uniform float STONE_LIMIT = 0.8;

void main()
{
	float shade = dot(vsNormal, vec3(0,1,0));
	float h = texture(diffuseTexture, -vsTexCoord).r*yScale;
	float h2 = texture(diffuseTexture, vsTexCoord).r*yScale;
	vec4 c = texture(strataTexture, vec2(0,h*(1.0-q) + h2*q));
	float p = (c.r+c.g+c.z)/3.0;
	
	vec4 detail = vec4(0);
	float scale = 2;
	int N = 6;
	for (int i = 0; i < N; ++i)
	{
		detail += texture(detailTexture, vsTexCoord*scale);
		scale *= 2.0;
	}
	
	detail *= 1.0/float(N);
	
	gl_FragColor = detail*c*shade;
}
