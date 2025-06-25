package graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import math.Vector2f;

public class Bitmap {
	private String path;
	public final int width;
	public final int height;
	public int[] pixels;

	public static Bitmap map_1 = new Bitmap("/textures/map_1.png", 100, 100);
	public static Bitmap map_2 = new Bitmap("/textures/map_2.png", 100, 100);
	public static Bitmap map_glass = new Bitmap("/textures/map_glass.png", 100, 100);
	public static Bitmap map_door_wall = new Bitmap("/textures/map_door_wall.png", 100, 100);
	public static Bitmap smily = new Bitmap("/textures/smily.png", 16, 16);
	public static Bitmap wall = new Bitmap("/textures/wall.png", 16, 16);
	public static Bitmap floor = new Bitmap("/textures/floor.png", 16, 16);
	public static Bitmap floor_2 = new Bitmap("/textures/floor_2.png", 16, 16);
	public static Bitmap glass = new Bitmap("/textures/glass.png", 16, 16);
	public static Bitmap door_wall = new Bitmap("/textures/door_wall.png", 16, 16);
	public static Bitmap door = new Bitmap("/textures/door.png", 16, 16);
	public static Bitmap leather = new Bitmap("/textures/leather.png", 32, 32);

	public static Bitmap skybox_left = new Bitmap("/textures/skybox/left.jpg", 2048, 2048);
	public static Bitmap skybox_right = new Bitmap("/textures/skybox/right.jpg", 2048, 2048);
	public static Bitmap skybox_front = new Bitmap("/textures/skybox/front.jpg", 2048, 2048);
	public static Bitmap skybox_back = new Bitmap("/textures/skybox/back.jpg", 2048, 2048);
	public static Bitmap skybox_top = new Bitmap("/textures/skybox/top.jpg", 2048, 2048);
	public static Bitmap skybox_bottom = new Bitmap("/textures/skybox/bottom.jpg", 2048, 2048);

	public Bitmap(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		load();
	}

	public int getColor(int xTexCoords, int yTexCoords) {
		if (xTexCoords >= 0 && xTexCoords < width && yTexCoords >= 0 && yTexCoords < height) {
			return pixels[xTexCoords + yTexCoords * width];
		}
		return 0xffff00ff;
	}

	public int getColor(float Xpos, float Ypos) {
		if (Xpos >= 0 && Xpos <= 1 && Ypos >= 0 && Xpos <= 1) {
			int xTexCoords = (int) Math.min(Xpos * width, width - 1);
			int yTexCoords = (int) Math.min(Ypos * height, height - 1);
			return pixels[xTexCoords + yTexCoords * width];
		}
		return 0xffff00ff;
	}

	public int getColor(Vector2f textureCoords) {
		if (textureCoords.Xpos >= 0 && textureCoords.Xpos <= 1 && textureCoords.Ypos >= 0 && textureCoords.Xpos <= 1) {
			int xTexCoords = (int) Math.min(textureCoords.Xpos * width, width - 1);
			int yTexCoords = (int) Math.min(textureCoords.Ypos * height, height - 1);
			return pixels[xTexCoords + yTexCoords * width];
		}
		return 0xffff00ff;
	}

	private void load() {
		try {
			BufferedImage image = ImageIO.read(Bitmap.class.getResource(path));
			int w = image.getWidth();
			int h = image.getHeight();
			image.getRGB(0, 0, w, h, pixels, 0, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
