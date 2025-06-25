package graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import entities.Camera;
import entities.Light;
import mainGameLoop.Main;
import math.Clipping;
import math.Vector2f;
import math.Vector3f;
import math.Matrix4f;

public class Render3D extends Renderer {
	private static float NEAR = 0.01f;
	private static float FAR = 100f;
	private static float aspectRatio;
	private static double fov = 60;
	private static int clearColor = 0x428bf3;
	private double[] depthBuffer;
	public static Vector3f rayCastPoint = null;
	public static boolean isGlass = false;
	public static boolean updateRaycastPoint = true;

	public Render3D(int width, int height) {
		super(width, height);
		aspectRatio = (float) Main.widthResize / (float) Main.heightResize;

		depthBuffer = new double[width * height];
		for (int i = 0; i < depthBuffer.length; i++) {
			depthBuffer[i] = Float.MAX_VALUE;
		}
	}

	public void tick() {
		aspectRatio = (float) Main.widthResize / (float) Main.heightResize;
		fov = 90;
	}

	public void clear() {
		IntStream.range(0, width * height).parallel().forEach(s -> {
			depthBuffer[s] = Float.MAX_VALUE;
		});
	}

	public void clear(int[] screenPixels) {
		IntStream.range(0, width * height).parallel().forEach(s -> {
			screenPixels[s] = clearColor;
			depthBuffer[s] = Float.MAX_VALUE;
		});
	}

	public void gaussianBlur(int[] screenPixels) {
		int[] kernel = new int[9];
		kernel[0 + 0 * 3] = 1;
		kernel[1 + 0 * 3] = 2;
		kernel[2 + 0 * 3] = 1;
		kernel[0 + 1 * 3] = 2;
		kernel[1 + 1 * 3] = 4;
		kernel[2 + 1 * 3] = 2;
		kernel[0 + 2 * 3] = 1;
		kernel[1 + 2 * 3] = 2;
		kernel[2 + 2 * 3] = 1;

		int newWidth = width / 2;
		int newHeight = height / 2;

		int[] pixels = new int[newWidth * newHeight];
		float stepY = (float) height / (float) newHeight;
		float stepX = (float) width / (float) newWidth;
		float newY = 1;

		for (int y = 1; y < newHeight - 1; y++) {
			float newX = 1;
			for (int x = 1; x < newWidth - 1; x++) {
				int[] sample = new int[9];
				int Y = (int) Math.ceil(newY - 0.5f);
				int X = (int) Math.ceil(newX - 0.5f);
				sample[0 + 0 * 3] = screenPixels[(X - 1) + (Y - 1) * width];
				sample[1 + 0 * 3] = screenPixels[(X) + (Y - 1) * width];
				sample[2 + 0 * 3] = screenPixels[(X + 1) + (Y - 1) * width];
				sample[0 + 1 * 3] = screenPixels[(X - 1) + (Y) * width];
				sample[1 + 1 * 3] = screenPixels[(X) + (Y) * width];
				sample[2 + 1 * 3] = screenPixels[(X + 1) + (Y) * width];
				sample[0 + 2 * 3] = screenPixels[(X - 1) + (Y + 1) * width];
				sample[1 + 2 * 3] = screenPixels[(X) + (Y + 1) * width];
				sample[2 + 2 * 3] = screenPixels[(X + 1) + (Y + 1) * width];
				pixels[x + y * newWidth] = blur(sample, kernel);

				newX += stepX;
			}
			newY += stepY;
		}

		float stepScreenY = (float) newHeight / (float) height;
		float stepScreenX = (float) newWidth / (float) width;
		float newScreenY = 0;

		for (int y = 0; y < height; y++) {
			float newScreenX = 0;
			for (int x = 0; x < width; x++) {
				int X = (int) Math.ceil(newScreenX - 0.5f);
				int Y = (int) Math.ceil(newScreenY - 0.5f);
				screenPixels[x + y * width] = pixels[X + Y * newWidth];
				newScreenX += stepScreenX;
			}
			newScreenY += stepScreenY;
		}
	}

	public void bloom(int[] screenPixels) {
		int[] kernel = new int[9];
		kernel[0 + 0 * 3] = 1;
		kernel[1 + 0 * 3] = 2;
		kernel[2 + 0 * 3] = 1;
		kernel[0 + 1 * 3] = 2;
		kernel[1 + 1 * 3] = 4;
		kernel[2 + 1 * 3] = 2;
		kernel[0 + 2 * 3] = 1;
		kernel[1 + 2 * 3] = 2;
		kernel[2 + 2 * 3] = 1;

		int newWidth = width / 2;
		int newHeight = height / 2;

		int[] pixels = new int[newWidth * newHeight];
		float stepY = (float) height / (float) newHeight;
		float stepX = (float) width / (float) newWidth;
		float newY = 1;

		for (int y = 1; y < newHeight; y++) {
			float newX = 1;
			for (int x = 1; x < newWidth; x++) {
				int[] sample = new int[9];
				int Y = (int) Math.ceil(newY - 0.5f);
				int X = (int) Math.ceil(newX - 0.5f);
				sample[0 + 0 * 3] = brightFilter(screenPixels[(X - 1) + (Y - 1) * width]);
				sample[1 + 0 * 3] = brightFilter(screenPixels[(X) + (Y - 1) * width]);
				sample[2 + 0 * 3] = brightFilter(screenPixels[(X + 1) + (Y - 1) * width]);
				sample[0 + 1 * 3] = brightFilter(screenPixels[(X - 1) + (Y) * width]);
				sample[1 + 1 * 3] = brightFilter(screenPixels[(X) + (Y) * width]);
				sample[2 + 1 * 3] = brightFilter(screenPixels[(X + 1) + (Y) * width]);
				sample[0 + 2 * 3] = brightFilter(screenPixels[(X - 1) + (Y + 1) * width]);
				sample[1 + 2 * 3] = brightFilter(screenPixels[(X) + (Y + 1) * width]);
				sample[2 + 2 * 3] = brightFilter(screenPixels[(X + 1) + (Y + 1) * width]);
				pixels[x + y * newWidth] = blur(sample, kernel);
				newX += stepX;
			}
			newY += stepY;
		}

		float stepScreenY = (float) newHeight / (float) height;
		float stepScreenX = (float) newWidth / (float) width;
		float newScreenY = 0;

		for (int y = 0; y < height; y++) {
			float newScreenX = 0;
			for (int x = 0; x < width; x++) {
				int X = (int) Math.ceil(newScreenX - 0.25f);
				int Y = (int) Math.ceil(newScreenY - 0.25f);

				if (X < 0 || X >= newWidth) {
					continue;
				}

				if (Y < 0 || Y >= newHeight) {
					continue;
				}

				int R = (pixels[X + Y * newWidth] >> 16) & 0xff;
				int G = (pixels[X + Y * newWidth] >> 8) & 0xff;
				int B = (pixels[X + Y * newWidth]) & 0xff;

				int Rp = (screenPixels[x + y * width] >> 16) & 0xff;
				int Gp = (screenPixels[x + y * width] >> 8) & 0xff;
				int Bp = (screenPixels[x + y * width]) & 0xff;

				Vector3f pixelsColor = new Vector3f(R, G, B);
				Vector3f screenPixelsColor = new Vector3f(Rp, Gp, Bp);

				Vector3f result = new Vector3f(pixelsColor.Xpos + screenPixelsColor.Xpos, pixelsColor.Ypos + screenPixelsColor.Ypos, pixelsColor.Zpos + screenPixelsColor.Zpos);

				if (result.Xpos >= 255) {
					result.Xpos = 255;
				}

				if (result.Ypos >= 255) {
					result.Ypos = 255;
				}

				if (result.Zpos >= 255) {
					result.Zpos = 255;
				}

				int red = (int) result.Xpos;
				int green = (int) result.Ypos;
				int blue = (int) result.Zpos;

				screenPixels[x + y * width] = (red << 16) | (green << 8) | (blue);
				newScreenX += stepScreenX;
			}
			newScreenY += stepScreenY;
		}

	}

	private int blur(int[] color, int[] kernel) {
		int red = 0;
		int green = 0;
		int blue = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int R = (color[x + y * 3] >> 16) & 0xff;
				int G = (color[x + y * 3] >> 8) & 0xff;
				int B = (color[x + y * 3]) & 0xff;

				red += R * kernel[x + y * 3];
				green += G * kernel[x + y * 3];
				blue += B * kernel[x + y * 3];
			}
		}
		red /= 16;
		green /= 16;
		blue /= 16;

		return (red << 16) | (green << 8) | (blue);
	}

	private int brightFilter(int screenPixel) {
		int R = (screenPixel >> 16) & 0xff;
		int G = (screenPixel >> 8) & 0xff;
		int B = (screenPixel) & 0xff;

		Vector3f brightColor = new Vector3f((float) R, (float) G, (float) B);
		brightColor.scale(0.004f);
		float brightness = brightColor.Xpos * 0.2126f + brightColor.Ypos * 0.7152f + brightColor.Zpos * 0.0722f;
		brightColor.scale(brightness * 255);

		R = (int) brightColor.Xpos;
		G = (int) brightColor.Ypos;
		B = (int) brightColor.Zpos;

		return (R << 16) | (G << 8) | (B);
	}

	public void renderTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex aTranslate = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals), new Vector3f(a.modelPosition));
		Vertex bTranslate = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals), new Vector3f(b.modelPosition));
		Vertex cTranslate = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals), new Vector3f(c.modelPosition));

		// translate opposite to camera position(if the camera moves left the whole
		// world moves right to create an illusion that you move left)
		aTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		aTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		aTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		bTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		bTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		bTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		cTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		cTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		cTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		// same as translation we do rotation relative to camera
		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		// if the triangles are too far or behind the NEAR then don't render them
		if (aTranslate.position.Zpos <= NEAR && bTranslate.position.Zpos <= NEAR && cTranslate.position.Zpos <= NEAR) {
			return;
		} else if (aTranslate.position.Zpos >= FAR && bTranslate.position.Zpos >= FAR && cTranslate.position.Zpos >= FAR) {
			return;
		}

		Clipping clipping = new Clipping();
		Triangle triangle = new Triangle(aTranslate, bTranslate, cTranslate);
		// geometric clipping in clockwise
		clipping.clipNearZPlane(triangle, NEAR);
		for (Triangle tri1 : clipping.triZnear) {
			Triangle triangle1 = new Triangle(tri1.a, tri1.b, tri1.c);
			clipping.clipFarZPlane(triangle1, FAR);
			for (Triangle tri2 : clipping.triZfar) {
				rasterizeTriangle(tri2.a, tri2.b, tri2.c, texture, lights, screenPixels);
			}
		}
		clipping.triZnear.clear();
		clipping.triZfar.clear();
	}

	private void rasterizeTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, List<Light> lights, int[] screenPixels) {
		Vertex min = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals), new Vector3f(a.modelPosition));
		Vertex mid = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals), new Vector3f(b.modelPosition));
		Vertex max = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals), new Vector3f(c.modelPosition));

		min.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		mid.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		max.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));

		min.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		mid.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		max.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));

		min.position.Xpos /= min.position.Zpos;
		min.position.Ypos /= min.position.Zpos;

		mid.position.Xpos /= mid.position.Zpos;
		mid.position.Ypos /= mid.position.Zpos;

		max.position.Xpos /= max.position.Zpos;
		max.position.Ypos /= max.position.Zpos;

		min.position.shiftToScreenSpace(width, height);
		mid.position.shiftToScreenSpace(width, height);
		max.position.shiftToScreenSpace(width, height);

		// if the triangle is out of screen then don't render it
		if (min.position.Xpos >= width && mid.position.Xpos >= width && max.position.Xpos >= width) {
			return;
		} else if (min.position.Xpos <= 0 && mid.position.Xpos <= 0 && max.position.Xpos <= 0) {
			return;
		}

		if (min.position.Ypos >= height && mid.position.Ypos >= height && max.position.Ypos >= height) {
			return;
		} else if (min.position.Ypos <= 0 && mid.position.Ypos <= 0 && max.position.Ypos <= 0) {
			return;
		}

		min.texCoords.Xpos /= min.position.Zpos;
		min.texCoords.Ypos /= min.position.Zpos;

		mid.texCoords.Xpos /= mid.position.Zpos;
		mid.texCoords.Ypos /= mid.position.Zpos;

		max.texCoords.Xpos /= max.position.Zpos;
		max.texCoords.Ypos /= max.position.Zpos;

		min.normals.Xpos /= min.position.Zpos;
		mid.normals.Xpos /= mid.position.Zpos;
		max.normals.Xpos /= max.position.Zpos;

		min.normals.Ypos /= min.position.Zpos;
		mid.normals.Ypos /= mid.position.Zpos;
		max.normals.Ypos /= max.position.Zpos;

		min.normals.Zpos /= min.position.Zpos;
		mid.normals.Zpos /= mid.position.Zpos;
		max.normals.Zpos /= max.position.Zpos;

		min.modelPosition.Xpos /= min.position.Zpos;
		mid.modelPosition.Xpos /= mid.position.Zpos;
		max.modelPosition.Xpos /= max.position.Zpos;

		min.modelPosition.Ypos /= min.position.Zpos;
		mid.modelPosition.Ypos /= mid.position.Zpos;
		max.modelPosition.Ypos /= max.position.Zpos;

		min.modelPosition.Zpos /= min.position.Zpos;
		mid.modelPosition.Zpos /= mid.position.Zpos;
		max.modelPosition.Zpos /= max.position.Zpos;

		min.position.Zpos = 1 / min.position.Zpos;
		mid.position.Zpos = 1 / mid.position.Zpos;
		max.position.Zpos = 1 / max.position.Zpos;

		Vector2f left = new Vector2f(mid.position.Xpos - min.position.Xpos, mid.position.Ypos - min.position.Ypos);
		Vector2f right = new Vector2f(max.position.Xpos - min.position.Xpos, max.position.Ypos - min.position.Ypos);

		float backfaceCulling = left.Xpos * right.Ypos - left.Ypos * right.Xpos;

		if (backfaceCulling < 0) {
			return;
		}

		Vertex[] sort = new Vertex[3];

		sort[0] = new Vertex(new Vector3f(min.position), new Vector2f(min.texCoords), new Vector3f(min.normals), new Vector3f(min.modelPosition));
		sort[1] = new Vertex(new Vector3f(mid.position), new Vector2f(mid.texCoords), new Vector3f(mid.normals), new Vector3f(mid.modelPosition));
		sort[2] = new Vertex(new Vector3f(max.position), new Vector2f(max.texCoords), new Vector3f(max.normals), new Vector3f(max.modelPosition));

		// sorting according to the y position for rasterization
		for (int i = 0; i < sort.length - 1; i++) {
			for (int j = i + 1; j < sort.length; j++) {
				if (sort[j].position.Ypos < sort[i].position.Ypos) {
					Vertex temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
				}
			}
		}

		min = sort[0];
		mid = sort[1];
		max = sort[2];

		float alpha = (mid.position.Ypos - min.position.Ypos) / (max.position.Ypos - min.position.Ypos);
		Vector3f interpolatedVector = new Vector3f().interpolate(min.position, max.position, alpha);
		Vector2f interpolatedTexCoords = new Vector2f().interpolate(min.texCoords, max.texCoords, alpha);
		Vector3f interpolatedNormal = new Vector3f().interpolate(min.normals, max.normals, alpha);
		Vector3f interpolatedModelPosition = new Vector3f().interpolate(min.modelPosition, max.modelPosition, alpha);
		Vertex interpolatedVertex = new Vertex(interpolatedVector, interpolatedTexCoords, interpolatedNormal, interpolatedModelPosition);

		if (interpolatedVector.Xpos > mid.position.Xpos) {
			// flatBottom
			rasterize(new Edge(min, mid), new Edge(min, interpolatedVertex), texture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(mid, max), new Edge(interpolatedVertex, max), texture, lights, screenPixels);
		} else {
			// flatBottom
			rasterize(new Edge(min, interpolatedVertex), new Edge(min, mid), texture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(interpolatedVertex, max), new Edge(mid, max), texture, lights, screenPixels);
		}
	}

	private void rasterize(Edge left, Edge right, Bitmap texture, List<Light> lights, int[] screenPixels) {
		int yStart = (int) Math.ceil(left.a.position.Ypos - 0.5f);
		int yEnd = (int) Math.ceil(right.b.position.Ypos - 0.5f);

		// raster clipping in Y - axis
		float yDistTop = 0;

		if (yStart < 0) {
			yDistTop = -yStart;
			yStart = 0;
		}

		if (yEnd >= height) {
			yEnd = height;
		}

		float LYPreStep = ((float) ((int) Math.ceil(left.a.position.Ypos - 0.5f)) + 0.5f - left.a.position.Ypos);
		float RYPreStep = ((float) ((int) Math.ceil(right.a.position.Ypos - 0.5f)) + 0.5f - right.a.position.Ypos);

		float leftXTex = (left.a.texCoords.Xpos + left.xTexStep * LYPreStep) + left.xTexStep * yDistTop;
		float leftYTex = (left.a.texCoords.Ypos + left.yTexStep * LYPreStep) + left.yTexStep * yDistTop;
		float rightXTex = (right.a.texCoords.Xpos + right.xTexStep * RYPreStep) + right.xTexStep * yDistTop;
		float rightYTex = (right.a.texCoords.Ypos + right.yTexStep * RYPreStep) + right.yTexStep * yDistTop;

		float leftZ = (left.a.position.Zpos + left.zStep * LYPreStep) + left.zStep * yDistTop;
		float rightZ = (right.a.position.Zpos + right.zStep * RYPreStep) + right.zStep * yDistTop;

		Vector3f leftNormal = new Vector3f((left.a.normals.Xpos + left.normalStep.Xpos * LYPreStep) + left.normalStep.Xpos * yDistTop, (left.a.normals.Ypos + left.normalStep.Ypos * LYPreStep) + left.normalStep.Ypos * yDistTop, (left.a.normals.Zpos + left.normalStep.Zpos * LYPreStep) + left.normalStep.Zpos * yDistTop);
		Vector3f rightNormal = new Vector3f((right.a.normals.Xpos + right.normalStep.Xpos * RYPreStep) + right.normalStep.Xpos * yDistTop, (right.a.normals.Ypos + right.normalStep.Ypos * RYPreStep) + right.normalStep.Ypos * yDistTop, (right.a.normals.Zpos + right.normalStep.Zpos * RYPreStep) + right.normalStep.Zpos * yDistTop);

		Vector3f leftModelPosition = new Vector3f((left.a.modelPosition.Xpos + left.modelPositionStep.Xpos * LYPreStep) + left.modelPositionStep.Xpos * yDistTop, (left.a.modelPosition.Ypos + left.modelPositionStep.Ypos * LYPreStep) + left.modelPositionStep.Ypos * yDistTop, (left.a.modelPosition.Zpos + left.modelPositionStep.Zpos * LYPreStep) + left.modelPositionStep.Zpos * yDistTop);
		Vector3f rightModelPosition = new Vector3f((right.a.modelPosition.Xpos + right.modelPositionStep.Xpos * RYPreStep) + right.modelPositionStep.Xpos * yDistTop, (right.a.modelPosition.Ypos + right.modelPositionStep.Ypos * RYPreStep) + right.modelPositionStep.Ypos * yDistTop, (right.a.modelPosition.Zpos + right.modelPositionStep.Zpos * RYPreStep) + right.modelPositionStep.Zpos * yDistTop);

		for (int y = yStart; y < yEnd; y++) {
			float xMin = left.xStep * ((float) (y) + 0.5f - left.a.position.Ypos) + left.a.position.Xpos;
			float xMax = right.xStep * ((float) (y) + 0.5f - right.a.position.Ypos) + right.a.position.Xpos;

			int xStart = (int) Math.ceil(xMin - 0.5f);
			int xEnd = (int) Math.ceil(xMax - 0.5f);

			int leftXClamp = 0;
			float XpreStep = ((float) (xStart) + 0.5f - xMin);

			// raster clipping in X - axis
			if (xStart < 0) {
				leftXClamp = -xStart;
				xStart = 0;
			}

			if (xEnd >= width) {
				xEnd = width;
			}

			float xDist = xMax - xMin;

			float xTexStep = (rightXTex - leftXTex) / xDist;
			float yTexStep = (rightYTex - leftYTex) / xDist;

			float xTex = leftXTex + xTexStep * XpreStep + xTexStep * leftXClamp;
			float yTex = leftYTex + yTexStep * XpreStep + yTexStep * leftXClamp;

			float zStep = (rightZ - leftZ) / xDist;
			float zCoords = leftZ + zStep * XpreStep + zStep * leftXClamp;

			Vector3f normalStep = new Vector3f((rightNormal.Xpos - leftNormal.Xpos) / xDist, (rightNormal.Ypos - leftNormal.Ypos) / xDist, (rightNormal.Zpos - leftNormal.Zpos) / xDist);
			Vector3f normalX = new Vector3f(leftNormal.Xpos + normalStep.Xpos * XpreStep + normalStep.Xpos * leftXClamp, leftNormal.Ypos + normalStep.Ypos * XpreStep + normalStep.Ypos * leftXClamp, leftNormal.Zpos + normalStep.Zpos * XpreStep + normalStep.Zpos * leftXClamp);

			Vector3f modelPositionStep = new Vector3f((rightModelPosition.Xpos - leftModelPosition.Xpos) / xDist, (rightModelPosition.Ypos - leftModelPosition.Ypos) / xDist, (rightModelPosition.Zpos - leftModelPosition.Zpos) / xDist);
			Vector3f modelPositionX = new Vector3f(leftModelPosition.Xpos + modelPositionStep.Xpos * XpreStep + modelPositionStep.Xpos * leftXClamp, leftModelPosition.Ypos + modelPositionStep.Ypos * XpreStep + modelPositionStep.Ypos * leftXClamp, leftModelPosition.Zpos + modelPositionStep.Zpos * XpreStep + modelPositionStep.Zpos * leftXClamp);

			for (int x = xStart; x < xEnd; x++) {
				float z = 1 / zCoords;

				int index = x + y * width;
				int texColor = texture.getColor(xTex * z, yTex * z);

				if (texColor != 0xffff00ff && z < depthBuffer[index]) {
					int tRed = (texColor >> 16) & 0xff;
					int tGreen = (texColor >> 8) & 0xff;
					int tBlue = (texColor) & 0xff;

					int cRed = (clearColor >> 16) & 0xff;
					int cGreen = (clearColor >> 8) & 0xff;
					int cBlue = (clearColor) & 0xff;

					int R = 0, G = 0, B = 0;

					if (z > depthBuffer[index]) {
						continue;
					}

					Vector3f pixelWorldPosition = new Vector3f(modelPositionX.Xpos * z, modelPositionX.Ypos * z, modelPositionX.Zpos * z);
					Vector3f normal = new Vector3f(normalX.Xpos * z, normalX.Ypos * z, normalX.Zpos * z);

					for (int i = 0; i < lights.size(); i++) {
						Vector3f pixelWorldPositionToLight = new Vector3f(lights.get(i).position.Xpos - pixelWorldPosition.Xpos, lights.get(i).position.Ypos - pixelWorldPosition.Ypos, lights.get(i).position.Zpos - pixelWorldPosition.Zpos);
						float distance = pixelWorldPositionToLight.getMagnitude();
						normal.normalize();
						pixelWorldPositionToLight.normalize();
						float lightValue = new Vector3f().dot(pixelWorldPositionToLight, normal);

						if (lightValue < 0.1f) {
							lightValue = 0.1f;
						}
						if (lightValue > 1) {
							lightValue = 1;
						}

						float attenuation = 1 / (lights.get(i).constantvalue + distance * lights.get(i).linearValue + distance * distance * lights.get(i).quadraticValue);

						R += (int) ((tRed * lightValue * lights.get(i).R * lightValue * attenuation) / 255);
						G += (int) ((tGreen * lightValue * lights.get(i).G * lightValue * attenuation) / 255);
						B += (int) ((tBlue * lightValue * lights.get(i).B * lightValue * attenuation) / 255);
					}

					if (R >= 255) {
						R = 255;
					}

					if (G >= 255) {
						G = 255;
					}

					if (B >= 255) {
						B = 255;
					}

					Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
					Vector3f objectColor = new Vector3f(R, G, B);
					float visibility = (z * 1.2f) / FAR;
					Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility + 0.1f);

					R = (int) fog.Xpos;
					G = (int) fog.Ypos;
					B = (int) fog.Zpos;

					// main color
					int color = (R << 16) | (G << 8) | (B);
					if (z < depthBuffer[index]) {
						depthBuffer[index] = z;
						pixel(x, y, screenPixels, color);
						if (x == width / 2 && y == height / 2 && updateRaycastPoint == true) {
							rayCastPoint = new Vector3f(modelPositionX.Xpos * z, modelPositionX.Ypos * z, modelPositionX.Zpos * z);
						}
					}
				}

				xTex += xTexStep;
				yTex += yTexStep;
				zCoords += zStep;
				normalX.Xpos += normalStep.Xpos;
				normalX.Ypos += normalStep.Ypos;
				normalX.Zpos += normalStep.Zpos;
				modelPositionX.Xpos += modelPositionStep.Xpos;
				modelPositionX.Ypos += modelPositionStep.Ypos;
				modelPositionX.Zpos += modelPositionStep.Zpos;
			}

			leftXTex += left.xTexStep;
			leftYTex += left.yTexStep;
			rightXTex += right.xTexStep;
			rightYTex += right.yTexStep;
			leftZ += left.zStep;
			rightZ += right.zStep;
			leftNormal.Xpos += left.normalStep.Xpos;
			leftNormal.Ypos += left.normalStep.Ypos;
			leftNormal.Zpos += left.normalStep.Zpos;
			rightNormal.Xpos += right.normalStep.Xpos;
			rightNormal.Ypos += right.normalStep.Ypos;
			rightNormal.Zpos += right.normalStep.Zpos;
			leftModelPosition.Xpos += left.modelPositionStep.Xpos;
			leftModelPosition.Ypos += left.modelPositionStep.Ypos;
			leftModelPosition.Zpos += left.modelPositionStep.Zpos;
			rightModelPosition.Xpos += right.modelPositionStep.Xpos;
			rightModelPosition.Ypos += right.modelPositionStep.Ypos;
			rightModelPosition.Zpos += right.modelPositionStep.Zpos;
		}
	}

	public void renderTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap emissiveTexture, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex aTranslate = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals), new Vector3f(a.modelPosition));
		Vertex bTranslate = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals), new Vector3f(b.modelPosition));
		Vertex cTranslate = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals), new Vector3f(c.modelPosition));

		// translate opposite to camera position(if the camera moves left the whole
		// world moves right to create an illusion that you move left)
		aTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		aTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		aTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		bTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		bTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		bTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		cTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		cTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		cTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		// same as translation we do rotation relative to camera
		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		// if the triangles are too far or behind the NEAR then don't render them
		if (aTranslate.position.Zpos <= NEAR && bTranslate.position.Zpos <= NEAR && cTranslate.position.Zpos <= NEAR) {
			return;
		} else if (aTranslate.position.Zpos >= FAR && bTranslate.position.Zpos >= FAR && cTranslate.position.Zpos >= FAR) {
			return;
		}

		Clipping clipping = new Clipping();
		Triangle triangle = new Triangle(aTranslate, bTranslate, cTranslate);
		// geometric clipping in clockwise
		clipping.clipNearZPlane(triangle, NEAR);
		for (Triangle tri1 : clipping.triZnear) {
			Triangle triangle1 = new Triangle(tri1.a, tri1.b, tri1.c);
			clipping.clipFarZPlane(triangle1, FAR);
			for (Triangle tri2 : clipping.triZfar) {
				rasterizeTriangle(tri2.a, tri2.b, tri2.c, texture, emissiveTexture, lights, screenPixels);
			}
		}
		clipping.triZnear.clear();
		clipping.triZfar.clear();
	}

	private void rasterizeTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap emissiveTexture, List<Light> lights, int[] screenPixels) {
		Vertex min = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals), new Vector3f(a.modelPosition));
		Vertex mid = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals), new Vector3f(b.modelPosition));
		Vertex max = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals), new Vector3f(c.modelPosition));

		min.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		mid.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		max.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));

		min.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		mid.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		max.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));

		min.position.Xpos /= min.position.Zpos;
		min.position.Ypos /= min.position.Zpos;

		mid.position.Xpos /= mid.position.Zpos;
		mid.position.Ypos /= mid.position.Zpos;

		max.position.Xpos /= max.position.Zpos;
		max.position.Ypos /= max.position.Zpos;

		min.position.shiftToScreenSpace(width, height);
		mid.position.shiftToScreenSpace(width, height);
		max.position.shiftToScreenSpace(width, height);

		// if the triangle is out of screen then don't render it
		if (min.position.Xpos >= width && mid.position.Xpos >= width && max.position.Xpos >= width) {
			return;
		} else if (min.position.Xpos <= 0 && mid.position.Xpos <= 0 && max.position.Xpos <= 0) {
			return;
		}

		if (min.position.Ypos >= width && mid.position.Ypos >= height && max.position.Ypos >= height) {
			return;
		} else if (min.position.Ypos <= 0 && mid.position.Ypos <= 0 && max.position.Ypos <= 0) {
			return;
		}

		min.texCoords.Xpos /= min.position.Zpos;
		min.texCoords.Ypos /= min.position.Zpos;

		mid.texCoords.Xpos /= mid.position.Zpos;
		mid.texCoords.Ypos /= mid.position.Zpos;

		max.texCoords.Xpos /= max.position.Zpos;
		max.texCoords.Ypos /= max.position.Zpos;

		min.normals.Xpos /= min.position.Zpos;
		mid.normals.Xpos /= mid.position.Zpos;
		max.normals.Xpos /= max.position.Zpos;

		min.normals.Ypos /= min.position.Zpos;
		mid.normals.Ypos /= mid.position.Zpos;
		max.normals.Ypos /= max.position.Zpos;

		min.normals.Zpos /= min.position.Zpos;
		mid.normals.Zpos /= mid.position.Zpos;
		max.normals.Zpos /= max.position.Zpos;

		min.modelPosition.Xpos /= min.position.Zpos;
		mid.modelPosition.Xpos /= mid.position.Zpos;
		max.modelPosition.Xpos /= max.position.Zpos;

		min.modelPosition.Ypos /= min.position.Zpos;
		mid.modelPosition.Ypos /= mid.position.Zpos;
		max.modelPosition.Ypos /= max.position.Zpos;

		min.modelPosition.Zpos /= min.position.Zpos;
		mid.modelPosition.Zpos /= mid.position.Zpos;
		max.modelPosition.Zpos /= max.position.Zpos;

		min.position.Zpos = 1 / min.position.Zpos;
		mid.position.Zpos = 1 / mid.position.Zpos;
		max.position.Zpos = 1 / max.position.Zpos;

		Vector2f left = new Vector2f(mid.position.Xpos - min.position.Xpos, mid.position.Ypos - min.position.Ypos);
		Vector2f right = new Vector2f(max.position.Xpos - min.position.Xpos, max.position.Ypos - min.position.Ypos);

		float backfaceCulling = left.Xpos * right.Ypos - left.Ypos * right.Xpos;

		if (backfaceCulling < 0) {
			return;
		}

		Vertex[] sort = new Vertex[3];

		sort[0] = new Vertex(new Vector3f(min.position), new Vector2f(min.texCoords), new Vector3f(min.normals), new Vector3f(min.modelPosition));
		sort[1] = new Vertex(new Vector3f(mid.position), new Vector2f(mid.texCoords), new Vector3f(mid.normals), new Vector3f(mid.modelPosition));
		sort[2] = new Vertex(new Vector3f(max.position), new Vector2f(max.texCoords), new Vector3f(max.normals), new Vector3f(max.modelPosition));

		// sorting according to the y position for rasterization
		for (int i = 0; i < sort.length - 1; i++) {
			for (int j = i + 1; j < sort.length; j++) {
				if (sort[j].position.Ypos < sort[i].position.Ypos) {
					Vertex temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
				}
			}
		}

		min = sort[0];
		mid = sort[1];
		max = sort[2];

		float alpha = (mid.position.Ypos - min.position.Ypos) / (max.position.Ypos - min.position.Ypos);
		Vector3f interpolatedVector = new Vector3f().interpolate(min.position, max.position, alpha);
		Vector2f interpolatedTexCoords = new Vector2f().interpolate(min.texCoords, max.texCoords, alpha);
		Vector3f interpolatedNormal = new Vector3f().interpolate(min.normals, max.normals, alpha);
		Vector3f interpolatedModelPosition = new Vector3f().interpolate(min.modelPosition, max.modelPosition, alpha);
		Vertex interpolatedVertex = new Vertex(interpolatedVector, interpolatedTexCoords, interpolatedNormal, interpolatedModelPosition);

		if (interpolatedVector.Xpos > mid.position.Xpos) {
			// flatBottom
			rasterize(new Edge(min, mid), new Edge(min, interpolatedVertex), texture, emissiveTexture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(mid, max), new Edge(interpolatedVertex, max), texture, emissiveTexture, lights, screenPixels);
		} else {
			// flatBottom
			rasterize(new Edge(min, interpolatedVertex), new Edge(min, mid), texture, emissiveTexture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(interpolatedVertex, max), new Edge(mid, max), texture, emissiveTexture, lights, screenPixels);
		}
	}

	private void rasterize(Edge left, Edge right, Bitmap texture, Bitmap emissiveTexture, List<Light> lights, int[] screenPixels) {
		int yStart = (int) Math.ceil(left.a.position.Ypos - 0.5f);
		int yEnd = (int) Math.ceil(right.b.position.Ypos - 0.5f);

		// raster clipping in Y - axis
		float yDistTop = 0;

		if (yStart < 0) {
			yDistTop = -yStart;
			yStart = 0;
		}

		if (yEnd >= height) {
			yEnd = height;
		}

		float LYPreStep = ((float) ((int) Math.ceil(left.a.position.Ypos - 0.5f)) + 0.5f - left.a.position.Ypos);
		float RYPreStep = ((float) ((int) Math.ceil(right.a.position.Ypos - 0.5f)) + 0.5f - right.a.position.Ypos);

		float leftXTex = (left.a.texCoords.Xpos + left.xTexStep * LYPreStep) + left.xTexStep * yDistTop;
		float leftYTex = (left.a.texCoords.Ypos + left.yTexStep * LYPreStep) + left.yTexStep * yDistTop;
		float rightXTex = (right.a.texCoords.Xpos + right.xTexStep * RYPreStep) + right.xTexStep * yDistTop;
		float rightYTex = (right.a.texCoords.Ypos + right.yTexStep * RYPreStep) + right.yTexStep * yDistTop;

		float leftZ = (left.a.position.Zpos + left.zStep * LYPreStep) + left.zStep * yDistTop;
		float rightZ = (right.a.position.Zpos + right.zStep * RYPreStep) + right.zStep * yDistTop;

		Vector3f leftNormal = new Vector3f((left.a.normals.Xpos + left.normalStep.Xpos * LYPreStep) + left.normalStep.Xpos * yDistTop, (left.a.normals.Ypos + left.normalStep.Ypos * LYPreStep) + left.normalStep.Ypos * yDistTop, (left.a.normals.Zpos + left.normalStep.Zpos * LYPreStep) + left.normalStep.Zpos * yDistTop);
		Vector3f rightNormal = new Vector3f((right.a.normals.Xpos + right.normalStep.Xpos * RYPreStep) + right.normalStep.Xpos * yDistTop, (right.a.normals.Ypos + right.normalStep.Ypos * RYPreStep) + right.normalStep.Ypos * yDistTop, (right.a.normals.Zpos + right.normalStep.Zpos * RYPreStep) + right.normalStep.Zpos * yDistTop);

		Vector3f leftModelPosition = new Vector3f((left.a.modelPosition.Xpos + left.modelPositionStep.Xpos * LYPreStep) + left.modelPositionStep.Xpos * yDistTop, (left.a.modelPosition.Ypos + left.modelPositionStep.Ypos * LYPreStep) + left.modelPositionStep.Ypos * yDistTop, (left.a.modelPosition.Zpos + left.modelPositionStep.Zpos * LYPreStep) + left.modelPositionStep.Zpos * yDistTop);
		Vector3f rightModelPosition = new Vector3f((right.a.modelPosition.Xpos + right.modelPositionStep.Xpos * RYPreStep) + right.modelPositionStep.Xpos * yDistTop, (right.a.modelPosition.Ypos + right.modelPositionStep.Ypos * RYPreStep) + right.modelPositionStep.Ypos * yDistTop, (right.a.modelPosition.Zpos + right.modelPositionStep.Zpos * RYPreStep) + right.modelPositionStep.Zpos * yDistTop);

		for (int y = yStart; y < yEnd; y++) {
			float xMin = left.xStep * ((float) (y) + 0.5f - left.a.position.Ypos) + left.a.position.Xpos;
			float xMax = right.xStep * ((float) (y) + 0.5f - right.a.position.Ypos) + right.a.position.Xpos;

			int xStart = (int) Math.ceil(xMin - 0.5f);
			int xEnd = (int) Math.ceil(xMax - 0.5f);

			int leftXClamp = 0;
			float XpreStep = ((float) (xStart) + 0.5f - xMin);

			// raster clipping in X - axis
			if (xStart < 0) {
				leftXClamp = -xStart;
				xStart = 0;
			}

			if (xEnd >= width) {
				xEnd = width;
			}

			float xDist = xMax - xMin;

			float xTexStep = (rightXTex - leftXTex) / xDist;
			float yTexStep = (rightYTex - leftYTex) / xDist;

			float xTex = leftXTex + xTexStep * XpreStep + xTexStep * leftXClamp;
			float yTex = leftYTex + yTexStep * XpreStep + yTexStep * leftXClamp;

			float zStep = (rightZ - leftZ) / xDist;
			float zCoords = leftZ + zStep * XpreStep + zStep * leftXClamp;

			Vector3f normalStep = new Vector3f((rightNormal.Xpos - leftNormal.Xpos) / xDist, (rightNormal.Ypos - leftNormal.Ypos) / xDist, (rightNormal.Zpos - leftNormal.Zpos) / xDist);
			Vector3f normalX = new Vector3f(leftNormal.Xpos + normalStep.Xpos * XpreStep + normalStep.Xpos * leftXClamp, leftNormal.Ypos + normalStep.Ypos * XpreStep + normalStep.Ypos * leftXClamp, leftNormal.Zpos + normalStep.Zpos * XpreStep + normalStep.Zpos * leftXClamp);

			Vector3f modelPositionStep = new Vector3f((rightModelPosition.Xpos - leftModelPosition.Xpos) / xDist, (rightModelPosition.Ypos - leftModelPosition.Ypos) / xDist, (rightModelPosition.Zpos - leftModelPosition.Zpos) / xDist);
			Vector3f modelPositionX = new Vector3f(leftModelPosition.Xpos + modelPositionStep.Xpos * XpreStep + modelPositionStep.Xpos * leftXClamp, leftModelPosition.Ypos + modelPositionStep.Ypos * XpreStep + modelPositionStep.Ypos * leftXClamp, leftModelPosition.Zpos + modelPositionStep.Zpos * XpreStep + modelPositionStep.Zpos * leftXClamp);

			for (int x = xStart; x < xEnd; x++) {
				float z = 1 / zCoords;

				int index = x + y * width;
				int texColor = 0;
				int texEmissiveColor = 0;

				texColor = texture.getColor(xTex * z, yTex * z);
				texEmissiveColor = emissiveTexture.getColor(xTex * z, yTex * z);

				int color = 0;// main color

				if (texColor != 0xffff00ff && z < depthBuffer[index]) {
					if (texEmissiveColor == 0xff000000) {
						int tRed = (texColor >> 16) & 0xff;
						int tGreen = (texColor >> 8) & 0xff;
						int tBlue = (texColor) & 0xff;

						int cRed = (clearColor >> 16) & 0xff;
						int cGreen = (clearColor >> 8) & 0xff;
						int cBlue = (clearColor) & 0xff;

						int R = 0, G = 0, B = 0;

						if (z > depthBuffer[index]) {
							continue;
						}

						Vector3f pixelWorldPosition = new Vector3f(modelPositionX.Xpos * z, modelPositionX.Ypos * z, modelPositionX.Zpos * z);
						Vector3f normal = new Vector3f(normalX.Xpos * z, normalX.Ypos * z, normalX.Zpos * z);

						for (int i = 0; i < lights.size(); i++) {
							Vector3f pixelWorldPositionToLight = new Vector3f(lights.get(i).position.Xpos - pixelWorldPosition.Xpos, lights.get(i).position.Ypos - pixelWorldPosition.Ypos, lights.get(i).position.Zpos - pixelWorldPosition.Zpos);
							float distance = pixelWorldPositionToLight.getMagnitude();
							normal.normalize();
							pixelWorldPositionToLight.normalize();
							float lightValue = new Vector3f().dot(pixelWorldPositionToLight, normal);

							if (lightValue < 0.1f) {
								lightValue = 0.1f;
							}
							if (lightValue > 1) {
								lightValue = 1;
							}

							float attenuation = 1 / (lights.get(i).constantvalue + distance * lights.get(i).linearValue + distance * distance * lights.get(i).quadraticValue);

							R += (int) ((tRed * lightValue * lights.get(i).R * lightValue * attenuation) / 255);
							G += (int) ((tGreen * lightValue * lights.get(i).G * lightValue * attenuation) / 255);
							B += (int) ((tBlue * lightValue * lights.get(i).B * lightValue * attenuation) / 255);

						}

						if (R >= 255) {
							R = 255;
						}

						if (G >= 255) {
							G = 255;
						}

						if (B >= 255) {
							B = 255;
						}

						Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
						Vector3f objectColor = new Vector3f(R, G, B);
						float visibility = (z * 1.2f) / FAR;
						Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility + 0.1f);

						R = (int) fog.Xpos;
						G = (int) fog.Ypos;
						B = (int) fog.Zpos;

						color = (R << 16) | (G << 8) | (B);
					} else {
						if (isGlass == false) {
							int red = (texEmissiveColor >> 16) & 0xff;
							int green = (texEmissiveColor >> 8) & 0xff;
							int blue = (texEmissiveColor) & 0xff;

							int tRed = (texColor >> 16) & 0xff;
							int tGreen = (texColor >> 8) & 0xff;
							int tBlue = (texColor) & 0xff;

							int cRed = (clearColor >> 16) & 0xff;
							int cGreen = (clearColor >> 8) & 0xff;
							int cBlue = (clearColor) & 0xff;

							int R = 0, G = 0, B = 0;

							Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
							Vector3f objectColor = new Vector3f(tRed, tGreen, tBlue);
							float visibility = (z) / FAR;
							Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility);

							R = (int) (fog.Xpos * 0.1f + red * 0.9f);
							G = (int) (fog.Ypos * 0.1f + green * 0.9f);
							B = (int) (fog.Zpos * 0.1f + blue * 0.9f);

							color = (R << 16) | (G << 8) | (B);
						} else {
							int red = (texEmissiveColor >> 16) & 0xff;
							int green = (texEmissiveColor >> 8) & 0xff;
							int blue = (texEmissiveColor) & 0xff;

							int tRed = (texColor >> 16) & 0xff;
							int tGreen = (texColor >> 8) & 0xff;
							int tBlue = (texColor) & 0xff;

							int cRed = (clearColor >> 16) & 0xff;
							int cGreen = (clearColor >> 8) & 0xff;
							int cBlue = (clearColor) & 0xff;

							int R = 0, G = 0, B = 0;

							Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
							Vector3f objectColor = new Vector3f(tRed, tGreen, tBlue);
							float visibility = (z) / FAR;
							Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility);

							int backgroundColor = screenPixels[x + y * width];

							int mred = (backgroundColor >> 16) & 0xff;
							int mgreen = (backgroundColor >> 8) & 0xff;
							int mblue = (backgroundColor) & 0xff;

							R = (int) (fog.Xpos * 0.1f + red * 0.9f);
							G = (int) (fog.Ypos * 0.1f + green * 0.9f);
							B = (int) (fog.Zpos * 0.1f + blue * 0.9f);

							int finalR = (int) (mred * 0.5f + R * 0.5f);
							int finalG = (int) (mgreen * 0.5f + G * 0.5f);
							int finalB = (int) (mblue * 0.5f + B * 0.5f);

							color = (finalR << 16) | (finalG << 8) | (finalB);
						}
					}

					if (z < depthBuffer[index]) {
						depthBuffer[index] = z;
						pixel(x, y, screenPixels, color);
						if (x == width / 2 && y == height / 2 && updateRaycastPoint == true) {
							rayCastPoint = new Vector3f(modelPositionX.Xpos * z, modelPositionX.Ypos * z, modelPositionX.Zpos * z);
						}
					}
				}

				xTex += xTexStep;
				yTex += yTexStep;
				zCoords += zStep;
				normalX.Xpos += normalStep.Xpos;
				normalX.Ypos += normalStep.Ypos;
				normalX.Zpos += normalStep.Zpos;
				modelPositionX.Xpos += modelPositionStep.Xpos;
				modelPositionX.Ypos += modelPositionStep.Ypos;
				modelPositionX.Zpos += modelPositionStep.Zpos;
			}

			leftXTex += left.xTexStep;
			leftYTex += left.yTexStep;
			rightXTex += right.xTexStep;
			rightYTex += right.yTexStep;
			leftZ += left.zStep;
			rightZ += right.zStep;
			leftNormal.Xpos += left.normalStep.Xpos;
			leftNormal.Ypos += left.normalStep.Ypos;
			leftNormal.Zpos += left.normalStep.Zpos;
			rightNormal.Xpos += right.normalStep.Xpos;
			rightNormal.Ypos += right.normalStep.Ypos;
			rightNormal.Zpos += right.normalStep.Zpos;
			leftModelPosition.Xpos += left.modelPositionStep.Xpos;
			leftModelPosition.Ypos += left.modelPositionStep.Ypos;
			leftModelPosition.Zpos += left.modelPositionStep.Zpos;
			rightModelPosition.Xpos += right.modelPositionStep.Xpos;
			rightModelPosition.Ypos += right.modelPositionStep.Ypos;
			rightModelPosition.Zpos += right.modelPositionStep.Zpos;
		}
	}

	public void renderTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap normalMap, Bitmap displacementMap, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex aTranslate = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(), new Vector3f(a.modelPosition));
		Vertex bTranslate = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(), new Vector3f(b.modelPosition));
		Vertex cTranslate = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(), new Vector3f(c.modelPosition));

		// translate opposite to camera position(if the camera moves left the whole
		// world moves right to create an illusion that you move left)
		aTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		aTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		aTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		bTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		bTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		bTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		cTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		cTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		cTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		// same as translation we do rotation relative to camera
		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		// if the triangles are too far or behind the NEAR then don't render them
		if (aTranslate.position.Zpos <= NEAR && bTranslate.position.Zpos <= NEAR && cTranslate.position.Zpos <= NEAR) {
			return;
		} else if (aTranslate.position.Zpos >= FAR && bTranslate.position.Zpos >= FAR && cTranslate.position.Zpos >= FAR) {
			return;
		}

		a.tangent.normalize();
		a.bitangent.normalize();
		a.normals.normalize();

		Clipping clipping = new Clipping();
		Triangle triangle = new Triangle(aTranslate, bTranslate, cTranslate);
		// geometric clipping in clockwise
		clipping.clipNearZPlane(triangle, NEAR);
		for (Triangle tri1 : clipping.triZnear) {
			Triangle triangle1 = new Triangle(tri1.a, tri1.b, tri1.c);
			clipping.clipFarZPlane(triangle1, FAR);
			for (Triangle tri2 : clipping.triZfar) {
				tri2.a.tangent = a.tangent;
				tri2.a.bitangent = a.bitangent;
				rasterizeTriangle(tri2.a, tri2.b, tri2.c, texture, normalMap, displacementMap, lights, camera, screenPixels);
			}
		}
		clipping.triZnear.clear();
		clipping.triZfar.clear();
	}

	private void rasterizeTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap normalMap, Bitmap displacementMap, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex min = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals), new Vector3f(a.modelPosition));
		Vertex mid = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals), new Vector3f(b.modelPosition));
		Vertex max = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals), new Vector3f(c.modelPosition));

		min.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		mid.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		max.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));

		min.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		mid.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		max.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));

		min.position.Xpos /= min.position.Zpos;
		min.position.Ypos /= min.position.Zpos;
		min.texCoords.Xpos /= min.position.Zpos;
		min.texCoords.Ypos /= min.position.Zpos;

		mid.position.Xpos /= mid.position.Zpos;
		mid.position.Ypos /= mid.position.Zpos;
		mid.texCoords.Xpos /= mid.position.Zpos;
		mid.texCoords.Ypos /= mid.position.Zpos;

		max.position.Xpos /= max.position.Zpos;
		max.position.Ypos /= max.position.Zpos;
		max.texCoords.Xpos /= max.position.Zpos;
		max.texCoords.Ypos /= max.position.Zpos;

		min.position.shiftToScreenSpace(width, height);
		mid.position.shiftToScreenSpace(width, height);
		max.position.shiftToScreenSpace(width, height);

		// if the triangle is out of screen then don't render it
		if (min.position.Xpos >= width && mid.position.Xpos >= width && max.position.Xpos >= width) {
			return;
		} else if (min.position.Xpos <= 0 && mid.position.Xpos <= 0 && max.position.Xpos <= 0) {
			return;
		}

		if (min.position.Ypos >= height && mid.position.Ypos >= height && max.position.Ypos >= height) {
			return;
		} else if (min.position.Ypos <= 0 && mid.position.Ypos <= 0 && max.position.Ypos <= 0) {
			return;
		}

		Vector3f tangent = new Vector3f(a.tangent);
		Vector3f bitangent = new Vector3f(a.bitangent);
		Vector3f normal = new Vector3f().cross(tangent, bitangent);

		Matrix4f TBN = new Matrix4f().TBN(tangent, bitangent, normal);

		min.modelPosition = min.modelPosition.multiply(TBN);
		mid.modelPosition = mid.modelPosition.multiply(TBN);
		max.modelPosition = max.modelPosition.multiply(TBN);

		List<Light> rotatedLights = new ArrayList<Light>();
		for (Light light : lights) {
			rotatedLights.add(new Light(light.position.multiply(TBN), light.constantvalue, light.linearValue, light.quadraticValue, light.R, light.G, light.B));
		}

		Camera rotatedCamera = new Camera(camera.cameraPosition.multiply(TBN), camera.getAngleX(), camera.getAngleY());

		min.modelPosition.Xpos /= min.position.Zpos;
		min.modelPosition.Ypos /= min.position.Zpos;
		min.modelPosition.Zpos /= min.position.Zpos;

		mid.modelPosition.Xpos /= mid.position.Zpos;
		mid.modelPosition.Ypos /= mid.position.Zpos;
		mid.modelPosition.Zpos /= mid.position.Zpos;

		max.modelPosition.Xpos /= max.position.Zpos;
		max.modelPosition.Ypos /= max.position.Zpos;
		max.modelPosition.Zpos /= max.position.Zpos;

		min.position.Zpos = 1 / min.position.Zpos;
		mid.position.Zpos = 1 / mid.position.Zpos;
		max.position.Zpos = 1 / max.position.Zpos;

		Vector2f left = new Vector2f(mid.position.Xpos - min.position.Xpos, mid.position.Ypos - min.position.Ypos);
		Vector2f right = new Vector2f(max.position.Xpos - min.position.Xpos, max.position.Ypos - min.position.Ypos);

		float backfaceCulling = left.Xpos * right.Ypos - left.Ypos * right.Xpos;

		if (backfaceCulling < 0) {
			return;
		}

		Vertex[] sort = new Vertex[3];

		sort[0] = new Vertex(new Vector3f(min.position), new Vector2f(min.texCoords), new Vector3f(min.normals), new Vector3f(min.modelPosition));
		sort[1] = new Vertex(new Vector3f(mid.position), new Vector2f(mid.texCoords), new Vector3f(mid.normals), new Vector3f(mid.modelPosition));
		sort[2] = new Vertex(new Vector3f(max.position), new Vector2f(max.texCoords), new Vector3f(max.normals), new Vector3f(max.modelPosition));

		// sorting according to the y position for rasterization
		for (int i = 0; i < sort.length - 1; i++) {
			for (int j = i + 1; j < sort.length; j++) {
				if (sort[j].position.Ypos < sort[i].position.Ypos) {
					Vertex temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
				}
			}
		}

		min = sort[0];
		mid = sort[1];
		max = sort[2];

		float alpha = (mid.position.Ypos - min.position.Ypos) / (max.position.Ypos - min.position.Ypos);
		Vector3f interpolatedVector = new Vector3f().interpolate(min.position, max.position, alpha);
		Vector2f interpolatedTexCoords = new Vector2f().interpolate(min.texCoords, max.texCoords, alpha);
		Vector3f interpolatedNormal = new Vector3f().interpolate(min.normals, max.normals, alpha);
		Vector3f interpolatedModelPosition = new Vector3f().interpolate(min.modelPosition, max.modelPosition, alpha);
		Vertex interpolatedVertex = new Vertex(interpolatedVector, interpolatedTexCoords, interpolatedNormal, interpolatedModelPosition);

		if (interpolatedVector.Xpos > mid.position.Xpos) {
			// flatBottom
			rasterize(new Edge(min, mid), new Edge(min, interpolatedVertex), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
			// flatTop
			rasterize(new Edge(mid, max), new Edge(interpolatedVertex, max), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
		} else {
			// flatBottom
			rasterize(new Edge(min, interpolatedVertex), new Edge(min, mid), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
			// flatTop
			rasterize(new Edge(interpolatedVertex, max), new Edge(mid, max), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
		}
	}

	private void rasterize(Edge left, Edge right, Bitmap texture, Bitmap normalMap, Bitmap displacementMap, List<Light> lights, Camera camera, int[] screenPixels) {
		int yStart = (int) Math.ceil(left.a.position.Ypos - 0.5f);
		int yEnd = (int) Math.ceil(right.b.position.Ypos - 0.5f);

		// raster clipping in Y - axis
		float yDistTop = 0;

		if (yStart < 0) {
			yDistTop = -yStart;
			yStart = 0;
		}

		if (yEnd >= height) {
			yEnd = height;
		}

		float LYPreStep = ((float) ((int) Math.ceil(left.a.position.Ypos - 0.5f)) + 0.5f - left.a.position.Ypos);
		float RYPreStep = ((float) ((int) Math.ceil(right.a.position.Ypos - 0.5f)) + 0.5f - right.a.position.Ypos);

		float leftXTex = (left.a.texCoords.Xpos + left.xTexStep * LYPreStep) + left.xTexStep * yDistTop;
		float leftYTex = (left.a.texCoords.Ypos + left.yTexStep * LYPreStep) + left.yTexStep * yDistTop;
		float rightXTex = (right.a.texCoords.Xpos + right.xTexStep * RYPreStep) + right.xTexStep * yDistTop;
		float rightYTex = (right.a.texCoords.Ypos + right.yTexStep * RYPreStep) + right.yTexStep * yDistTop;

		float leftZ = (left.a.position.Zpos + left.zStep * LYPreStep) + left.zStep * yDistTop;
		float rightZ = (right.a.position.Zpos + right.zStep * RYPreStep) + right.zStep * yDistTop;

		Vector3f leftModelPosition = new Vector3f((left.a.modelPosition.Xpos + left.modelPositionStep.Xpos * LYPreStep) + left.modelPositionStep.Xpos * yDistTop, (left.a.modelPosition.Ypos + left.modelPositionStep.Ypos * LYPreStep) + left.modelPositionStep.Ypos * yDistTop, (left.a.modelPosition.Zpos + left.modelPositionStep.Zpos * LYPreStep) + left.modelPositionStep.Zpos * yDistTop);
		Vector3f rightModelPosition = new Vector3f((right.a.modelPosition.Xpos + right.modelPositionStep.Xpos * RYPreStep) + right.modelPositionStep.Xpos * yDistTop, (right.a.modelPosition.Ypos + right.modelPositionStep.Ypos * RYPreStep) + right.modelPositionStep.Ypos * yDistTop, (right.a.modelPosition.Zpos + right.modelPositionStep.Zpos * RYPreStep) + right.modelPositionStep.Zpos * yDistTop);

		for (int y = yStart; y < yEnd; y++) {
			float xMin = left.xStep * ((float) (y) + 0.5f - left.a.position.Ypos) + left.a.position.Xpos;
			float xMax = right.xStep * ((float) (y) + 0.5f - right.a.position.Ypos) + right.a.position.Xpos;

			int xStart = (int) Math.ceil(xMin - 0.5f);
			int xEnd = (int) Math.ceil(xMax - 0.5f);

			int leftXClamp = 0;
			float XpreStep = ((float) (xStart) + 0.5f - xMin);

			// raster clipping in X - axis
			if (xStart < 0) {
				leftXClamp = -xStart;
				xStart = 0;
			}

			if (xEnd >= width) {
				xEnd = width;
			}

			float xDist = xMax - xMin;

			float xTexStep = (rightXTex - leftXTex) / xDist;
			float yTexStep = (rightYTex - leftYTex) / xDist;

			float xTex = leftXTex + xTexStep * XpreStep + xTexStep * leftXClamp;
			float yTex = leftYTex + yTexStep * XpreStep + yTexStep * leftXClamp;

			float zStep = (rightZ - leftZ) / xDist;
			float zCoords = leftZ + zStep * XpreStep + zStep * leftXClamp;

			Vector3f modelPositionStep = new Vector3f((rightModelPosition.Xpos - leftModelPosition.Xpos) / xDist, (rightModelPosition.Ypos - leftModelPosition.Ypos) / xDist, (rightModelPosition.Zpos - leftModelPosition.Zpos) / xDist);
			Vector3f modelPositionX = new Vector3f(leftModelPosition.Xpos + modelPositionStep.Xpos * XpreStep + modelPositionStep.Xpos * leftXClamp, leftModelPosition.Ypos + modelPositionStep.Ypos * XpreStep + modelPositionStep.Ypos * leftXClamp, leftModelPosition.Zpos + modelPositionStep.Zpos * XpreStep + modelPositionStep.Zpos * leftXClamp);

			for (int x = xStart; x < xEnd; x++) {
				float z = 1 / zCoords;
				Vector2f currentTexCoords = new Vector2f(xTex * z, yTex * z);

				int index = x + y * width;
				int texColor = 0xffff00ff;
				int normalMapColor = 0;
				int depthMapColor = 0;

				if (z < depthBuffer[index]) {
					Vector3f viewDirection = new Vector3f(camera.cameraPosition.Xpos - modelPositionX.Xpos * z, camera.cameraPosition.Ypos - modelPositionX.Ypos * z, camera.cameraPosition.Zpos - modelPositionX.Zpos * z);
					viewDirection.Ypos *= -1;
					viewDirection.normalize();
					depthMapColor = displacementMap.getColor(currentTexCoords);
					float depthHeight = (float) ((depthMapColor >> 16) & 0xff) / 255.0f;

					float minLayers = 1.0f;
					float maxLayers = 3.0f;
					float dot = new Vector3f().dot(new Vector3f(0, 0, 1), viewDirection);
					if (dot < 0) {
						dot = 0;
					}

					float numLayers = minLayers * dot + maxLayers * (1 - dot);

					float layerDepth = 1.0f / numLayers;
					float currentLayerDepth = 0.0f;

					Vector2f p = new Vector2f((viewDirection.Xpos / viewDirection.Zpos) * depthHeight * 0.05f, -(viewDirection.Ypos / viewDirection.Zpos) * depthHeight * 0.05f);
					Vector2f deltaTexCoords = new Vector2f(p.Xpos / numLayers, p.Ypos / numLayers);

					int currentDepthColor = texture.getColor(currentTexCoords);
					float currentDepthMapValue = (float) ((currentDepthColor >> 16) & 0xff) / 255.0f;

					while (currentLayerDepth < currentDepthMapValue) {
						currentTexCoords.Xpos -= deltaTexCoords.Xpos;
						currentTexCoords.Ypos -= deltaTexCoords.Ypos;
						currentDepthColor = texture.getColor(currentTexCoords);
						currentDepthMapValue = (float) ((currentDepthColor >> 16) & 0xff) / 255.0f;
						currentLayerDepth += layerDepth;
					}

					Vector2f prevTexCoords = new Vector2f(currentTexCoords.Xpos + deltaTexCoords.Xpos, currentTexCoords.Ypos + deltaTexCoords.Ypos);
					float afterDepth = currentDepthMapValue - currentLayerDepth;

					int beforeDepthColor = texture.getColor(prevTexCoords);
					float beforeDepthMapValue = (float) ((beforeDepthColor >> 16) & 0xff) / 255.0f;

					float beforeDepth = beforeDepthMapValue - currentLayerDepth + layerDepth;
					float weight = afterDepth / (afterDepth - beforeDepth);
					currentTexCoords = new Vector2f().interpolate(currentTexCoords, prevTexCoords, weight);

					texColor = texture.getColor(currentTexCoords);
					normalMapColor = normalMap.getColor(currentTexCoords);

					if (texColor != 0xffff00ff) {
						int tRed = (texColor >> 16) & 0xff;
						int tGreen = (texColor >> 8) & 0xff;
						int tBlue = (texColor) & 0xff;

						int cRed = (clearColor >> 16) & 0xff;
						int cGreen = (clearColor >> 8) & 0xff;
						int cBlue = (clearColor) & 0xff;

						int nRed = (normalMapColor >> 16) & 0xff;
						int nGreen = (normalMapColor >> 8) & 0xff;
						int nBlue = (normalMapColor) & 0xff;

						float normalRed = (float) nRed / 255;
						float normalGreen = (float) nGreen / 255;
						float normalBlue = (float) nBlue / 255;

						if (z > depthBuffer[index]) {
							continue;
						}

						Vector3f normalWorld = new Vector3f((normalRed * 2.0f) - 1.0f, (normalGreen * 2.0f) - 1.0f, normalBlue * 1.2f);
						Vector3f normal = new Vector3f(normalWorld);

						int R = 0, G = 0, B = 0;

						for (int i = 0; i < lights.size(); i++) {
							Vector3f pixelWorldPositionToLight = new Vector3f(lights.get(i).position.Xpos - modelPositionX.Xpos * z, lights.get(i).position.Ypos - modelPositionX.Ypos * z, lights.get(i).position.Zpos - modelPositionX.Zpos * z);
							float distance = pixelWorldPositionToLight.getMagnitude();
							normal.normalize();
							pixelWorldPositionToLight.normalize();
							float lightValue = new Vector3f().dot(pixelWorldPositionToLight, normal);

							if (lightValue < 0.1f) {
								lightValue = 0.1f;
							}
							if (lightValue > 1) {
								lightValue = 1;
							}

							float attenuation = 1 / (lights.get(i).constantvalue + distance * lights.get(i).linearValue + distance * distance * lights.get(i).quadraticValue);

							R += (int) ((tRed * lightValue * lights.get(i).R * lightValue * attenuation) / 255);
							G += (int) ((tGreen * lightValue * lights.get(i).G * lightValue * attenuation) / 255);
							B += (int) ((tBlue * lightValue * lights.get(i).B * lightValue * attenuation) / 255);
						}

						if (R >= 255) {
							R = 255;
						}

						if (G >= 255) {
							G = 255;
						}

						if (B >= 255) {
							B = 255;
						}

						Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
						Vector3f objectColor = new Vector3f(R, G, B);
						float visibility = (z * 1.2f) / FAR;
						Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility + 0.1f);

						R = (int) fog.Xpos;
						G = (int) fog.Ypos;
						B = (int) fog.Zpos;

						// main color
						int color = (R << 16) | (G << 8) | (B);

						if (z < depthBuffer[index]) {
							depthBuffer[index] = z;
							pixel(x, y, screenPixels, color);
							if (x == width / 2 && y == height / 2 && updateRaycastPoint == true) {
								rayCastPoint = new Vector3f(modelPositionX.Xpos * z, modelPositionX.Ypos * z, modelPositionX.Zpos * z);
							}
						}
					}
				}

				xTex += xTexStep;
				yTex += yTexStep;
				zCoords += zStep;
				modelPositionX.Xpos += modelPositionStep.Xpos;
				modelPositionX.Ypos += modelPositionStep.Ypos;
				modelPositionX.Zpos += modelPositionStep.Zpos;
			}

			leftXTex += left.xTexStep;
			leftYTex += left.yTexStep;
			rightXTex += right.xTexStep;
			rightYTex += right.yTexStep;
			leftZ += left.zStep;
			rightZ += right.zStep;
			leftModelPosition.Xpos += left.modelPositionStep.Xpos;
			leftModelPosition.Ypos += left.modelPositionStep.Ypos;
			leftModelPosition.Zpos += left.modelPositionStep.Zpos;
			rightModelPosition.Xpos += right.modelPositionStep.Xpos;
			rightModelPosition.Ypos += right.modelPositionStep.Ypos;
			rightModelPosition.Zpos += right.modelPositionStep.Zpos;
		}
	}

}
