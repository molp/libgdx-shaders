uniform sampler2D u_texture;
uniform float u_blackWhite;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 final = v_color * texture2D(u_texture, v_texCoords);

    // luminance calculations
    float luminance = final.r * 0.299 + final.g * 0.587 + final.b * 0.114;
    vec4 bw = vec4(luminance, luminance, luminance, final.a);

    gl_FragColor = mix(final, bw, u_blackWhite);
}