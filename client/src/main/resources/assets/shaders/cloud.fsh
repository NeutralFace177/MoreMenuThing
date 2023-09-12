#version 330 core

in vec3 pos;
in vec2 uv;

out vec4 fColor;

uniform sampler2D tex;
// Fog
uniform vec3 camPos;
uniform float camFar;

float doFog(float fog) {
    return fog * 2.0 - 1.0;
}

void main() {
    vec4 cloudColor = texture(tex, uv);
    if (cloudColor.a == 0) discard;

    float fog = clamp(distance(pos, camPos) / camFar, 0.0, 1.0);
    fog = clamp(doFog(fog), 0.0, 1.0);
    fColor = vec4(cloudColor.xyz, cloudColor.a * (1.0 - fog));
}