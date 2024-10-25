in vec2 ScreenCoord;

uniform sampler2D uDepth;
uniform float uNearPlane;
uniform float uFarPlane;
uniform mat4 uInverseProjection;
uniform mat4 uInverseView;
uniform vec3 uCameraPos;

vec3 getRelPos() {
    float z = texture2D(uDepth, ScreenCoord).r * 2.0 - 1.0;
    vec4 clipSpacePosition = vec4(ScreenCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = uInverseProjection * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;
    vec4 worldSpacePosition = uInverseView * viewSpacePosition;

    return worldSpacePosition.xyz;
}

vec3 RelPos = getRelPos();
vec3 WorldPos = RelPos + uCameraPos;
float WorldDepth = length(RelPos);
vec3 CameraDir = RelPos / WorldDepth;
