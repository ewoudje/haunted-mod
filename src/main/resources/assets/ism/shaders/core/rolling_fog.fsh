#version 150

uniform vec3 uFogPosition;
uniform vec3 uFogNormal;
uniform vec3 uFogUp;
uniform sampler2D uFogTexture;
uniform sampler2D uFogShadowTexture;
uniform float uScroll;
uniform float uHeight;

#moj_import <ism:depth.glsl>
#define MAX_EXTENSION 20

out vec4 fragColor;

vec3 FogLeft = cross(uFogUp, uFogNormal);
vec3 CameraDirOnPlane = normalize((dot(uFogUp, CameraDir) * uFogUp) + (dot(FogLeft, CameraDir) * FogLeft));
float ToPlaneDistance = dot(uFogPosition - uCameraPos, uFogNormal) / dot(CameraDir, uFogNormal);
float DepthInFog = dot(uFogNormal, WorldPos - uFogPosition);
bool IsPlayerAbove = uCameraPos.y > uHeight;


float sampleTexture3(vec2 dPos) {
    float tex1 = texture(uFogTexture, dPos * 0.0004).r * 3;
    float tex2 = texture(uFogTexture, dPos * 0.0001).r * 8;
    float tex3 = texture(uFogTexture, dPos * 0.00001).r * 32;
    return min(abs(tex1 + tex2 - tex3 + 2), MAX_EXTENSION);
}

float getFogExtension(vec3 planePos) {
    vec2 dPos = vec2(dot(FogLeft, planePos), dot(uFogUp, planePos) + uScroll);
    return sampleTexture3(dPos);
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

void outsideOnTop() {
    float toFlatPlaneDist = (uFogPosition.y - uCameraPos.y) / CameraDir.y;
    if (toFlatPlaneDist < 0.0) discard;
    vec3 inWorldPos = (toFlatPlaneDist * CameraDir) + uCameraPos;

    float fogExtension = sampleTexture3(inWorldPos.xz);
    fogExtension = 14 - clamp(fogExtension, 0, 14);
    float grey = ((fogExtension / 14) * 0.15) + 0.45;
    fragColor = vec4(grey, grey, grey + 0.07, 0.98);
}

void outsideFog() {
    if (ToPlaneDistance < 0.1 || ToPlaneDistance > 10000) discard;
    if (ToPlaneDistance > WorldDepth) discard;


    float secondPlaneDistance = dot(uFogPosition - uCameraPos - (uFogNormal * MAX_EXTENSION), uFogNormal) / dot(CameraDir, uFogNormal);
    vec3 onPlanePos; /* = (CameraDir * ToPlaneDistance) + uCameraPos;
    if (onPlanePos.y >= uHeight) {
        outsideOnTop();
        return;
    }*/


    //vec3 testPos = WorldDir * depth;
    vec3 samplePos;
    float distFromPlane;
    float firstFogExtension = -1000;
    float fogExtension;
    float sampleLength;

    float sampleRangeStart = max(ToPlaneDistance, 0);
    float sampleRangeSize = min(secondPlaneDistance - sampleRangeStart, WorldDepth);

    const int sampleAmount = 40;
    const float invSampleAmount = 1.0 / float(sampleAmount);
    for (int i = 0; i < sampleAmount; i++)
    {
        sampleLength = (float(i) * sampleRangeSize * invSampleAmount) + sampleRangeStart;
        if (sampleLength > WorldDepth) break;

        samplePos = (CameraDir * sampleLength) + uCameraPos;

        // When we are looking the max height we shouldn't care anymore
        //if (samplePos.y >= uHeight) discard;

        distFromPlane = dot(uFogNormal, samplePos - uFogPosition);
        onPlanePos = samplePos - (distFromPlane * uFogNormal);
        fogExtension = getFogExtension(onPlanePos);

        if (distFromPlane < -fogExtension) {
            break;
        }
    }

    fogExtension = 14 - clamp(fogExtension, 0, 14);
    float grey = ((fogExtension / 14) * 0.15) + 0.45;
    float alpha = clamp(WorldDepth - sampleLength, 0, 10) / 10;
    fragColor = vec4(grey, grey, grey + 0.07, alpha);
}

void main() {
    float isInside = dot(uFogNormal, uCameraPos - uFogPosition);
    if (isInside > 0 || IsPlayerAbove)
        outsideFog();
    else
        insideFog(-isInside);
}