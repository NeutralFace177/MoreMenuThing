package io.bluestaggo.voxelthing.renderer.shader;

import io.bluestaggo.voxelthing.renderer.shader.uniform.ShaderUniform;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

public class CloudShader extends Shader {
    public final ShaderUniform<Matrix4f> viewProj;
    public final ShaderUniform<Vector3f> camPos;
    public final ShaderUniform<Vector4f> uvRange;
    public final ShaderUniform<Float> ticks;
    public final ShaderUniform<Float> cloudHeight;

    public final ShaderUniform<Integer> tex;
    public final ShaderUniform<Float> camFar;

    public CloudShader() throws IOException {
        super("/assets/shaders/cloud");
        use();
        viewProj = getUniformMatrix4fv("viewProj");
        camPos = getUniform3f("camPos");
        (uvRange = getUniform4f("uvRange")).set(new Vector4f(0.0f, 0.0f, 0.25f, 0.25f));
        ticks = getUniform1f("ticks");
        cloudHeight = getUniform1f("cloudHeight");

        (tex = getUniform1i("tex")).set(0);
        camFar = getUniform1f("camFar");
        stop();
    }
}
