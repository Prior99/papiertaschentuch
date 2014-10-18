#define LIGHT_AMOUNT 8

uniform sampler2D uSampler;

varying vec2 vTextureCoord;
varying vec3 vNormal;
varying vec3 vVertex;
varying vec3[LIGHT_AMOUNT] vLightPositions;

void main() {
	gl_FragColor = texture2D(uSampler, vTextureCoord);
	/*vec3 direction = normalize(vec3(vLightPositions[0]) - vVertex);
	vec3 negativeVectorDirection = normalize(-vVertex);
	vec3 reflection = normalize(-reflect(direction, vNormal));

	vec4 diffuseLight = gl_FrontLightProduct[0].diffuse * max(dot(vNormal, direction), 0.0);
	diffuseLight = clamp(diffuseLight, 0.0, 1.0);

	vec4 specularLight = gl_FrontLightProduct[0].specular
		* pow(max(dot(reflection, negativeVectorDirection), 0.0), 0.3 * gl_FrontMaterial.shininess);
	specularLight = clamp(specularLight, 0.0, 1.0);
	vec3 lightColor = (diffuseLight + specularLight).rgb;
	vec4 textureColor = texture2D(uSampler, vTextureCoord);
	gl_FragColor = vec4(textureColor.rgb * lightColor, textureColor.a);*/
}
