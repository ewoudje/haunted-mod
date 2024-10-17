#version 150

uniform vec3 uFogPosition;
uniform vec3 uFogNormal;
uniform vec3 uFogUp;
uniform sampler3D uFogTexture;
uniform sampler3D uFogShadowTexture;

uniform sampler2D uDepth;
uniform float uNearPlane;
uniform float uFarPlane;
uniform mat4 uInverseProjection;
uniform mat4 uInverseView;
uniform vec3 uCameraPos;


in vec2 ScreenCoord;
in vec3 WorldDir;

out vec4 fragColor;

vec3 FogLeft = cross(uFogUp, uFogNormal);


vec3 relpos() {
    float z = texture2D(uDepth, ScreenCoord).r * 2.0 - 1.0;
    vec4 clipSpacePosition = vec4(ScreenCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = uInverseProjection * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;
    vec4 worldSpacePosition = uInverseView * viewSpacePosition;

    return worldSpacePosition.xyz;
}
vec3 RelPos = relpos();
vec3 WorldPos = RelPos + uCameraPos;
vec3 CameraDir = normalize(RelPos);

float linearizeDepth(float d, float zNear, float zFar) {
    float clipZ = 2.0 * d - 1.0;
    return zNear * zFar / (zFar + zNear - clipZ * (zFar - zNear));
}

float getDepth() {
    float depth = texture2D(uDepth, ScreenCoord).r;

    return linearizeDepth(depth, uNearPlane, uFarPlane);
}

float sampleMixed(sampler3D sampler, vec3 pos) {
    float tex1 = texture(uFogShadowTexture, pos * 0.02).r;
    float tex2 = texture(uFogShadowTexture, pos * 0.01).r;
    float tex3 = texture(uFogShadowTexture, pos * 0.005).r;
    return tex1 + tex2 * 4 + tex3 * 16;
}

void insideFog(float distanceIn) {
    float lookingOustide = dot(CameraDir, uFogNormal);
    float alpha;
    if (lookingOustide < 0) {
        alpha = 0.99f;
    } else {
        distanceIn = clamp(distanceIn * 0.2, 0, 1);
        alpha = (1 - lookingOustide) * distanceIn * 0.99;
    }

    float depth = getDepth();
    depth /= 10f;
    depth = clamp(depth, 0, 1);
    fragColor = vec4(depth, depth, depth, alpha);
}

void setColorForPos(vec3 pos, float alpha) {
    float shadow = sampleMixed(uFogShadowTexture, pos);
    float grey = shadow * 0.55 + 0.40;
    fragColor = vec4(grey, grey, grey + 0.07f, alpha);
}

void outsideFog() {
    float depth = getDepth();


    //vec3 testPos = WorldDir * depth;
    float dist = dot(uFogNormal, WorldPos - uFogPosition);

    float planePosDistance = dot(uFogPosition - uCameraPos, uFogNormal) / dot(CameraDir, uFogNormal);
    vec3 planePos = CameraDir * planePosDistance + uCameraPos;

    if (dist > 0) {
        planePos = WorldPos;
    }

    float fogDepth = sampleMixed(uFogTexture, planePos);
    fogDepth += 0.5;
    fogDepth = clamp(fogDepth, 0, 20);

    //float fogDepth = getFogDepth(planePos);
    if (dist > fogDepth) discard;

    float alpha = 1.0f;
    if (dist > 0) {
        alpha = clamp((exp(1f - (dist / fogDepth)) / exp(1f)), 0, 1);
    }

    setColorForPos(planePos, alpha);
}

void main() {
    float isInside = dot(uFogNormal, uCameraPos - uFogPosition);
    if (isInside > 0) outsideFog(); else insideFog(-isInside);
}