public class Vector2 {
	public double x;
	public double y;

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 round() {
		return new Vector2(
			Math.round(this.x),
			Math.round(this.y)
		);
	}

	public Vector2 move(double distance, double direction) {
		return new Vector2(
			this.x + Math.cos(Math.toRadians(direction)) * distance,
			this.y + Math.sin(Math.toRadians(direction)) * distance
		);
	}

	public double distance(Vector2 point) {
		return Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));		// Pitagora's theorem
	}
}
