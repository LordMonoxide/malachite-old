#version 150 core

in vec4 vecColPass;

out vec4 vecTexOut;

void main(void) {
  vecTexOut = vecColPass;
}