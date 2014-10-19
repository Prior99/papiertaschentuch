attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec3 aNormals;

uniform mat4 uTranslationMatrix;

varying vec2 vTextureCoord;

void main() {
	gl_Position = uTranslationMatrix * vec4(aVertexPosition, 1.);
    vTextureCoord = aTextureCoord;
}