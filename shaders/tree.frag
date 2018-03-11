#version 430

layout(binding = 0) uniform sampler2D heightTexture;
layout(binding = 1) uniform sampler2D strataTexture;
layout(binding = 2) uniform sampler2D detailTexture;

in vec3 vsNormal;
in vec2 vsTexCoord;
in float vsHeight;

layout(location = 0) out vec4 fragmentColor;

layout(location = 3) uniform float normalizingFactor;

vec4 calculateDetailSample(sampler2D sampler, vec2 texCoord, int numSamples)
{
	vec4 detail = vec4(0);
	float scale = 2;
	for (int i = 0; i < numSamples; ++i)
	{
		detail += texture(sampler, texCoord*scale);
		scale *= 2.0;
	}
	
	return detail * 1.0/float(numSamples);
}

void main()
{

	vec4  samp = texture(heightTexture, vsTexCoord); 
	
	float stoneHeight = vsHeight;
	float waterHeight = samp.z;
	 
	float h = stoneHeight*normalizingFactor;
	
	vec4 stoneColor = texture(strataTexture, vec2(0,h));	
	//vec4 detailColor = calculateDetailSample(detailTexture, vsTexCoord, 6);
	vec4 detailColor = texture(detailTexture, vsTexCoord);	
	
	vec3 ldir = normalize(vec3(2,3,-1));
	float shade = 0.5 + max(0.0, dot(ldir, vsNormal));
	fragmentColor = detailColor*shade;//stoneColor * detailColor * shade;
	//fragmentColor = texture(detailTexture, vsTexCoord);
}
