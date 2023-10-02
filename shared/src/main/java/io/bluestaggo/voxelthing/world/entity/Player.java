package io.bluestaggo.voxelthing.world.entity;

import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.world.World;
import org.joml.Vector2d;

public class Player extends Entity {
	private final IPlayerController controller;

	private double prevWalkAmount;
	private double walkAmount;
	private double prevRenderWalk;
	private double renderWalk;
	private double prevWalkDir;
	private double walkDir;
	private double prevFallAmount;
	private double fallAmount;

	private int jumpTimer = 0;
	private boolean wasJumpPressed;
	private boolean jumpPressed;

	public double accel = 0.2;
	public double flightAccel = 0.4;
	public double friction = 0.6;
	public double jumpHeight = 0.5;

	public int health = 20;
	public int maxHealth = 20;

	public boolean survival = true;

	public Player(World world, IPlayerController controller) {
		super(world);
		this.controller = controller;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public void onGameUpdate() {
		rotYaw += controller.moveYaw();
		rotPitch += controller.movePitch();
		rotPitch = MathUtil.clamp(rotPitch, -89.0f, 89.0f);
		jumpPressed |= controller.doJump();
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
		super.update();
		if (survival) {
			hasGravity = true;
			noClip = false;
		}

		if (jumpTimer > 0) {
			jumpTimer--;
		}

		if (!hasGravity) {
			velY *= friction;
			if (controller.doJump()) {
				velY = jumpHeight;
			}
			if (controller.doCrouch()) {
				velY = -jumpHeight;
			}
		}

		double yawRad = Math.toRadians(rotYaw);
		double forward = controller.moveForward();
		double side = controller.moveSide();

		double dist = Math.sqrt(forward * forward + side * side);
		if (dist > 0.0) {
			forward /= dist;
			side /= dist;
		}

		double accel = this.accel;
		if (!hasGravity) {
			accel = flightAccel;
		}

		forward *= accel;
		side *= accel;

		velX += forward * Math.cos(yawRad) + side * Math.cos(yawRad + Math.PI / 2.0);
		velZ += forward * Math.sin(yawRad) + side * Math.sin(yawRad + Math.PI / 2.0);
		velX *= friction;
		velZ *= friction;

		double walkAdd = Math.min(Math.sqrt(velX * velX + velZ * velZ), 1.0);
		if (!onGround) {
			walkAdd /= 2.5;
		}

		prevWalkAmount = walkAmount;
		prevRenderWalk = renderWalk;
		if (hasGravity) {
			walkAmount += walkAdd * 1.5;
			renderWalk = Math.sin(walkAmount) * Math.min(walkAdd * 10.0, 1.0);

			if (onGround && walkAmount - prevWalkAmount < 0.01) {
				walkAmount = 0.0;
			}
		} else {
			walkAmount = 0.0;
			renderWalk = 0.0;
		}

		prevWalkDir = walkDir;
		if (walkAdd > 0.1) {
			walkDir = Math.toDegrees(new Vector2d(velX, velZ).angle(new Vector2d(-1.0f, 0.0f)));
		}

		if ((onGround || noClip) && controller.doJump()) {
			velY = jumpHeight;
		}

		prevFallAmount = fallAmount;
		if (onGround) {
			fallAmount = 0.0;
		}
		fallAmount += (velY - fallAmount) * 0.5;

		if (jumpPressed && !wasJumpPressed) {
			if (jumpTimer > 0) {
				hasGravity = !hasGravity;
				jumpTimer = 0;
			} else {
				jumpTimer = 10;
			}
		}

		wasJumpPressed = jumpPressed;
		jumpPressed = false;
	}

	public double getRenderWalk() {
		return world.scaleToTick(prevRenderWalk, renderWalk);
	}

	public float getRenderWalkDir() {
		return (float) world.scaleToTick(prevWalkDir, walkDir);
	}

	public double getFallAmount() {
		return world.scaleToTick(prevFallAmount, fallAmount);
	}

	@Override
	public float getRenderY() {
		return super.getRenderY() + (float) Math.abs(getRenderWalk() / 3.0);
	}

	@Override
	public int getRenderFrame() {
		int frame = 1;
		double walk = getRenderWalk();

		if (walk >= 0.5) {
			frame++;
		} else if (walk <= -0.5) {
			frame--;
		}

		return frame;
	}

	@Override
	public int getRenderRotation(float cameraAngle) {
		return (int) (MathUtil.floorMod(getRenderWalkDir() + cameraAngle + 22.5f, 360.0f) / 45.0f) % 8;
	}
}
