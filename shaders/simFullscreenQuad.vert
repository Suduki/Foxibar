#version 430

out vec2 texCoord;

void main()
{
	const vec4 pos[6] = vec4[6](
		vec4(-1, -1, 0.5, 1),
		vec4( 1,  1, 0.5, 1),
		vec4(-1,  1, 0.5, 1),
		
		vec4(-1, -1, 0.5, 1),
		vec4( 1, -1, 0.5, 1),
		vec4( 1,  1, 0.5, 1));
		
		vec4 p = pos[gl_VertexID];
		texCoord = (p.xy + vec2(1,1))*0.5;
		gl_Position = p;
}