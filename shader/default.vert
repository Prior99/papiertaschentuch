#define LIGHT_AMOUNT 8

varying vec2 vTextureCoord;
varying vec3 vNormal;
varying vec3 vVertex;
varying vec3[LIGHT_AMOUNT] vLightPositions;
void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vTextureCoord = vec2(gl_MultiTexCoord0);
	vNormal = normalize(gl_NormalMatrix * gl_Normal);
	vVertex = vec3(gl_ModelViewMatrix * gl_Vertex);
	//for(int i = 0; i < LIGHT_AMOUNT; i++) {
		vLightPositions[0] = vec3(gl_ModelViewMatrix * gl_LightSource[0].position);
	//}
}
