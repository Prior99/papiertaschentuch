uniform sampler2D uSampler;
varying vec2 vTextureCoord;
varying vec3 normal;
varying vec3 vertexToLight;

void main() {
	const float lightStrength = 25.;
	vec4 texColor = texture2D(uSampler, vTextureCoord);
	vec3 color = texColor.rgb;
	color *= 1. - (length(vertexToLight)/lightStrength);
	color *= clamp(dot(normal, vertexToLight), 0., 1.);
	gl_FragColor = vec4(color, texColor.a);
}