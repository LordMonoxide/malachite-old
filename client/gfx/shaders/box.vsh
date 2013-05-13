#version 150 core

uniform mat4 matProj;
uniform mat4 matWorld;
uniform mat4 matTrans;

in vec4 vecPos;
in vec4 vecCol;

out vec4 vecColPass;

void main(void) {
  gl_Position = matProj * matWorld * matTrans * vecPos;
  vecColPass = vecCol;
}