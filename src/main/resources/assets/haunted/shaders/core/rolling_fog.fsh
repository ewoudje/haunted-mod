#version 150

uniform vec3 uFogPosition;
uniform vec3 uFogNormal;
uniform vec3 uFogUp;
uniform sampler2D uFogTexture;
uniform sampler2D uFogShadowTexture;
uniform float uScroll;

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
float WorldDepth = length(RelPos);
vec3 CameraDir = RelPos / WorldDepth;
float ToPlaneDistance = dot(uFogPosition - uCameraPos, uFogNormal) / dot(CameraDir, uFogNormal);

float linearizeDepth(float d, float zNear, float zFar) {
    float clipZ = 2.0 * d - 1.0;
    return zNear * zFar / (zFar + zNear - clipZ * (zFar - zNear));
}

float getDepth() {
    float depth = texture2D(uDepth, ScreenCoord).r;

    return linearizeDepth(depth, uNearPlane, uFarPlane);
}

float getFogExtension(vec3 planePos) {
    vec2 dPos = vec2(dot(FogLeft, planePos), dot(uFogUp, planePos) + uScroll);
    float tex1 = texture(uFogTexture, dPos * 0.0004).r * 3;
    float tex2 = texture(uFogTexture, dPos * 0.0001).r * 8;
    float tex3 = texture(uFogTexture, dPos * 0.00001).r * 32;
    return abs(tex1 + tex2 - tex3 + 2);
}

void insideFog(float distanceIn) {
    /*
    float lookingOustide = dot(CameraDir, uFogNormal);
    float alpha;
    if (lookingOustide < 0) {
        alpha = 0.99f;
    } else {
        distanceIn = clamp(distanceIn * 0.2, 0, 1);
        alpha = (1 - lookingOustide) * distanceIn * 0.99;
    }
*/

    float depth = WorldDepth;
    depth /= 10;
    depth = clamp(depth, 0, 1);
    fragColor = vec4(depth, depth, depth, 0.98);
}

void outsideFog() {
    if (ToPlaneDistance < 0.1 || ToPlaneDistance > 10000) discard;
    if ((ToPlaneDistance - 20) > WorldDepth) discard;

    //vec3 testPos = WorldDir * depth;
    vec3 samplePos;
    vec3 onPlanePos;
    float distFromPlane;
    float firstFogExtension = -1000;
    float fogExtension;
    float sampleLength;


    float sampleRangeStart = max(ToPlaneDistance - 20, 0);
    float sampleRangeSize = min(20, WorldDepth);

    const int sampleAmount = 50;
    const float invSampleAmount = 1.0 / float(sampleAmount);
    for (int i = 0; i < sampleAmount; i++)
    {
        sampleLength = (float(i) * sampleRangeSize * invSampleAmount) + sampleRangeStart;
        if (sampleLength > WorldDepth) break;

        samplePos = (CameraDir * sampleLength) + uCameraPos;
        distFromPlane = dot(uFogNormal, samplePos - uFogPosition);
        onPlanePos = samplePos - (distFromPlane * uFogNormal);
        fogExtension = getFogExtension(onPlanePos);

        if (distFromPlane < fogExtension) {
            break;
        }
    }

    if (distFromPlane > fogExtension) {
        if (ToPlaneDistance < WorldDepth) // If we just missed it by looking straight into it
            fogExtension = getFogExtension(CameraDir * ToPlaneDistance + uCameraPos);
        else discard;
    }

    float alpha = clamp(WorldDepth - sampleLength, 0, 10) / 10;

    fogExtension = clamp(fogExtension, 0, 14);
    float grey = ((fogExtension / 14) * 0.15) + 0.45;
    fragColor = vec4(grey, grey, grey + 0.07, alpha);
}

void main() {
    float isInside = dot(uFogNormal, uCameraPos - uFogPosition);
    if (isInside > 0) outsideFog(); else insideFog(-isInside);
}