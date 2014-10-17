attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec3 aNormals;

varying vec2 vTextureCoord;
//varying vec3 vTransformedNormal;
varying vec4 vPosition;

void main() {
	vPosition = gl_ModelMatrix * vec4(aVertexPosition, 1.);
    gl_Position = gl_ProjectionMatrix * gl_ViewMatrix * gl_Vertex;
    vTextureCoord = aTextureCoord;
    //vTransformedNormal = uNormalMatrix * aNormals;
}