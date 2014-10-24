#define LIGHT_AMOUNT 8

uniform sampler2D uSampler;
uniform bool uDeactivateLighting;

uniform int uLightAmount;
uniform Light[64] uLights;
uniform Sun uSun;

varying vec2 vTextureCoord;
varying vec3 vNormal;
varying vec3 vVertex;

vec3 calculateSunLight() {
	vec3 direction = normalize(uSun.position - vVertex);
	vec3 negativeVectorDirection = normalize(-vVertex);
	vec3 reflection = normalize(-reflect(direction, vNormal));
	vec3 diffuseLight = uSun.color * max(dot(vNormal, direction), 0.0);
	diffuseLight = clamp(diffuseLight, 0.0, 1.0);
	vec4 specularLight = uSun.color
		* pow(max(dot(reflection, negativeVectorDirection), 0.0), 0.3 * gl_FrontMaterial.shininess);
	specularLight = clamp(specularLight, 0.0, 1.0);
	vec3 lightColor = (diffuseLight + specularLight.rgb);
	return lightColor;
}

vec3 calculateSingleSceneLight(Light light) {
	
	
}

vec3 calculateSceneLights() {
	vec3 lightColor = vec3(1.0, 1.0, 1.0);
	for(int i = 0; i < uLightAmount; i++) {
		lightColor *= calculateSingleSceneLight(uLights[i]);
		lightColor = clamp(lightColor, 0.0, 1.0);
	}
	return lightColor;
}

void main() {
	vec4 color = texture2D(uSampler, vTextureCoord);
	if(uDeactivateLighting) {
		gl_FragColor = color;
	}
	else {
		vec3 lightColor = calculateSunLight() * calculateSceneLights() * color.rgb;
		gl_FragColor = vec4(lightcolor, color.a);
	}
}
