package view;

import controller.ClientController;
import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GamePanel extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelNotification p1;
	private PanelPlayer p2;

	private GameFrame gameFrame;

	private GameController gameController;

	private int w;
	private int h;
	private int boom;

	public GamePanel(int w, int h, int boom, GameFrame gameFrame) {

		this.gameFrame = gameFrame;

		this.boom = boom;
		this.w = w;
		this.h = h;

		gameController = new GameController(w, h, boom, this);

		setLayout(new BorderLayout(20, 20));

		add(p1 = new PanelNotification(this), BorderLayout.NORTH);
		add(p2 = new PanelPlayer(this), BorderLayout.CENTER);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		getP1().getBt().setStage(ButtonSmile.wow);
		getP1().getBt().repaint();
		ButtonPlay[][] arrayButton = p2.getArrayButton();
		for (int i = 0; i < arrayButton.length; i++) {
			for (int j = 0; j < arrayButton[i].length; j++) {
				if (e.getButton() == 1 && e.getSource() == arrayButton[i][j] && !gameController.getArrayCamCo()[i][j]) {

					if (!getP1().getTime().isRunning()) {
						getP1().getTime().start();
					}

					if (!gameController.open(i, j)) {

						if (gameController.isComplete()) {

							getP1().getTime().stop();
							getP1().getBt().setStage(ButtonSmile.lose);
							getP1().getBt().repaint();

							int option = JOptionPane.showConfirmDialog(this, "Bạn đã thua có muốn tiếp tục chơi?", "Notification",
									JOptionPane.YES_NO_OPTION);
							if (option == JOptionPane.YES_OPTION) {
//								gameFrame.setVisible(false);
//								new GameFrame(w, h, boom);
                                                                ClientController.getInstance().sendData("LOSE_GAME", ClientController.getInstance().oppName);
							} else {
								gameController.fullTrue();
							}
						} else if (gameController.isEnd()) {

							getP1().getTime().stop();
							getP1().getBt().setStage(ButtonSmile.win);
							getP1().getBt().repaint();

							int option = JOptionPane.showConfirmDialog(this, "Bạn đã thắng có muốn tiếp tục chơi", "Notification",
									JOptionPane.YES_NO_OPTION);
							if (option == JOptionPane.YES_OPTION) {
//								gameFrame.setVisible(false);
//								new GameFrame(w, h, boom);
                                                                ClientController.getInstance().sendData("WIN_GAME", ClientController.getInstance().oppName);
							}
						}
					}
				} else if (e.getButton() == 3 && e.getSource() == arrayButton[i][j]) {
					gameController.camCo(i, j);
				}
				if (e.getClickCount() == 2 && e.getSource() == arrayButton[i][j] && gameController.getArrayBoolean()[i][j]) {
					if (!gameController.clickDouble(i, j)) {

						int option = JOptionPane.showConfirmDialog(this, "Bạn đã thua có muốn tiếp tục không", "Notification",
								JOptionPane.YES_NO_OPTION);

						if (option == JOptionPane.YES_OPTION) {
							gameFrame.setVisible(false);
							new GameFrame(w, h, boom);
						} else {
							gameController.fullTrue();
						}
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		getP1().getBt().setStage(ButtonSmile.now);
		getP1().getBt().repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public GameController getWorld() {
		return gameController;
	}

	public void setWorld(GameController world) {
		this.gameController = world;
	}

	public GameFrame getGameFrame() {
		return gameFrame;
	}

	public void setGameFrame(GameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	public int getBoom() {
		return boom;
	}

	public void setBoom(int boom) {
		this.boom = boom;
	}

	public PanelNotification getP1() {
		return p1;
	}

	public void setP1(PanelNotification p1) {
		this.p1 = p1;
	}

	public PanelPlayer getP2() {
		return p2;
	}

	public void setP2(PanelPlayer p2) {
		this.p2 = p2;
	}
}
