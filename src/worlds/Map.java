package worlds;

import java.util.List;

import collision.AABB;
import entities.Entity;
import entities.Player;
import graphics.Bitmap;
import math.Vector3f;
import model.OBJLoader;

public class Map {
	public static void getEntities(int size, Player player, List<Entity> entities, Bitmap map, float mapHeight) {
		int grid_Xpos = (int) Math.floor(player.cameraPosition.Xpos / size);
		int grid_Zpos = map.height - (int) Math.floor(player.cameraPosition.Zpos / size);
		for (int z = grid_Zpos - 2; z <= grid_Zpos + 2; z++) {
			if (z < 0 || z >= map.height) {
				continue;
			}
			for (int x = grid_Xpos - 2; x <= grid_Xpos + 2; x++) {
				if (x < 0 || x >= map.width) {
					continue;
				}

				int color = map.pixels[x + z * map.width];

				float Xpos = x * size + size / 2;
				float Zpos = (map.height - z - 1) * size + size / 2;

				Vector3f sizeVector = new Vector3f(size, size, size);
				sizeVector.scale(0.5f);

				switch (color) {
				case 0xff0000ff:// floor/ceiling
					entities.add(new Entity(new Vector3f(Xpos, mapHeight, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 0.0f, -1.0f), new Vector3f(-1.0f, -0.0f, 1.0f))));
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 0.0f, -1.0f), new Vector3f(-1.0f, 0.0f, 1.0f))));
					break;
				case 0xffff0000:// left
					entities.add(new Entity(new Vector3f(Xpos - size / 2 - 0.05f, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, -1.0f))));
					break;
				case 0xff00ff00:// right
					entities.add(new Entity(new Vector3f(Xpos + size / 2 + 0.05f, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, -1.0f))));
					break;
				case 0xffffff00:// back
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2 - 0.05f), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(1.0f, -1.0f, 0.0f))));
					break;
				case 0xff00ffff:// front
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2 + 0.05f), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(1.0f, -1.0f, 0.0f))));
					break;
				case 0xff000000:// left/back
					entities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, -1.0f))));
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(1.0f, -1.0f, 0.0f))));
					break;
				case 0xffffffff:// right/back
					entities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, -1.0f))));
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(1.0f, -1.0f, 0.0f))));
					break;
				case 0xff333333:// front/right
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(1.0f, -1.0f, 0.0f))));
					entities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, -1.0f))));
					break;
				case 0xff7f7f7f:// front/left
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(1.0f, -1.0f, 0.0f))));
					entities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, -1.0f))));
					break;
				case 0xff428bf3:// floor
					entities.add(new Entity(new Vector3f(Xpos, mapHeight, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 0.0f, -1.0f), new Vector3f(-1.0f, -0.0f, 1.0f))));
					break;
				case 0xffcd8bf3:// ceiling
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 0.0f, -1.0f), new Vector3f(-1.0f, 0.0f, 1.0f))));
					break;
				case 0xff7f00ff:// floor/ceiling2
					entities.add(new Entity(new Vector3f(Xpos, mapHeight, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 0.0f, -1.0f), new Vector3f(-1.0f, -0.0f, 1.0f))));
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 0.0f, -1.0f), new Vector3f(-1.0f, 0.0f, 1.0f))));
					break;
				case 0xff822382:// box
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f))));
					break;
				}
			}
		}
	}

	public static void renderMap(int size, Player player, List<Entity> renderingEntities, int renderingDistance, Bitmap map, float mapHeight) {
		int grid_Xpos = (int) Math.floor(player.cameraPosition.Xpos / size);
		int grid_Zpos = map.height - (int) Math.floor(player.cameraPosition.Zpos / size);
		for (int z = grid_Zpos - renderingDistance; z < grid_Zpos + renderingDistance; z++) {
			if (z < 0 || z >= map.height) {
				continue;
			}
			for (int x = grid_Xpos - renderingDistance; x < grid_Xpos + renderingDistance; x++) {
				if (x < 0 || x >= map.width) {
					continue;
				}
				int color = map.pixels[x + z * map.width];

				float Xpos = x * size + size / 2;
				float Zpos = (map.height - z - 1) * size + size / 2;

				Vector3f sizeVector = new Vector3f(size, size, size);
				sizeVector.scale(0.5f);

				switch (color) {
				case 0xff0000ff:// floor/ceiling
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.floor, null));
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size, Zpos), 180, 0, 0, sizeVector, OBJLoader.plane, Bitmap.floor, null));
					break;
				case 0xffff0000:// left
					renderingEntities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 90, 0, -90, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xff00ff00:// right
					renderingEntities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 90, 0, 90, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xffffff00:// back
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 90, 0, 0, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xff00ffff:// front
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), -90, 180, 0, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xff000000:// left/back
					renderingEntities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 90, 0, -90, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 90, 0, 0, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xffffffff:// right/back
					renderingEntities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 90, 0, 90, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 90, 0, 0, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xff333333:// front/right
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), -90, 180, 0, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					renderingEntities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 90, 0, 90, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xff7f7f7f:// front/left
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), -90, 180, 0, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					renderingEntities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 90, 0, -90, sizeVector, OBJLoader.plane, Bitmap.wall, null));
					break;
				case 0xff428bf3:// floor
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.floor_2, null));
					break;
				case 0xffcd8bf3:// ceiling
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size, Zpos), 180, 0, 0, sizeVector, OBJLoader.plane, Bitmap.floor, null));
					break;
				case 0xff7f00ff:// floor/ceiling2
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.floor_2, null));
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size, Zpos), 180, 0, 0, sizeVector, OBJLoader.plane, Bitmap.floor, null));
					break;
				}
			}
		}
	}

	public static void renderMap_glass(int size, Player player, List<Entity> renderingEntities, int renderingDistance, Bitmap map, float mapHeight) {
		int grid_Xpos = (int) Math.floor(player.cameraPosition.Xpos / size);
		int grid_Zpos = map.height - (int) Math.floor(player.cameraPosition.Zpos / size);
		for (int z = grid_Zpos - renderingDistance; z < grid_Zpos + renderingDistance; z++) {
			if (z < 0 || z >= map.height) {
				continue;
			}
			for (int x = grid_Xpos - renderingDistance; x < grid_Xpos + renderingDistance; x++) {
				if (x < 0 || x >= map.width) {
					continue;
				}
				int color = map.pixels[x + z * map.width];

				float Xpos = x * size + size / 2;
				float Zpos = (map.height - z - 1) * size + size / 2;

				Vector3f sizeVector = new Vector3f(size, size, size);
				sizeVector.scale(0.5f);

				switch (color) {
				case 0xffff0000:// left
					renderingEntities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 90, 0, -90, sizeVector, OBJLoader.plane, Bitmap.wall, Bitmap.glass, true, null));
					break;
				case 0xff00ff00:// right
					renderingEntities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 90, 0, 90, sizeVector, OBJLoader.plane, Bitmap.wall, Bitmap.glass, true, null));
					break;
				case 0xffffff00:// back
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 90, 0, 0, sizeVector, OBJLoader.plane, Bitmap.wall, Bitmap.glass, true, null));
					break;
				case 0xff00ffff:// front
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), -90, 180, 0, sizeVector, OBJLoader.plane, Bitmap.wall, Bitmap.glass, true, null));
					break;
				}
			}
		}
	}

	public static void renderMap_door_wall(int size, Player player, List<Entity> renderingEntities, int renderingDistance, Bitmap map, float mapHeight) {
		int grid_Xpos = (int) Math.floor(player.cameraPosition.Xpos / size);
		int grid_Zpos = map.height - (int) Math.floor(player.cameraPosition.Zpos / size);
		for (int z = grid_Zpos - renderingDistance; z < grid_Zpos + renderingDistance; z++) {
			if (z < 0 || z >= map.height) {
				continue;
			}
			for (int x = grid_Xpos - renderingDistance; x < grid_Xpos + renderingDistance; x++) {
				if (x < 0 || x >= map.width) {
					continue;
				}
				int color = map.pixels[x + z * map.width];

				float Xpos = x * size + size / 2;
				float Zpos = (map.height - z - 1) * size + size / 2;

				Vector3f sizeVector = new Vector3f(size, size, size);
				sizeVector.scale(0.5f);

				switch (color) {
				case 0xffff0000:// left
					renderingEntities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 90, 0, -90, sizeVector, OBJLoader.plane, Bitmap.door_wall, null));
					break;
				case 0xff00ff00:// right
					renderingEntities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 90, 0, 90, sizeVector, OBJLoader.plane, Bitmap.door_wall, null));
					break;
				case 0xffffff00:// back
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 90, 0, 0, sizeVector, OBJLoader.plane, Bitmap.door_wall, null));
					break;
				case 0xff00ffff:// front
					renderingEntities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), -90, 180, 0, sizeVector, OBJLoader.plane, Bitmap.door_wall, null));
					break;
				}
			}
		}
	}

	public static void getEntities_door_wall(int size, Player player, List<Entity> entities, Bitmap map, float mapHeight) {
		int grid_Xpos = (int) Math.floor(player.cameraPosition.Xpos / size);
		int grid_Zpos = map.height - (int) Math.floor(player.cameraPosition.Zpos / size);
		for (int z = grid_Zpos - 2; z <= grid_Zpos + 2; z++) {
			if (z < 0 || z >= map.height) {
				continue;
			}
			for (int x = grid_Xpos - 2; x <= grid_Xpos + 2; x++) {
				if (x < 0 || x >= map.width) {
					continue;
				}

				int color = map.pixels[x + z * map.width];

				float Xpos = x * size + size / 2;
				float Zpos = (map.height - z - 1) * size + size / 2;

				Vector3f sizeVector = new Vector3f(size, size, size);
				sizeVector.scale(0.5f);

				switch (color) {
				case 0xffff0000:// left
					entities.add(new Entity(new Vector3f(Xpos - size / 2, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, -1.0f), new Vector3f(0, -1.0f, -0.5f))));
					break;
				case 0xff00ff00:// right
					entities.add(new Entity(new Vector3f(Xpos + size / 2, mapHeight + size / 2, Zpos), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(0, 1.0f, 1.0f), new Vector3f(0, -1.0f, 0.5f))));
					break;
				case 0xffffff00:// back
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos - size / 2), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(-1.0f, 1.0f, 0), new Vector3f(-0.5f, -1.0f, 0.0f))));
					break;
				case 0xff00ffff:// front
					entities.add(new Entity(new Vector3f(Xpos, mapHeight + size / 2, Zpos + size / 2), 0, 0, 0, sizeVector, OBJLoader.plane, Bitmap.smily, new AABB(new Vector3f(1.0f, 1.0f, 0), new Vector3f(0.5f, -1.0f, 0.0f))));
					break;

				}
			}
		}
	}
}
