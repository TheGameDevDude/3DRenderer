package worlds;

import java.util.ArrayList;
import java.util.List;
import entities.Couch;
import entities.Door;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.SkyBox;
import graphics.Bitmap;
import graphics.Render3D;
import input.Keyboard;
import input.Mouse;
import math.Vector3f;

public class World {
	private List<Entity> renderingEntities = new ArrayList<Entity>();
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Light> lights = new ArrayList<Light>();
	private List<Door> doors = new ArrayList<Door>();
	private List<Couch> couchs = new ArrayList<Couch>();
	private int renderingDistance = 10;

	public World(Render3D renderer) {
		lights.add(new Light(new Vector3f(15, 2, 10), 255, 150, 150));
		lights.add(new Light(new Vector3f(51.1f * 4, 2.0f, (100.0f - 43.1f) * 4), 150, 150, 255));
		doors.add(new Door(new Vector3f(46.0f * 4, 3.0f, ((100 - 43) * 4) - 3), 0));
		doors.add(new Door(new Vector3f(48.25f * 4, 3.0f, ((100 - 41) * 4) - 4), -90));
		doors.add(new Door(new Vector3f(44.25f * 4, 3.0f, ((100 - 41) * 4) - 4), -90));
		couchs.add(new Couch(new Vector3f(44.0f * 4, 1.0f, ((100 - 43) * 4)), 90, 3));
	}

	public void tick(Mouse mouse, Keyboard key, Player player) {
		entities.clear();
		otherObjectsTick(player, key);
		playerTick(mouse, key, player);
	}

	public void render(Render3D renderer, int[] screenPixels, Player player) {
		// SkyBox
		SkyBox.render(renderer, screenPixels, player.getCamera());
		renderer.clear();

		// Solid Map Entities
		renderingEntities.clear();
		Map.renderMap(4, player, renderingEntities, renderingDistance, Bitmap.map_1, 1.0f);
		Map.renderMap(4, player, renderingEntities, renderingDistance, Bitmap.map_2, 1.0f);
		Map.renderMap_door_wall(4, player, renderingEntities, renderingDistance, Bitmap.map_door_wall, 1.0f);
		renderingEntities.parallelStream().forEach(s -> s.renderXYZ(renderer, screenPixels, lights, player));

		// Solid Entities
		doors.forEach(s -> s.render(renderer, screenPixels, lights, player.getCamera()));
		couchs.forEach(s -> s.render(renderer, screenPixels, lights, player.getCamera()));

		// Transparency
		renderingEntities.clear();
		Map.renderMap_glass(4, player, renderingEntities, renderingDistance, Bitmap.map_glass, 1.0f);
		renderingEntities.stream().forEach(s -> s.renderXYZ(renderer, screenPixels, lights, player));

		renderer.clear();
		Render3D.updateRaycastPoint = false;
		// Render player/Other
		Render3D.updateRaycastPoint = true;
	}

	private void otherObjectsTick(Player player, Keyboard key) {
		doors.forEach(s -> s.tick(player, key, entities));
		couchs.forEach(s -> s.tick(player, entities));
	}

	private void playerTick(Mouse mouse, Keyboard key, Player player) {
		lights.get(0).position = new Vector3f(player.cameraPosition.Xpos, player.cameraPosition.Ypos, player.cameraPosition.Zpos);
		Map.getEntities(4, player, entities, Bitmap.map_1, 1.0f);
		Map.getEntities(4, player, entities, Bitmap.map_2, 1.0f);
		Map.getEntities(4, player, entities, Bitmap.map_glass, 1.0f);
		Map.getEntities_door_wall(4, player, entities, Bitmap.map_door_wall, 1.0f);
		player.tick(mouse, key, entities);
	}
}
