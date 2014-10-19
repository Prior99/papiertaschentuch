#define AMOUNT 16

uniform sampler2D uSampler;
uniform int uIndex;

varying vec2 vTextureCoord;

void main() {
	float step = 1./ AMOUNT;
	int row = uIndex / AMOUNT;
	int col = uIndex % AMOUNT;
	vec4 textureColor = texture2D(uSampler, vec2(vTextureCoord.x + col * step, 1. - vTextureCoord.y - row * step));
	gl_FragColor = textureColor;
}