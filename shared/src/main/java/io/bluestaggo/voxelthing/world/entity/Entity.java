package io.bluestaggo.voxelthing.world.entity;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.voxelthing.math.AABB;
import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.world.World;

import java.util.List;

public class Entity {
	public final World world;

	protected String texture;

	private final AABB collisionBox = new AABB();
	private final AABB offsetBox = new AABB();
	public double width = 0.4;
	public double height = 1.8;

	private double prevPosX;
	private double prevPosY;
	private double prevPosZ;
	public double posX;
	public double posY;
	public double posZ;

	private double prevVelX;
	private double prevVelY;
	private double prevVelZ;
	public double velX;
	public double velY;
	public double velZ;

	private double prevRotPitch;
	private double prevRotYaw;
	public double rotPitch;
	public double rotYaw;

	private boolean wasOnGround;
	public boolean onGround;
	public boolean noClip;
	protected boolean hasGravity = true;

	public int health = 20;
	public int maxHealth = 20;

	public Entity(World world) {
		this.world = world;
		texture = "/assets/entities/template.png";
		posY = 64.0;
	}

	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevVelX = velX;
		prevVelY = velY;
		prevVelZ = velZ;
		prevRotYaw = rotYaw;
		prevRotPitch = rotPitch;

		update();
		updateMovement();
	}

	public int damage(int amount) {
		health -= amount;
		health = health < 0 ? health : 0;
		return health;
	}

	public int heal(int amount) {
		health += amount;
		health = health > maxHealth ? health : maxHealth;
		return health;
	}

	protected void update() {
		if (hasGravity && velY > -4.0) {
			velY -= 0.1;
		}
	}

	private void updateCollisionBox() {
		collisionBox.setBounds(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width, null);
	}

	private void updateMovement() {
		wasOnGround = onGround;
		if (!noClip) {
			updateCollisionBox();
			List<AABB> intersectingBoxes = world.getSurroundingCollision(collisionBox.expandToPoint(velX, velY, velZ, offsetBox));

			double oldVelY = velY;

			for (AABB box : intersectingBoxes) {
				velY = box.calcYOffset(collisionBox, velY);
			}
			collisionBox.offset(0.0, velY, 0.0, collisionBox);

			for (AABB box : intersectingBoxes) {
				velX = box.calcXOffset(collisionBox, velX);
			}
			collisionBox.offset(velX, 0.0, 0.0, collisionBox);

			for (AABB box : intersectingBoxes) {
				velZ = box.calcZOffset(collisionBox, velZ);
			}
			collisionBox.offset(0.0, 0.0, velZ, collisionBox);

			onGround = oldVelY < 0.0 && oldVelY < velY;
		} else {
			onGround = false;
		}

		posX += velX;
		posY += velY;
		posZ += velZ;
		updateCollisionBox();
	}

	public boolean justLanded() {
		return onGround && !wasOnGround;
	}

	public boolean justFell() {
		return wasOnGround && !onGround;
	}

	public double getPartialX() {
		return MathUtil.lerp(prevPosX, posX, world.partialTick);
	}

	public double getPartialY() {
		return MathUtil.lerp(prevPosY, posY, world.partialTick);
	}

	public double getPartialZ() {
		return MathUtil.lerp(prevPosZ, posZ, world.partialTick);
	}

	public float getRenderX() {
		return (float) getPartialX();
	}

	public float getRenderY() {
		return (float) getPartialY();
	}

	public float getRenderZ() {
		return (float) getPartialZ();
	}

	public int getBlockX() {
		return (int) Math.floor(posX);
	}

	public int getBlockY() {
		return (int) Math.floor(posY);
	}

	public int getBlockZ() {
		return (int) Math.floor(posZ);
	}

	public double getPartialVelX() {
		return MathUtil.lerp(prevVelX, velX, world.partialTick);
	}

	public double getPartialVelY() {
		return MathUtil.lerp(prevVelY, velY, world.partialTick);
	}

	public double getPartialVelZ() {
		return MathUtil.lerp(prevVelZ, velZ, world.partialTick);
	}

	public float getRenderYaw() {
		return (float) MathUtil.lerp(prevRotYaw, rotYaw, world.partialTick);
	}

	public float getRenderPitch() {
		return (float) MathUtil.lerp(prevRotPitch, rotPitch, world.partialTick);
	}

	public float getRenderWidth() {
		return 2.0f;
	}

	public float getRenderHeight() {
		return 2.0f;
	}

	public int getRenderFrame() {
		return 0;
	}

	public int getRenderRotation(float cameraAngle) {
		return (int) (MathUtil.floorMod(getRenderYaw() - 180.0f - cameraAngle + 22.5f, 360.0f) / 45.0f) % 8;
	}

	public String getTexture() {
		return texture;
	}

	public CompoundItem serialize() {
		var data = new CompoundItem();
		data.setDouble("posX", posX);
		data.setDouble("posY", posY);
		data.setDouble("posZ", posZ);
		data.setDouble("velX", velX);
		data.setDouble("velY", velY);
		data.setDouble("velZ", velZ);
		data.setDouble("rotYaw", rotYaw);
		data.setDouble("rotPitch", rotPitch);
		return data;
	}

	public void deserialize(CompoundItem data) {
		prevPosX = posX = data.getDouble("posX");
		prevPosY = posY = data.getDouble("posY");
		prevPosZ = posZ = data.getDouble("posZ");
		prevVelX = velX = data.getDouble("velX");
		prevVelY = velY = data.getDouble("velY");
		prevVelZ = velZ = data.getDouble("velZ");
		prevRotYaw = rotYaw = data.getDouble("rotYaw");
		prevRotPitch = rotPitch = data.getDouble("rotPitch");
	}
}
