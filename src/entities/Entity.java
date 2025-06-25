package entities;

import java.util.List;
import collision.AABB;
import graphics.Bitmap;
import graphics.Render3D;
import graphics.Triangle;
import graphics.Vertex;
import math.Matrix4f;
import math.Vector3f;
import model.Mesh;

public class Entity {
	public Vector3f position;
	public float Xrot;
	public float Yrot;
	public float Zrot;
	public Vector3f scale;
	public Mesh mesh;
	private Bitmap texture;
	private Bitmap emissiveTexture;
	private Bitmap normalTexture;
	private Bitmap displacementTexture;
	private boolean isGlass;
	public AABB aabb;

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture, AABB aabb) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = null;
		this.normalTexture = null;
		this.displacementTexture = null;
		this.isGlass = false;

		if (aabb != null) {
			aabb.farLeft = aabb.farLeft.multiply(new Matrix4f().scale(scale));
			aabb.nearRight = aabb.nearRight.multiply(new Matrix4f().scale(scale));
			aabb.farLeft.Xpos += position.Xpos;
			aabb.farLeft.Ypos += position.Ypos;
			aabb.farLeft.Zpos += position.Zpos;
			aabb.nearRight.Xpos += position.Xpos;
			aabb.nearRight.Ypos += position.Ypos;
			aabb.nearRight.Zpos += position.Zpos;
			this.aabb = new AABB(aabb.farLeft, aabb.nearRight);
		} else {
			this.aabb = null;
		}
	}

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture, Bitmap emissiveTexture, AABB aabb) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = emissiveTexture;
		this.normalTexture = null;
		this.displacementTexture = null;
		this.isGlass = false;

		if (aabb != null) {
			aabb.farLeft = aabb.farLeft.multiply(new Matrix4f().scale(scale));
			aabb.nearRight = aabb.nearRight.multiply(new Matrix4f().scale(scale));
			aabb.farLeft.Xpos += position.Xpos;
			aabb.farLeft.Ypos += position.Ypos;
			aabb.farLeft.Zpos += position.Zpos;
			aabb.nearRight.Xpos += position.Xpos;
			aabb.nearRight.Ypos += position.Ypos;
			aabb.nearRight.Zpos += position.Zpos;
			this.aabb = new AABB(aabb.farLeft, aabb.nearRight);
		} else {
			this.aabb = null;
		}
	}

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture, Bitmap normalTexture, Bitmap displacementTexture, AABB aabb) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = null;
		this.normalTexture = normalTexture;
		this.displacementTexture = displacementTexture;
		this.isGlass = false;

		if (aabb != null) {
			aabb.farLeft = aabb.farLeft.multiply(new Matrix4f().scale(scale));
			aabb.nearRight = aabb.nearRight.multiply(new Matrix4f().scale(scale));
			aabb.farLeft.Xpos += position.Xpos;
			aabb.farLeft.Ypos += position.Ypos;
			aabb.farLeft.Zpos += position.Zpos;
			aabb.nearRight.Xpos += position.Xpos;
			aabb.nearRight.Ypos += position.Ypos;
			aabb.nearRight.Zpos += position.Zpos;
			this.aabb = new AABB(aabb.farLeft, aabb.nearRight);
		} else {
			this.aabb = null;
		}
	}

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture, Bitmap emissiveTexture, boolean isGlass, AABB aabb) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = emissiveTexture;
		this.normalTexture = null;
		this.displacementTexture = null;
		this.isGlass = isGlass;

		if (aabb != null) {
			aabb.farLeft = aabb.farLeft.multiply(new Matrix4f().scale(scale));
			aabb.nearRight = aabb.nearRight.multiply(new Matrix4f().scale(scale));
			aabb.farLeft.Xpos += position.Xpos;
			aabb.farLeft.Ypos += position.Ypos;
			aabb.farLeft.Zpos += position.Zpos;
			aabb.nearRight.Xpos += position.Xpos;
			aabb.nearRight.Ypos += position.Ypos;
			aabb.nearRight.Zpos += position.Zpos;
			this.aabb = new AABB(aabb.farLeft, aabb.nearRight);
		} else {
			this.aabb = null;
		}
	}

	public void renderXYZ(Render3D render3D, int[] screenPixels, List<Light> lights, Camera camera) {
		if (emissiveTexture == null && normalTexture == null && displacementTexture == null) {
			for (Triangle triangle : mesh.triangles) {
				Vertex a = new Vertex(triangle.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.a.texCoords, triangle.a.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex b = new Vertex(triangle.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.b.texCoords, triangle.b.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex c = new Vertex(triangle.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.c.texCoords, triangle.c.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				a.modelPosition = new Vector3f(a.position);
				b.modelPosition = new Vector3f(b.position);
				c.modelPosition = new Vector3f(c.position);
				render3D.renderTriangle(a, b, c, texture, lights, camera, screenPixels);
			}
		} else if (normalTexture != null && emissiveTexture == null) {
			for (Triangle triangle : mesh.triangles) {
				Vertex a = new Vertex(triangle.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.a.texCoords, triangle.a.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				Vertex b = new Vertex(triangle.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.b.texCoords, triangle.b.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				Vertex c = new Vertex(triangle.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.c.texCoords, triangle.c.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				a.tangent = triangle.a.tangent.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.bitangent = triangle.a.bitangent.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				a.modelPosition = new Vector3f(a.position);
				b.modelPosition = new Vector3f(b.position);
				c.modelPosition = new Vector3f(c.position);
				render3D.renderTriangle(a, b, c, texture, normalTexture, displacementTexture, lights, camera, screenPixels);
			}
		} else if (emissiveTexture != null) {
			for (Triangle triangle : mesh.triangles) {
				Vertex a = new Vertex(triangle.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.a.texCoords, triangle.a.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex b = new Vertex(triangle.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.b.texCoords, triangle.b.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex c = new Vertex(triangle.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.c.texCoords, triangle.c.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				a.modelPosition = new Vector3f(a.position);
				b.modelPosition = new Vector3f(b.position);
				c.modelPosition = new Vector3f(c.position);
				if (isGlass == true) {
					Render3D.isGlass = true;
				}
				render3D.renderTriangle(a, b, c, texture, emissiveTexture, lights, camera, screenPixels);
				Render3D.isGlass = false;
			}
		}
	}

	public void renderXYZ_Parallel(Render3D render3D, int[] screenPixels, List<Light> lights, Camera camera) {
		if (emissiveTexture == null && normalTexture == null && displacementTexture == null) {
			mesh.triangles.parallelStream().forEach(s -> {
				Vertex a = new Vertex(s.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), s.a.texCoords, s.a.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex b = new Vertex(s.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), s.b.texCoords, s.b.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex c = new Vertex(s.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), s.c.texCoords, s.c.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				a.modelPosition = new Vector3f(a.position);
				b.modelPosition = new Vector3f(b.position);
				c.modelPosition = new Vector3f(c.position);
				render3D.renderTriangle(a, b, c, texture, lights, camera, screenPixels);
			});
		} else if (normalTexture != null && emissiveTexture == null) {
			mesh.triangles.parallelStream().forEach(s -> {
				Vertex a = new Vertex(s.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), s.a.texCoords, s.a.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				Vertex b = new Vertex(s.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), s.b.texCoords, s.b.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				Vertex c = new Vertex(s.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), s.c.texCoords, s.c.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				a.tangent = s.a.tangent.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.bitangent = s.a.bitangent.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				a.modelPosition = new Vector3f(a.position);
				b.modelPosition = new Vector3f(b.position);
				c.modelPosition = new Vector3f(c.position);
				render3D.renderTriangle(a, b, c, texture, normalTexture, displacementTexture, lights, camera, screenPixels);
			});
		}
	}

}
