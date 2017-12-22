package display;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import input.Mouse;

public class Camera implements CameraI {
	
	private Vector3f mTargetPos;
	private Vector3f mEyePos;
	private Vector3f mUp = new Vector3f(0,1,0);
	
	private float mFov;
	private float mNearZ;
	private float mFarZ;
	private float mAspectRatio;
	
	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;
	
	public void setFovRadians(float pFov) {
		mFov = pFov;
	}
	
	public void setFovDegrees(float fov) {
		mFov = fov*(float)Math.PI/180.0f;
	}
	
	public void setAspectRatio(float pAspectRatio) {
		mAspectRatio = pAspectRatio;
	}
	
	public void setNearZ(float pNearZ) {
		mNearZ = pNearZ;
	}
	
	public void setFarZ(float pFarZ) {
		mFarZ = pFarZ;
	}
	
	public void setEyePosition(Vector3f pEyePos) {
		mEyePos = pEyePos;
	}
	
	public void setTargetPosition(Vector3f pTargetPos) {
		mTargetPos = pTargetPos;
	}
	public Vector3f getTargetPosition() {
		return mTargetPos;
	}
	
	public Vector3f getEyePosition() {
		return mEyePos;
	}
	
	public float getFovRadians() {
		return mFov;
	}
	
	public float getFovDegrees() {
		return mFov*180.0f/(float)Math.PI;
	}
	
	public float getAspectRatio() {
		return mAspectRatio;
	}
	
	public float getNearZ() {
		return mNearZ;
	}
	
	public float getFarZ() {
		return mFarZ;
	}
	
	public Camera(float pFovDegrees, float pAspectRatio, float pNearZ, float pFarZ) {
		setFovDegrees(pFovDegrees);
		setAspectRatio(pAspectRatio);
		setNearZ(pNearZ);
		setFarZ(pFarZ);
		setTargetPosition(new Vector3f(0,0,0));
		setEyePosition(new Vector3f(0,3,-4));
		update();
	}
	
	public Camera() {
		this(70.0f, 1.0f, 0.1f, 1000.0f);
	}
	
	
	public void update() {
		mViewMatrix = new Matrix4f().lookAt(mEyePos, mTargetPos, mUp);
		mProjectionMatrix = new Matrix4f().perspective(mFov, mAspectRatio, mNearZ, mFarZ);
	}
	
	public Matrix4f getViewMatrix() {
		return mViewMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
		return mProjectionMatrix;
	}
}
