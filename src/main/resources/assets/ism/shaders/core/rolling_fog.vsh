#version 150

in vec3 Position;
in vec2 UV0;

out vec2 ScreenCoord;

void main() {
    gl_Position = vec4(Position.xy, 0., 1.);
    ScreenCoord = UV0;
}