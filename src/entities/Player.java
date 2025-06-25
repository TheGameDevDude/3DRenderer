package entities;

import java.util.List;
import collision.AABB;
import collision.CollisionResolutionAABB;
import input.Keyboard;
import input.Mouse;
import mainGameLoop.Main;
import math.Vector3f;

public class Player extends Camera {
	public Vector3f playerPosition;
	public AABB aabb;

	private static float playerSpeed = 0.05f;
	private static float playerHeight = 1.0f;
	private static float gravity = 0.005f;
	private static float jumpForce = 0.1f;

	private boolean front;
	private boolean back;
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;

	private boolean jump = false;
	private float acceleration = 0.0f;

	public Player(Vector3f playerPosition) {
		super(new Vector3f(), 90, 0);
		this.playerPosition = playerPosition;
		aabb = new AABB(new Vector3f(playerPosition.Xpos - 0.1f, playerPosition.Ypos + playerHeight + 0.1f, playerPosition.Zpos - 0.1f), new Vector3f(playerPosition.Xpos + 0.1f, playerPosition.Ypos, playerPosition.Zpos + 0.1f));
	}

	public void tick(Mouse mouse, Keyboard key, List<Entity> entities) {
		mouseControl(mouse);
		playerKeyboardControl(key, entities);
	}

	public void render() {

	}

	private void playerKeyboardControl(Keyboard key, List<Entity> entities) {
		float sin = (float) Math.sin(Math.toRadians(angleY));
		float cos = (float) Math.cos(Math.toRadians(angleY));

		float Xdir = 0;
		float Zdir = 0;

		if (key.front) {
			Zdir = Main.getFrameTime() * cos * playerSpeed;
			Xdir = Main.getFrameTime() * sin * playerSpeed;
		} else if (key.back) {
			Zdir = -Main.getFrameTime() * cos * playerSpeed;
			Xdir = -Main.getFrameTime() * sin * playerSpeed;
		}

		if (key.left) {
			Zdir += Main.getFrameTime() * sin * playerSpeed;
			Xdir -= Main.getFrameTime() * cos * playerSpeed;
		} else if (key.right) {
			Zdir -= Main.getFrameTime() * sin * playerSpeed;
			Xdir += Main.getFrameTime() * cos * playerSpeed;
		}

		playerPosition.Xpos += Xdir;
		updatePlayerAABB();
		if (Xdir >= 0) {
			right = true;
			left = false;
		} else {
			right = false;
			left = true;
		}
		entities.parallelStream().forEach(s -> resolveCollisioninXdirection(s));
		updatePlayerAABB();
		playerPosition.Zpos += Zdir;
		updatePlayerAABB();
		if (Zdir >= 0) {
			front = true;
			back = false;
		} else {
			front = false;
			back = true;
		}
		entities.parallelStream().forEach(s -> resolveCollisioninZdirection(s));
		updatePlayerAABB();

		if (jump == true) {
			playerPosition.Ypos += jumpForce * Main.getFrameTime();
			up = true;
			down = false;
			updatePlayerAABB();
			entities.parallelStream().forEach(s -> resolveCollisioninYdirection(s));
			updatePlayerAABB();
		}

		acceleration += gravity * Main.getFrameTime();
		playerPosition.Ypos -= acceleration * Main.getFrameTime();
		down = true;
		up = false;
		updatePlayerAABB();
		entities.parallelStream().forEach(s -> resolveCollisioninYdirection(s));
		updatePlayerAABB();

		if (playerPosition.Ypos < 1.0f) {
			playerPosition.Ypos = 1.0f;
			updatePlayerAABB();
			acceleration = 0.0f;
			jump = false;
		}

		if (key.up && jump == false) {
			jump = true;
		}

		cameraPosition.Xpos = playerPosition.Xpos;
		cameraPosition.Ypos = playerPosition.Ypos + playerHeight;
		cameraPosition.Zpos = playerPosition.Zpos;
	}

	private void updatePlayerAABB() {
		aabb = new AABB(new Vector3f(playerPosition.Xpos - 0.1f, playerPosition.Ypos + playerHeight + 0.1f, playerPosition.Zpos + 0.1f), new Vector3f(playerPosition.Xpos + 0.1f, playerPosition.Ypos, playerPosition.Zpos - 0.1f));
	}

	private void resolveCollisioninXdirection(Entity entity) {
		if (entity.aabb != null) {
			if (aabb.check(entity.aabb) == true) {
				if (right == true) {
					float Xoffset = aabb.nearRight.Xpos - entity.aabb.farLeft.Xpos;
					playerPosition.Xpos -= (Xoffset + 0.001f);
				} else if (left == true) {
					float Xoffset = entity.aabb.nearRight.Xpos - aabb.farLeft.Xpos;
					playerPosition.Xpos += (Xoffset + 0.001f);
				}
			}
		}
	}

	private void resolveCollisioninZdirection(Entity entity) {
		if (entity.aabb != null) {
			if (aabb.check(entity.aabb) == true) {
				if (front == true) {
					float Zoffset = aabb.farLeft.Zpos - entity.aabb.nearRight.Zpos;
					playerPosition.Zpos -= (Zoffset + 0.001f);
				} else if (back == true) {
					float Zoffset = entity.aabb.farLeft.Zpos - aabb.nearRight.Zpos;
					playerPosition.Zpos += (Zoffset + 0.001f);
				}
			}
		}
	}

	private void resolveCollisioninYdirection(Entity entity) {
		if (entity.aabb != null) {
			if (aabb.check(entity.aabb) == true) {
				if (up == true) {
					float Yoffset = aabb.farLeft.Ypos - entity.aabb.nearRight.Ypos;
					playerPosition.Ypos -= (Yoffset + 0.001f);
					acceleration += jumpForce;
				} else if (down == true) {
					float Yoffset = entity.aabb.farLeft.Ypos - aabb.nearRight.Ypos;
					playerPosition.Ypos += (Yoffset + 0.001f);
					acceleration = 0;
					jump = false;
				}
			}
		}
	}

	public void resolveCollision(Entity entity) {
		CollisionResolutionAABB[] dir = new CollisionResolutionAABB[6];

		float left = Math.abs(aabb.nearRight.Xpos - entity.aabb.farLeft.Xpos);
		float right = Math.abs(aabb.farLeft.Xpos - entity.aabb.nearRight.Xpos);
		float front = Math.abs(aabb.farLeft.Zpos - entity.aabb.nearRight.Zpos);
		float back = Math.abs(aabb.nearRight.Zpos - entity.aabb.farLeft.Zpos);
		float up = Math.abs(aabb.nearRight.Ypos - entity.aabb.farLeft.Ypos);
		float down = Math.abs(aabb.farLeft.Ypos - entity.aabb.nearRight.Ypos);

		dir[0] = new CollisionResolutionAABB(left, 0);
		dir[1] = new CollisionResolutionAABB(right, 1);
		dir[2] = new CollisionResolutionAABB(front, 2);
		dir[3] = new CollisionResolutionAABB(back, 3);
		dir[4] = new CollisionResolutionAABB(up, 4);
		dir[5] = new CollisionResolutionAABB(down, 5);

		for (int i = 0; i < 5; i++) {
			for (int j = i + 1; j < 6; j++) {
				if (dir[j].distance < dir[i].distance) {
					CollisionResolutionAABB temp;
					temp = dir[j];
					dir[j] = dir[i];
					dir[i] = temp;
				}
			}
		}

		if (dir[0].dir == 0) {
			playerPosition.Xpos -= dir[0].distance;
		} else if (dir[0].dir == 1) {
			playerPosition.Xpos += dir[0].distance;
		} else if (dir[0].dir == 2) {
			playerPosition.Zpos -= dir[0].distance;
		} else if (dir[0].dir == 3) {
			playerPosition.Zpos += dir[0].distance;
		} else if (dir[0].dir == 4) {
			acceleration = 0;
			playerPosition.Ypos += dir[0].distance;
			jump = false;
		} else if (dir[0].dir == 5) {
			playerPosition.Ypos -= dir[0].distance;
			acceleration += jumpForce;
		}

	}

}
