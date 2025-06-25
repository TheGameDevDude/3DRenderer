package graphics;

import math.Vector2f;
import math.Vector3f;

public class Vertex {
	public Vector3f position;
	public Vector2f texCoords;
	public Vector3f normals;
	public Vector3f tangent;
	public Vector3f bitangent;
	public Vector3f modelPosition;

	public Vertex(Vector3f position, Vector2f texCoords, Vector3f normals) {
		this.position = position;
		this.texCoords = texCoords;
		this.normals = normals;
		tangent = new Vector3f();
		bitangent = new Vector3f();
		modelPosition = new Vector3f();
	}

	public Vertex(Vector3f position, Vector2f texCoords, Vector3f normals, Vector3f modelPosition) {
		this.position = position;
		this.texCoords = texCoords;
		this.normals = normals;
		tangent = new Vector3f();
		bitangent = new Vector3f();
		this.modelPosition = modelPosition;
	}
}
