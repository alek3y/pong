import java.awt.Graphics2D;
import java.awt.Color;

public class Ball extends Entity {
	private int diameter;
	private Color color;

	public Ball(double x, double y, int radius, Color color) {
		super(x, y, radius*2, radius*2);
		this.setRadius(radius);
		this.setColor(color);
	}

	public void draw(Graphics2D graphics) {
		Color currentColor = graphics.getColor();
		graphics.setColor(this.color);

		graphics.fillRoundRect(
			(int) this.coordinates.x,
			(int) this.coordinates.y,
			this.diameter, this.diameter,
			this.diameter, this.diameter
		);

		graphics.setColor(currentColor);		// Restore previous context color
	}

	// Change slightly the direction
	public void bumpDirection(double threshold) {
		this.setDirection(Math.random() * threshold*2 - threshold + this.getDirection());
	}

	// The gameplay becomes frustrating if the ball direction is between
	// 60-120 and 240-300 excluded (https://i.imgur.com/8dtYCCa.png)
	public void adjustDirection(double min, double max) {
		this.setDirection(this.getDirection() % 360);		// First ensure it's between 0 and 360

		// Convert negative angle to positive
		if(this.getDirection() < 0) {
			this.setDirection(360 + this.getDirection());
		}

		// Check on both sides of the circle
		// FIXME: My eyes are bleeding please help
		for(int i = 0; i < 2; i++) {
			min = (min + 180*i) % 360;
			max = (max + 180*i) % 360;

			if(this.getDirection() > min && this.getDirection() < max) {
				double difference = this.getDirection() - min;

				if(difference < (max-min)/2) {
					this.setDirection(min);
				} else {
					this.setDirection(max);
				}
			}
		}
	}

	public void setRadius(int radius) {
		this.diameter = radius*2;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRadius() {
		return this.diameter/2;
	}

	public Color getColor() {
		return this.color;
	}
}
