#version 150 core

uniform mat4 matProj;
uniform mat4 matWorld;
uniform mat4 matTrans;

in vec4 vecPos;
in vec4 vecCol;
in vec2 vecTex;

out vec4 vecColPass;
out vec2 vecTexPass;

void main(void) {
  gl_Position = matProj * matWorld * matTrans * vecPos;
  vecColPass = vecCol;
  vecTexPass = vecTex;
}