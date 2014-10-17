varying vec2 vTextureCoord;
varying vec3 normal;
varying vec3 vertexToLight;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vTextureCoord = vec2(gl_MultiTexCoord0);
	normal = gl_NormalMatrix * gl_Normal;
	vertexToLight = normalize(vec3(gl_LightSource[0].position - gl_ModelViewMatrix * gl_Vertex));
}