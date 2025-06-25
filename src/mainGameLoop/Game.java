package mainGameLoop;

import entities.Player;
import graphics.Render3D;
import input.Keyboard;
import input.Mouse;
import math.Vector3f;
import worlds.World;

public class Game {
	public int width;
	public int height;
	private Render3D renderer;
	private Player player;
	World level1;

	public Game(int width, int height) {
		this.width = width;
		this.height = height;
		renderer = new Render3D(width, height);
		player = new Player(new Vector3f(51.1f * 4, 2.0f, (100.0f - 43.1f) * 4));
		level1 = new World(renderer);
	}

	public void tick(Mouse mouse, Keyboard key) {
		key.tick();
		renderer.tick();
		level1.tick(mouse, key, player);
	}

	public void render(int[] screenPixels) {
		renderer.clear(screenPixels);
		level1.render(renderer, screenPixels, player);
	}
}
