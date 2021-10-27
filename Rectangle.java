import java.awt.Graphics2D;
import java.awt.Color;

public class Rectangle extends Entity {
	private Color color;

	public Rectangle(double x, double y, double width, double height, Color color) {
		super(x, y, width, height);
		this.setColor(color);
	}

	public void draw(Graphics2D graphics) {
		Color currentColor = graphics.getColor();
		graphics.setColor(this.color);

		graphics.fillRect(
			(int) this.coordinates.x,
			(int) this.coordinates.y,
			(int) this.size.x,
			(int) this.size.y
		);

		graphics.setColor(currentColor);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}
}

