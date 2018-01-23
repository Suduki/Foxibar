#version 430

layout(binding = 0) uniform sampler2D heightTexture;
layout(binding = 1) uniform sampler2D strataTexture;
layout(binding = 2) uniform sampler2D detailTexture;

in vec2 vsTexCoord;
in vec3 vsNormal;

layout(location = 3) uniform float normalizingFactor;

layout(location = 0) out vec4 fragmentColor;

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
	
	float stoneHeight = samp.x;
	float soilHeight  = samp.y;
	float waterHeight = samp.z;
	 
	float h = stoneHeight*normalizingFactor;
	
	vec4 stoneColor = texture(strataTexture, vec2(0,h));	
	vec4 detailColor = calculateDetailSample(detailTexture, vsTexCoord, 6);	
	vec4 landColor = detailColor * texture(detailTexture, vsTexCoord*16.0f) * vec4(210.0, 180.0, 140.0, 0.0) * 1.0/255.0;
	//vec4 landColor = detailColor * texture(detailTexture, vsTexCoord*16.0f) * vec4(256.0, 0.0, 0.0, 0.0) * 1.0/255.0;
	vec4 bottomColor = detailColor*stoneColor;
	
	float a = 0.0;
	float d = 0.25;
	if (soilHeight > 0.0)
	{
		a = 1.0;
	 	if (soilHeight < d)
	 	{
			a = (soilHeight/d);
		}
	}
	
	float shade = dot(normalize(vsNormal), normalize(vec3(1,2,0)));
	fragmentColor = mix(bottomColor, landColor, a) * shade;
	
	//float stoneFactor = dot(vec4(1,1,1,0), texture(strataTexture, vec2(0,normalizingFactor*stoneHeight)))/3.0;
	//fragmentColor = vec4(stoneFactor);
}
