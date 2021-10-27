import java.awt.Graphics2D;

public abstract class Entity {
	protected Vector2 coordinates;
	protected Vector2 size;
	private double speed;		// Linear distance in px to travel between frames
	private double direction;		// Degrees

	public Entity(double x, double y, double width, double height) {
		this.coordinates = new Vector2(x, y);
		this.size = new Vector2(width, height);

		// Make the entity still
		this.setSpeed(0);
		this.setDirection(0);		// Face right
	}

	public abstract void draw(Graphics2D graphics);

	// I had to stole this because my previous attempts were disgusting :I
	// Source: https://spicyyoghurt.com/tutorials/html5-javascript-game-development/collision-detection-physics
	public boolean canStep(Entity entity) {
		Vector2 afterStepping = this.coordinates.move(this.speed, this.direction);
		return (
			entity.coordinates.x > afterStepping.x + this.size.x ||
			afterStepping.x > entity.coordinates.x + entity.size.x ||
			entity.coordinates.y > afterStepping.y + this.size.y ||
			afterStepping.y > entity.coordinates.y + entity.size.y
		);
	}

	public void step() {
		this.coordinates = this.coordinates.move(this.speed, this.direction);
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setDirection(double direction) {
		this.direction = -direction;		// The plane is reversed
	}

	public double getSpeed() {
		return this.speed;
	}

	public double getDirection() {
		return -this.direction;
	}
}
