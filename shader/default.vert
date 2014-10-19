#define LIGHT_AMOUNT 8

attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec3 aNormals;

uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat4 uNormalMatrix;

varying vec2 vTextureCoord;
varying vec3 vNormal;
varying vec3 vVertex;
varying vec3[LIGHT_AMOUNT] vLightPositions;
void main() {
	mat4 modelView = uViewMatrix * uModelMatrix;
    gl_Position =  uProjectionMatrix * uViewMatrix * uModelMatrix * vec4(aVertexPosition, 1.);
    vTextureCoord = aTextureCoord;
	vNormal = normalize(vec3(uNormalMatrix * vec4(aNormals, 1.)));
	vVertex = vec3(uModelMatrix * vec4(aVertexPosition, 1.));
	for(int i = 0; i < LIGHT_AMOUNT; i++) {
		vLightPositions[i] = vec3(gl_LightSource[i].position);
	}
}
