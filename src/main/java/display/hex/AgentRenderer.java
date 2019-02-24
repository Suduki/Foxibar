package display.hex;

import static org.lwjgl.opengl.GL11.glColor4f;

import org.joml.Vector3f;

import agents.Animal;
import agents.AnimalManager;
import constants.Constants;
import main.Main;
import plant.Plant;
import simulation.Simulation;

public class AgentRenderer extends TubeRenderer {

	Vector3f animalLowerPos;
	Vector3f animalUpperPos;

	private final float x0 = -Simulation.WORLD_SIZE_X / 2.0f;
	private final float z0 = -Simulation.WORLD_SIZE_Y / 2.0f;

	private final float xNudge = (float) (Math.sqrt(3.0f) * 0.2f);
	private final float zNudge = 3.0f / 9.0f;

	private final float xScale = (float) (Math.sqrt(3) * 0.5);
	private final float zScale = 1.5f;

	private TreeRenderer mTreeRenderer = null;

	public AgentRenderer() {
		super(Constants.Colors.BLACK, Constants.Colors.WHITE, 6, false, 0, true, false, true);
		animalLowerPos = new Vector3f();
		animalUpperPos = new Vector3f();

		mTreeRenderer = new TreeRenderer();
	}

	@Override
	protected void setColorForHeight(float scaledY) {
		if (scaledY < 0.5f) {
			scaledY *= 2;
			glColor4f((minColor[0] * scaledY + maxColor[0] * (1f - scaledY)),
					(minColor[1] * scaledY + maxColor[1] * (1f - scaledY)),
					(minColor[2] * scaledY + maxColor[2] * (1f - scaledY)),
					(minColor[3] * scaledY + maxColor[3] * (1f - scaledY)));
		} else {
			scaledY = (1f - scaledY) * 2;
			glColor4f((minColor[0] * scaledY + maxColor[0] * (1f - scaledY)),
					(minColor[1] * scaledY + maxColor[1] * (1f - scaledY)),
					(minColor[2] * scaledY + maxColor[2] * (1f - scaledY)),
					(minColor[3] * scaledY + maxColor[3] * (1f - scaledY)));
		}
	}

	public void drawAgents(float heightScale) {
		for (AnimalManager<?> manager : Main.mSimulation.animalManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Animal a = (Animal) manager.alive.get(i);
				if (a == null)
					break;

				float xLow = retrieveXFromWorldPos(a.pos.x - 1f);
				float x = retrieveXFromWorldPos(a.pos.x);
				float xHigh = retrieveXFromWorldPos(a.pos.x + 1f);

				float zLow = retrieveZFromWorldPos(a.pos.y - 1f);
				float z = retrieveZFromWorldPos(a.pos.y);
				float zHigh = retrieveZFromWorldPos(a.pos.y + 1f);

				xLow = (xLow + x) / 2;
				xHigh = (xHigh + x) / 2;
				zLow = (zLow + z) / 2;
				zHigh = (zHigh + z) / 2;

				float xLowness = 1f - a.pos.x % 1;
				float zLowness = 1f - a.pos.y % 1;

				float h = (float) Math.pow(Main.mSimulation.mWorld.terrain.height[(int) a.pos.x][(int) a.pos.y], 1.5)
						* heightScale;

				groundPos.set(xLow * xLowness + xHigh * (1f - xLowness), h, zLow * zLowness + zHigh * (1f - zLowness));
				if (a.pos.x >= Simulation.WORLD_SIZE_X - 1 || a.pos.x < 1) {
					groundPos.x = x;
				}
				if (a.pos.y >= Simulation.WORLD_SIZE_Y - 1 || a.pos.y < 1) {
					groundPos.z = z;
				}

				renderAgentAt(a);
			}
		}

		for (int i = 0; i < Main.mSimulation.plantManager.alive.size(); ++i) {
			Plant a = (Plant) Main.mSimulation.plantManager.alive.get(i);

			if (a == null)
				break;
			float xLow = retrieveXFromWorldPos(a.pos.x - 1f);
			float x = retrieveXFromWorldPos(a.pos.x);
			float xHigh = retrieveXFromWorldPos(a.pos.x + 1f);

			float zLow = retrieveZFromWorldPos(a.pos.y - 1f);
			float z = retrieveZFromWorldPos(a.pos.y);
			float zHigh = retrieveZFromWorldPos(a.pos.y + 1f);

			xLow = (xLow + x) / 2;
			xHigh = (xHigh + x) / 2;
			zLow = (zLow + z) / 2;
			zHigh = (zHigh + z) / 2;

			float xLowness = 1f - a.pos.x % 1;
			float zLowness = 1f - a.pos.y % 1;

			float h = (float) Math.pow(Main.mSimulation.mWorld.terrain.height[(int) a.pos.x][(int) a.pos.y], 1.5)
					* heightScale;

			groundPos.set(xLow * xLowness + xHigh * (1f - xLowness), h, zLow * zLowness + zHigh * (1f - zLowness));
			if (a.pos.x >= Simulation.WORLD_SIZE_X - 1 || a.pos.x < 1) {
				groundPos.x = x;
			}
			if (a.pos.y >= Simulation.WORLD_SIZE_Y - 1 || a.pos.y < 1) {
				groundPos.z = z;
			}

			mTreeRenderer.renderTreeAt(a, groundPos, x, z);
		}
	}

	@Override
	protected float heightToRadius(float h, float tubeMaxRadius) {
		return h * tubeMaxRadius;
	}

	private float retrieveXFromWorldPos(float x) {
		float hexX = (int) x / 2;
		float xpos = x0 + hexX * 2 * xScale + ((((int) x) % 2 == 0) ? -xNudge : xNudge);

		return xpos;
	}

	private float retrieveZFromWorldPos(float z) {
		float hexZ = (int) z / 2;
		float zpos = z0 + hexZ * zScale + ((((int) z) % 2 == 0) ? -zNudge : zNudge);

		return zpos;
	}

	void renderAgentAt(Animal a) {

		float[] c2 = a.secondaryColor;
		float[] c = a.color;

		setColor(c, c2, 1, 1);

		float scale = 0.7f;

		float height = (scale * (a.maxTall * a.size) * 10 + (1f - scale)) * 2f;
		float width = (scale * (a.maxSize * a.size) + (1f - scale)) * 0.6f;

		renderTube(groundPos, height, width, 0);
	}
}
