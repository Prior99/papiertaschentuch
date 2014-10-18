uniform sampler2D uSampler;
uniform vec3 uPlayerPosition;

varying vec2 vTextureCoord;
varying vec3 normal;
varying vec3 vertex;

void main() {
	vec3 direction = normalize(vec3(gl_ModelViewMatrix * gl_LightSource[0].position) - vertex);
	vec3 negativeVectorDirection = normalize(-vertex);
	vec3 reflection = normalize(-reflect(direction, normal));

	vec4 diffuseLight = gl_FrontLightProduct[0].diffuse * max(dot(normal, direction), 0.0);
	diffuseLight = clamp(diffuseLight, 0.0, 1.0);

	vec4 specularLight = gl_FrontLightProduct[0].specular
		* pow(max(dot(reflection, negativeVectorDirection), 0.0), 0.3 * gl_FrontMaterial.shininess);
	specularLight = clamp(specularLight, 0.0, 1.0);
	vec3 lightColor = (diffuseLight + specularLight).rgb;
	vec4 textureColor = texture2D(uSampler, vTextureCoord);
	gl_FragColor = vec4(textureColor.rgb * lightColor, textureColor.a);
}
