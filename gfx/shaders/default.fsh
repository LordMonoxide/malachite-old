#version 150 core

uniform sampler2D texture_diffuse;

in vec4 vecColPass;
in vec2 vecTexPass;

out vec4 vecTexOut;

void main(void) {
  vecTexOut = texture2D(texture_diffuse, vecTexPass) * vecColPass;
}