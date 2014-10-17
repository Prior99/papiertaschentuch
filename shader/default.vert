uniform bool[8] uLightEnabled;
uniform vec3[8] uLightPositions;
uniform vec3[8] uLightColors;
uniform float[8] uLightStrengths;

varying vec2 vTextureCoord;
varying vec3 normal;
varying vec3 vertexToLight;
varying vec3[8] verticesToLights;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vTextureCoord = vec2(gl_MultiTexCoord0);
	normal = gl_NormalMatrix * gl_Normal;
	vertexToLight = vec3(gl_LightSource[0].position - gl_ModelViewMatrix * gl_Vertex);
}