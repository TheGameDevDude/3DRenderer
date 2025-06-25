package entities;

import java.util.ArrayList;
import java.util.List;

import graphics.Bitmap;
import graphics.Render3D;
import math.Vector3f;
import model.OBJLoader;

public class SkyBox {
	public static void render(Render3D renderer, int[] screenPixels, Camera camera) {
		List<Entity> entities = new ArrayList<Entity>();
		// right
		entities.add(new Entity(new Vector3f(camera.cameraPosition.Xpos + 1.0f, camera.cameraPosition.Ypos, camera.cameraPosition.Zpos), 90, 0, -90, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.skybox_right, Bitmap.skybox_right, null));
		// left
		entities.add(new Entity(new Vector3f(camera.cameraPosition.Xpos - 1.0f, camera.cameraPosition.Ypos, camera.cameraPosition.Zpos), 90, 0, 90, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.skybox_left, Bitmap.skybox_left, null));
		// front
		entities.add(new Entity(new Vector3f(camera.cameraPosition.Xpos, camera.cameraPosition.Ypos, camera.cameraPosition.Zpos + 1.0f), 90, 0, 0, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.skybox_front, Bitmap.skybox_front, null));
		// back
		entities.add(new Entity(new Vector3f(camera.cameraPosition.Xpos, camera.cameraPosition.Ypos, camera.cameraPosition.Zpos - 1.0f), -90, 180, 0, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.skybox_back, Bitmap.skybox_back, null));
		// top
		entities.add(new Entity(new Vector3f(camera.cameraPosition.Xpos, camera.cameraPosition.Ypos + 1, camera.cameraPosition.Zpos), 180, 0, 0, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.skybox_top, Bitmap.skybox_top, null));
		// bottom
		entities.add(new Entity(new Vector3f(camera.cameraPosition.Xpos, camera.cameraPosition.Ypos - 1, camera.cameraPosition.Zpos), 0, 0, 0, new Vector3f(1, 1, 1), OBJLoader.plane, Bitmap.skybox_bottom, Bitmap.skybox_bottom, null));
		entities.parallelStream().forEach(s -> s.renderXYZ(renderer, screenPixels, new ArrayList<Light>(), camera));
	}
}
