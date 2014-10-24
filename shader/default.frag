struct Light {
	vec3 position;
	vec3 color;
	bool enabled;
	float strength;
};

struct Sun {
	vec3 position;
	vec3 color;
	vec3 ambientColor;
};

uniform sampler2D uSampler;
uniform bool uDeactivateLighting;

uniform int uLightAmount;
uniform Light[64] uLights;
uniform Sun uSun;

varying vec2 vTextureCoord;
varying vec3 vNormal;
varying vec3 vVertex;

void calculateSunLight(out vec3 lightColor) {
	vec3 direction = normalize(uSun.position - vVertex);
	vec3 negativeVectorDirection = normalize(-vVertex);
	vec3 reflection = normalize(-reflect(direction, vNormal));
	vec3 diffuseLight = uSun.color * abs(dot(vNormal, direction));
	lightColor = clamp(diffuseLight, 0.0, 1.0);
}

void calculateSingleSceneLight(in Light light, out vec3 lightColor) {
	vec3 direction = light.position - vVertex;
	vec3 diffuseLight = light.color * (max(light.strength - length(direction), 0.0) / light.strength);
	vec3 negativeVectorDirection = normalize(-vVertex);
	vec3 reflection = normalize(-reflect(direction, vNormal));
	diffuseLight = clamp(diffuseLight, 0.0, 1.0);
	vec3 specularLight = light.color 
		* pow(max(dot(reflection, negativeVectorDirection), 0.0), 0.3 * gl_FrontMaterial.shininess);
	specularLight = clamp(specularLight, 0.0, 1.0);
	lightColor = (diffuseLight /*+ specularLight*/);
}

void calculateSceneLights(out vec3 lightColor) {
	lightColor = vec3(0.0, 0.0, 0.0);
	for(int i = 0; i < uLightAmount; i++) {
		if(uLights[i].enabled) {
			vec3 singleLightColor;
			calculateSingleSceneLight(uLights[i], singleLightColor);
			lightColor += singleLightColor;
			lightColor = clamp(lightColor, 0.0, 1.0);
		}
	}
}

void main() {
	vec4 color = texture2D(uSampler, vTextureCoord);
	if(uDeactivateLighting) {
		gl_FragColor = color;
	}
	else {
		vec3 sunColor, sceneLightColor;
		calculateSunLight(sunColor);
		calculateSceneLights(sceneLightColor);
		vec3 lightColor = (sunColor + sceneLightColor + uSun.ambientColor)* color.rgb;
		gl_FragColor = vec4(clamp(lightColor, 0.0, 1.0), color.a);
	}
}
