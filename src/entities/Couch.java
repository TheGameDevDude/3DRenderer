package entities;

import java.util.List;

import collision.AABB;
import graphics.Bitmap;
import graphics.Render3D;
import math.Matrix4f;
import math.Vector3f;
import model.OBJLoader;

public class Couch extends Entity {
	private float distance = 0;

	public Couch(Vector3f position, float Yrot, int type) {
		super(position, 0, Yrot, 0, new Vector3f(1, 1, 1), null, Bitmap.leather, null);
		if (type == 1) {
			mesh = OBJLoader.couch1;
			Vector3f farLeft = new Vector3f(-0.55f, 0.25f, 0.5f).multiply(new Matrix4f().rotateY(Yrot));
			Vector3f nearRight = new Vector3f(0.55f, 0, -0.5f).multiply(new Matrix4f().rotateY(Yrot));
			farLeft.Xpos += position.Xpos;
			farLeft.Ypos += position.Ypos;
			farLeft.Zpos += position.Zpos;
			nearRight.Xpos += position.Xpos;
			nearRight.Ypos += position.Ypos;
			nearRight.Zpos += position.Zpos;
			aabb = new AABB(farLeft, nearRight);
		} else if (type == 2) {
			mesh = OBJLoader.couch2;
			Vector3f farLeft = new Vector3f(-1.0f, 0.25f, 0.5f).multiply(new Matrix4f().rotateY(Yrot));
			Vector3f nearRight = new Vector3f(1.0f, 0, -0.5f).multiply(new Matrix4f().rotateY(Yrot));
			farLeft.Xpos += position.Xpos;
			farLeft.Ypos += position.Ypos;
			farLeft.Zpos += position.Zpos;
			nearRight.Xpos += position.Xpos;
			nearRight.Ypos += position.Ypos;
			nearRight.Zpos += position.Zpos;
			aabb = new AABB(farLeft, nearRight);
		} else {
			mesh = OBJLoader.couch3;
			Vector3f farLeft = new Vector3f(-1.55f, 0.25f, 0.5f).multiply(new Matrix4f().rotateY(Yrot));
			Vector3f nearRight = new Vector3f(1.55f, 0, -0.5f).multiply(new Matrix4f().rotateY(Yrot));
			farLeft.Xpos += position.Xpos;
			farLeft.Ypos += position.Ypos;
			farLeft.Zpos += position.Zpos;
			nearRight.Xpos += position.Xpos;
			nearRight.Ypos += position.Ypos;
			nearRight.Zpos += position.Zpos;
			aabb = new AABB(farLeft, nearRight);
		}
	}

	public void tick(Player player, List<Entity> entities) {
		distance = new Vector3f(player.playerPosition.Xpos - position.Xpos, player.playerPosition.Ypos - position.Ypos, player.playerPosition.Zpos - position.Zpos).getMagnitude();
		if (distance <= 3) {
			entities.add(this);
		}
	}

	public void render(Render3D renderer, int[] screenPixels, List<Light> lights, Camera camera) {
		if (distance <= 25) {
			renderXYZ_Parallel(renderer, screenPixels, lights, camera);
		}
	}
}
