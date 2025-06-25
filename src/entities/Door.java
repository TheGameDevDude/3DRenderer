package entities;

import java.util.List;

import collision.AABB;
import graphics.Bitmap;
import graphics.Render3D;
import input.Keyboard;
import mainGameLoop.Main;
import math.Matrix4f;
import math.Vector3f;
import model.OBJLoader;

public class Door extends Entity {
	private float yaw;
	private float alpha;
	private boolean open;
	private boolean rotating;

	public Door(Vector3f position, float yaw) {
		super(position, 0, yaw, 0, new Vector3f(2, 2, 2), OBJLoader.door, Bitmap.door, null);
		this.yaw = yaw;
		alpha = 0.0f;
		open = false;
		rotating = false;
	}

	public void tick(Player player, Keyboard key, List<Entity> entities) {
		float distance = new Vector3f(player.playerPosition.Xpos - position.Xpos, 0, player.playerPosition.Zpos - position.Zpos).getMagnitude();
		if (distance <= 2.0f) {
			if (key.use == true && rotating == false) {
				open = !open;
				rotating = true;
			}
		}

		if (rotating == true && open == true) {
			alpha += Main.getFrameTime() * 0.1f;
		} else if (rotating == true && open == false) {
			alpha -= Main.getFrameTime() * 0.1f;
		}

		if (alpha >= 1.0f) {
			alpha = 1.0f;
			rotating = false;
		}

		if (alpha <= 0) {
			alpha = 0;
			rotating = false;
		}

		Yrot = yaw + 90 * alpha;

		Vector3f farLeft = new Vector3f(0, 2, 2).multiply(new Matrix4f().rotateY(Yrot));
		Vector3f nearRight = new Vector3f(0, -2, 0).multiply(new Matrix4f().rotateY(Yrot));
		farLeft.Xpos += position.Xpos;
		farLeft.Ypos += position.Ypos;
		farLeft.Zpos += position.Zpos;
		nearRight.Xpos += position.Xpos;
		nearRight.Ypos += position.Ypos;
		nearRight.Zpos += position.Zpos;
		entities.add(new Entity(new Vector3f(), 0, 0, 0, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.smily, new AABB(farLeft, nearRight)));
	}

	public void render(Render3D renderer, int[] screenPixels, List<Light> lights, Camera camera) {
		renderXYZ(renderer, screenPixels, lights, camera);
	}

}
