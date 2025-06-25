package collision;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;

public class Chunk {
	public List<Entity> staticEntities = new ArrayList<Entity>();
	public int chunkSize;

	public Chunk(int chunkSize, List<Entity> otherEntities) {
		this.chunkSize = chunkSize;
		for (Entity entity : otherEntities) {
			staticEntities.add(entity);
		}
	}
}
