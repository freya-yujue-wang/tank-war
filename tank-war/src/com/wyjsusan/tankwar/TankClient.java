package com.wyjsusan.tankwar;
/**
 * this class is the main window of the game.
 * @author:wyjsusan
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {

	public static final int GAME_WIDTH = 1200;
	public static final int GAME_HEIGHT = 900;

	// create my own tank.
	Tank myTank = new Tank(600, 450, true, Tank.Direction.STOP, this);

	// create a lot of missiles.
	List<Missile> missiles = new ArrayList<>();
	List<Explode> explodes = new ArrayList<>();
	List<Tank> tanks = new ArrayList<>();
	List<Wall> walls = new ArrayList<>();
	Blood b = new Blood();

	Image offScreenImage = null;

	@Override
	public void paint(Graphics g) {
		g.drawString("missiles count:" + missiles.size(), 10, 50);
		g.drawString("explodes count:" + explodes.size(), 10, 70);
		g.drawString("enemytanks count:" + tanks.size(), 10, 90);
		g.drawString("tanks life:" + myTank.getLife(), 10, 110);
		// if enemy was all killed, create new enmey.
		if (tanks.size() <= 0) {
			tanks = generateEnemies(40);
		}
		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(myTank);
			for (Wall wall : walls) {
				m.hitWall(wall);
			}
			m.draw(g);
		}
		for (int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			for (Wall wall : walls) {
				t.collidesWithWall(wall);
			}
			t.collidesWithTanks(tanks);
			t.draw(g);
		}
		myTank.draw(g);
		myTank.eat(b);
		for (Wall wall : walls) {
			wall.draw(g);
		}
		b.draw(g);
	}

	@Override
	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GRAY);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);

		g.drawImage(offScreenImage, 0, 0, null);
	}

	private List<Tank> generateEnemies(int number) {
		List<Tank> tanks = new ArrayList<>();
		for (int i = 0; i < number / 2; i++) {
			tanks.add(new Tank(50 + 40 * (i + 1), 50, false, Tank.Direction.D, this));
		}

		for (int i = 0; i < number / 2; i++) {
			tanks.add(new Tank(800, 100 + 30 * (i + 1), false, Tank.Direction.U, this));
		}
		return tanks;
	}

	public void lauchFrame() {
		// when the game start, add 20 enemy tanks to the board.

		tanks = generateEnemies(20);

		// create two walls to the game to increase the difficulty.
		Wall w1 = new Wall(100, 200, 20, 450, this);
		Wall w2 = new Wall(300, 100, 600, 20, this);
		Wall w3 = new Wall(1000, 200, 20, 450, this);
		Wall w4 = new Wall(200, 800, 600, 20, this);
		walls.add(w1);
		walls.add(w2);
		walls.add(w3);
		walls.add(w4);
		this.setLocation(300, 100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		this.setResizable(false);
		this.setBackground(Color.GRAY);

		this.addKeyListener(new KeyMonitor());
		setVisible(true);

		new Thread(new PaintThread()).start();
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();

	}

	private class PaintThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private class KeyMonitor extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}

	}
}
