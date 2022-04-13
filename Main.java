/*
# Pong
A lil game I made to practice (for school) with Java Swing and my game dev skills :P

> 17 May 2021
*/

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JPanel implements KeyListener {
	private static final String TITLE = "le broke pong :DDDD";
	private static final Dimension WINDOW_SIZE = new Dimension(1024, 768);
	private static final int FPS = 60;

	private JFrame window;
	private BufferedImage doubleBuffer;
	private ArrayList<Integer> keyPresses = new ArrayList<Integer>();		// List of all the keys being pressed
	private boolean stopped = false;
	private boolean bootstrapped = false;

	private static final Color BACKGROUND = Color.BLACK;
	private static final Color FOREGROUND = Color.WHITE;
	private static final int BALL_RADIUS = 10;
	private static final int[] PLAYER_SIZE = {20, 100};
	private static final int BORDER_SIZE = 50;
	private static final int[] BALL_FRUSTRATING_ZONE = {60, 120};

	private Ball ball;
	private Rectangle[] players;
	private Rectangle[] borders;
	private ArrayList<Entity> entities = new ArrayList<Entity>();

	/**
	 Game initialization
	*/

	public Main() {
		this.window = new JFrame(TITLE);

		this.window.setSize(WINDOW_SIZE.width, WINDOW_SIZE.height);
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.setResizable(false);
		this.window.setLocationRelativeTo(null);		// Center to screen
		this.window.addKeyListener(this);		// Bind key presses to this class
		this.window.setContentPane(this);
		this.setBackground(BACKGROUND);
		this.setPreferredSize(WINDOW_SIZE);
		this.window.pack();
		this.window.setVisible(true);
	}

	public void setup() {

		// Setup double buffer image to write graphics on before showing them
		this.doubleBuffer = new BufferedImage(WINDOW_SIZE.width, WINDOW_SIZE.height, BufferedImage.TYPE_INT_ARGB);

		/* Entity creation */

		this.ball = new Ball(
			WINDOW_SIZE.width/2 - BALL_RADIUS,
			WINDOW_SIZE.height/2 - BALL_RADIUS,
			BALL_RADIUS, FOREGROUND
		);
		this.players = new Rectangle[]{
			new Rectangle(
				30,
				WINDOW_SIZE.height/2 - PLAYER_SIZE[1]/2,
				PLAYER_SIZE[0], PLAYER_SIZE[1],
				FOREGROUND
			),
			new Rectangle(
				WINDOW_SIZE.width - 30 - PLAYER_SIZE[0],
				WINDOW_SIZE.height/2 - PLAYER_SIZE[1]/2,
				PLAYER_SIZE[0], PLAYER_SIZE[1],
				FOREGROUND
			)
		};

		/* Borders creation */

		this.borders = new Rectangle[]{

			// Top
			new Rectangle(
				0, -BORDER_SIZE,
				WINDOW_SIZE.width, BORDER_SIZE,
				Color.BLACK
			),

			// Right
			new Rectangle(
				WINDOW_SIZE.width, 0,
				BORDER_SIZE, WINDOW_SIZE.height,
				Color.BLACK
			),

			// Bottom
			new Rectangle(
				0, WINDOW_SIZE.height,
				WINDOW_SIZE.width, BORDER_SIZE,
				Color.BLACK
			),

			// Left
			new Rectangle(
				-BORDER_SIZE, 0,
				BORDER_SIZE, WINDOW_SIZE.height,
				Color.BLACK
			)
		};

		// Add entities to the list for collision detection
		this.entities.add(this.ball);
		this.entities.addAll(Arrays.asList(this.players));
		this.entities.addAll(Arrays.asList(this.borders));

		/* Entity setup */

		this.ball.setSpeed(8);

		// Randomize ball direction and position
		this.ball.setDirection(Math.random() * 360);
		this.ball.coordinates.y = Math.random() * (WINDOW_SIZE.height - BALL_RADIUS*2 - 1) + 1;

		this.ball.adjustDirection(BALL_FRUSTRATING_ZONE[0], BALL_FRUSTRATING_ZONE[1]);

		for(Rectangle player : this.players) {
			player.setSpeed(8);
		}

		this.bootstrapped = true;
	}

	public void quit() {
		this.window.setVisible(false);
		this.window.dispose();
		this.stopped = true;
	}

	public boolean hasQuit() {
		return this.stopped;
	}

	/**
	 Event handling
	*/

	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();

		if(keyCode == KeyEvent.VK_ESCAPE) {
			this.quit();
		} else {
			if(! this.keyPresses.contains(keyCode)) {
				this.keyPresses.add(keyCode);
			}
		}
	}

	public void keyReleased(KeyEvent event) {
		this.keyPresses.remove(Integer.valueOf(event.getKeyCode()));
	}

	public void keyTyped(KeyEvent event) {}

	/**
	 Frame processing
	*/

	public void update() {

		/* Check entity collisions */

		boolean canBallStep = true, canPlayerStep = true, canBotStep = true;
		for(Entity entity : this.entities) {

			// FIXME:
			// This is an hack to prevent the ball from glitching out when it hits the
			// corner of the rectangle (like this https://i.imgur.com/kQpXKfS.png).
			// In a nutshell, the rectangle just stops moving :I
			if(! this.players[0].canStep(entity) && entity != this.players[0]) {
				canPlayerStep = false;
			}

			if(! this.ball.canStep(entity) && entity != this.ball) {

				// Check for a collision on top and bottom sides of the enitty
				if(
					this.ball.coordinates.y + this.ball.size.y <= entity.coordinates.y ||
					this.ball.coordinates.y >= entity.coordinates.y + entity.size.y
				) {
					this.ball.setDirection(-this.ball.getDirection());
				
				// Check for a collision on left and right sides
				} else if(
					this.ball.coordinates.x >= entity.coordinates.x + entity.size.x ||
					this.ball.coordinates.x + this.ball.size.x <= entity.coordinates.x
				) {
					this.ball.setDirection(180 - this.ball.getDirection());
				
				// Otherwise it must be the corner of the entity
				} else {
					this.ball.setDirection(180 + this.ball.getDirection());		// Reverse the direction completely
				}

				this.ball.bumpDirection(25);
				this.ball.adjustDirection(BALL_FRUSTRATING_ZONE[0], BALL_FRUSTRATING_ZONE[1]);
				
				// This seems to fix a bug with the ball (?)
				// The balls sometimes reverses its direction when bouncing on the top and bottom
				// borders.. dunno why
				canBallStep = false;
			}
		}

		/* Update entity positions */

		if(canBallStep) {
			this.ball.step();
		}

		// Update entities based on the keys being pressed
		for(int key : this.keyPresses) {
			switch(key) {
				case KeyEvent.VK_UP:
					this.players[0].setDirection(90);
					if(canPlayerStep) {
						this.players[0].step();
					}
					break;
				case KeyEvent.VK_DOWN:
					this.players[0].setDirection(-90);
					if(canPlayerStep) {
						this.players[0].step();
					}
					break;
				default:
					break;
			}
		}

		// TODO: Make a better AI
		this.players[1].coordinates.y = this.ball.coordinates.y + this.ball.size.y/2 - this.players[1].size.y/2;
	}

	public void paint(Graphics context) {
		if(! this.bootstrapped) {
			return;
		}

		Graphics2D buffer = this.doubleBuffer.createGraphics();		// Create graphics buffer

		/* Drawing routine */

		super.paint(buffer);
		for(Entity entity : this.entities) {
			entity.draw(buffer);
		}

		// Write buffer to screen
		((Graphics2D) context).drawImage(this.doubleBuffer, null, 0, 0);
		buffer.dispose();		// Java has memory leaks, wtf?
	}

	public static void main(String[] args) throws Exception {
		Main game = new Main();
		game.setup();

		/* Game loop */

		while(! game.hasQuit()) {
			game.update();
			game.repaint();
			Thread.sleep((long) (((double) 1/FPS) * 1000));
		}
	}
}
