package display;

import org.joml.Matrix4f;

public interface CameraI {
	void update();
	Matrix4f getViewMatrix();
	Matrix4f getProjectionMatrix();
}
