#version 430

layout(binding = 0) uniform sampler2D heightTexture;
layout(binding = 1) uniform sampler2D strataTexture;
layout(binding = 2) uniform sampler2D detailTexture;
layout(binding = 3) uniform sampler2D waterTexture;
layout(binding = 4) uniform samplerCube skyboxTexture;
layout(binding = 5) uniform sampler2D velocityTexture;

in vec2 vsTexCoord;
in vec3 worldEyeDir;

void main()
{
	vec4  samp = texture(heightTexture, vsTexCoord); 
	
	float waterHeight = samp.z;
	
	float a = clamp(waterHeight, 0.0, 1.0);
		
	vec3 waterNormal = texture(waterTexture, vsTexCoord*8.0f).xyz;
	
	vec3 skyReflection = texture(skyboxTexture, reflect(worldEyeDir, waterNormal)).xyz;
	
	gl_FragColor = vec4(mix(skyReflection, vec3(0.25,0.5,1),0.5), a);
	
	/*
	a = 1;
	vec2 vel = texture(velocityTexture, vsTexCoord).xy;	
	float velScale = 1.0/30;
	gl_FragColor = vec4(0.5*(vec2(1,1) + vel*velScale), 0, a);
	*/	
}
