#version 430

layout(location = 0) uniform mat4 projMatrix;
layout(location = 1) uniform mat4 modelviewMatrix;

out vec3 vsTexCoord;

const vec3 vertices[] = {
	vec3(-1.0,-1.0,-1.0),
    vec3(-1.0,-1.0, 1.0),
    vec3(-1.0, 1.0, 1.0),
    vec3(1.0, 1.0,-1.0),
    vec3(-1.0,-1.0,-1.0),
    vec3(-1.0, 1.0,-1.0),
    vec3(1.0,-1.0, 1.0),
    vec3(-1.0,-1.0,-1.0),
    vec3(1.0,-1.0,-1.0),
    vec3(1.0, 1.0,-1.0),
    vec3(1.0,-1.0,-1.0),
    vec3(-1.0,-1.0,-1.0),
    vec3(-1.0,-1.0,-1.0),
    vec3(-1.0, 1.0, 1.0),
    vec3(-1.0, 1.0,-1.0),
    vec3(1.0,-1.0, 1.0),
    vec3(-1.0,-1.0, 1.0),
    vec3(-1.0,-1.0,-1.0),
    vec3(-1.0, 1.0, 1.0),
    vec3(-1.0,-1.0, 1.0),
    vec3(1.0,-1.0, 1.0),
    vec3(1.0, 1.0, 1.0),
    vec3(1.0,-1.0,-1.0),
    vec3(1.0, 1.0,-1.0),
    vec3(1.0,-1.0,-1.0),
    vec3(1.0, 1.0, 1.0),
    vec3(1.0,-1.0, 1.0),
    vec3(1.0, 1.0, 1.0),
    vec3(1.0, 1.0,-1.0),
    vec3(-1.0, 1.0,-1.0),
    vec3(1.0, 1.0, 1.0),
    vec3(-1.0, 1.0,-1.0),
    vec3(-1.0, 1.0, 1.0),
    vec3(1.0, 1.0, 1.0),
    vec3(-1.0, 1.0, 1.0),
    vec3(1.0,-1.0, 1.0)
};

void main()
{
	vec4 pos = vec4(vertices[gl_VertexID], 1);
	
	gl_Position = projMatrix * modelviewMatrix * pos;
	vsTexCoord = pos.xyz;
}