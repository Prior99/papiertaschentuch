uniform sampler2D uSampler;
varying vec2 vTextureCoord;
varying vec3 normal;
varying vec3 vertexToLight;

void main() {
	const vec4 DiffuseColor = vec4(1.0, 0.0, 0.0, 1.0);
	float brightness = clamp(dot(normal, vertexToLight), 0., 5.);
	vec4 texColor = texture2D(uSampler, vTextureCoord);
    gl_FragColor = vec4((texColor * brightness * 2.).rgb, texColor.a);

}