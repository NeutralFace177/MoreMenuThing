package io.bluestaggo.voxelthing.world;

import io.bluestaggo.voxelthing.world.block.Block;
import org.joml.Vector3d;

public class BlockRaycast {
	public static final float STEP_DISTANCE = 1.0f / 16.0f;

	public final Vector3d position;
	public final Vector3d direction;
	public final double length;

	private boolean hit;
	private int hitX, hitY, hitZ;
	private int prevHitX, prevHitY, prevHitZ;
	private Direction hitFace;

	public BlockRaycast(Vector3d position, Vector3d direction, double length) {
		this.position = position;
		this.direction = direction.normalize().mul(STEP_DISTANCE);
		this.length = length;
	}

	void setResult(int hitX, int hitY, int hitZ, Direction hitFace) {
		if (this.hitFace == null) {
			hit = true;
			this.prevHitX = this.hitX;
			this.prevHitY = this.hitY;
			this.prevHitZ = this.hitZ;
			this.hitX = hitX;
			this.hitY = hitY;
			this.hitZ = hitZ;
			this.hitFace = hitFace;
		}
	}

	public int getHitX() {
		return hitX;
	}

	public int getHitY() {
		return hitY;
	}

	public int getHitZ() {
		return hitZ;
	}

	public int getPrevHitX() {
		return prevHitX;
	}

	public int getPrevHitY() {
		return prevHitY;
	}

	public int getPrevHitZ() {
		return prevHitZ;
	}

	public Direction getHitFace() {
		return hitFace;
	}

	public boolean blockHit() {
		return hit;
	}

	//DOES NOT WORK!~!!!
	public boolean sameBlockHit(int thisShitNoWork) {
		return (prevHitX == hitX && prevHitY == hitY && prevHitZ == hitZ);
	}

	public String getDebugText(World world) {
		if (!hit) {
			return Block.ID_AIR.toString();
		}

		Block block = world.getBlock(hitX, hitY, hitZ);
		if (block == null) {
			return Block.ID_AIR.toString();
		}
		return block.id.toString();
	}
}
