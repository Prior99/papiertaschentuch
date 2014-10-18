varying vec2 vTextureCoord;
varying vec3 normal;
varying vec3 vertex;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vTextureCoord = vec2(gl_MultiTexCoord0);
	normal = normalize(gl_NormalMatrix * gl_Normal);
	vertex = vec3(gl_ModelViewMatrix * gl_Vertex);
}
