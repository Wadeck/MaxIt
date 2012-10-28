package maxit.display.gui

import groovy.transform.CompileStatic

import java.awt.Color
import java.awt.Font
import javax.swing.JButton

import maxit.commons.core.IClientSendingToServer
import maxit.commons.data.CellConstant
import maxit.commons.data.ErrorType
import maxit.commons.data.IData
import maxit.commons.data.SimpleData
import maxit.display.IDisplay

import org.apache.log4j.Logger

@CompileStatic
public class GuiDisplay implements IClickListener, ITextInputListener, IDisplay {
	private static Logger log = Logger.getLogger(GuiDisplay.class)
	
	private IView view

	private boolean horizontal
	private boolean currTurnIsHorizontal
	private int lastX, lastY
	private int scoreHValue
	private int scoreVValue
	private String otherName
	private IClientSendingToServer client
	private IData data

	private static Color baseColor = new Color(224, 224, 224)
	private static Color currColor = new Color(246, 255, 163)
	private static Color possibleColor = new Color(186, 255, 186)
	private static Color lastClick = new Color(186, 255, 255)
	private static Color blankColor = new Color(255, 255, 255)

	public GuiDisplay() {
		this.view = new GuiView(this, this)
		log.debug "view initialized"
	}

	private void reset() {
		this.data = null
		this.otherName = null
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				view.getButtonAt(i, j).setText("" + i + "x" + j)
				view.getButtonAt(i, j).setEnabled(true)
			}
		}
		view.getScoreH().setText("0")
		view.getScoreV().setText("0")
	}

	@Override
	public void commandStart(String otherName, boolean horizontal, int[] _data) {
		this.horizontal = horizontal
		this.currTurnIsHorizontal = true
		this.data = new SimpleData(_data)
		this.otherName = otherName

		Font tileFont = new Font(view.getScoreHLabel().getFont().getName(), Font.BOLD, 18)
		
		int value
		JButton button
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				button = view.getButtonAt(i, j)
				value = data.getDataAt(i, j)
				if (value == CellConstant.START) {
					button.setText("")
					lastX = i
					lastY = j
				} else if (value == CellConstant.TAKEN) {
					button.setText("##")
				} else {
					button.setText(value + "")
					button.setActionCommand(i + ":" + j)
				}
				button.setFont(tileFont)
			}
		}

		this.showLastOne(lastX, lastY)
		if (horizontal) {
			this.showPossible(lastX, lastY)
		}

		Font font = new Font(view.getScoreHLabel().getFont().getName(), Font.BOLD,
				view.getScoreHLabel().getFont().getSize())
		if (horizontal) {
			view.getScoreHLabel().setText(client.getPlayerName() + " :")
			view.getScoreHLabel().setFont(font)
			view.getScoreVLabel().setText(otherName + " :")

		} else {
			view.getScoreHLabel().setText(otherName + " :")
			view.getScoreVLabel().setText(client.getPlayerName() + " :")
			view.getScoreVLabel().setFont(font)
		}

		println("Game starts !")
		println("=============")
	}

	
	@Override
	public void commandChat(String from, String message) {
		String symbol
		if (from == otherName) {
			symbol = otherName + " < "
		} else {
			symbol = "> "
		}
		String condensed = symbol + message
		println(condensed)
	}

	@Override
	public void commandNotifyMove(int x, int y, int error, int scoreH,
			int scoreV) {
		boolean isEnd = error == ErrorType.END
		boolean isCorrect = error == ErrorType.CORRECT

		if (isEnd || isCorrect) {
			this.data.setDataAt(x, y, CellConstant.TAKEN)

			this.showLast(x, y, currTurnIsHorizontal == horizontal)

			currTurnIsHorizontal = !currTurnIsHorizontal

			this.scoreHValue = scoreH
			this.scoreVValue = scoreV

			view.getScoreH().text = scoreHValue + ""
			view.getScoreV().text = scoreVValue + ""

			if (isEnd) {
				println("End")
			}
		} else {
			String errMessage = ErrorType.getMessage(error)
			if (error != null) {
				println("Error: " + errMessage)
			} else {
				println("Error num: " + error)
			}
		}
	}

	@Override
	public void setClient(IClientSendingToServer client) {
		this.client = client

		println("Connected, waiting for other")
		view.setTitle(client.getPlayerName())
	}

	@Override
	public void onText(String text) {
		client.sendChat(text)
	}

	@Override
	public void onClick(int i, int j) {
		client.sendCoord(i, j)
	}

	private void println(String msg) {
		view.getTextArea().append(msg + "\n")
		view.getTextArea().setCaretPosition(view.getTextArea().getText().length() - 1)
	}
	
	private void hideOldLast(int cx, int cy) {
		view.getButtonAt(cx, cy).setEnabled(false)
		view.getButtonAt(cx, cy).setBackground(blankColor)
	}

	private void showLastOne(int cx, int cy) {
		view.getButtonAt(cx, cy).setBackground(currColor)
	}

	private void showLast(int nextX, int nextY, boolean wasMyTurn) {
		// disable old last
		if (wasMyTurn) {
			this.hidePossible(nextX, nextY)
		}
		this.hideOldLast(lastX, lastY)

		// highlight the last one
		this.showLastOne(nextX, nextY)
		lastX = nextX
		lastY = nextY

		if (!wasMyTurn) {
			this.showPossible(nextX, nextY)
		}
	}

	private void showPossible(int nextX, int nextY) {
		if (horizontal) {
			// highlight the row
			for (int i = 0; i < 8; i++) {
				if (CellConstant.isPlayableValue(data.getDataAt(i, nextY))){
					view.getButtonAt(i, nextY).setBackground(possibleColor)
				}
			}
		} else {
			// highlight the column
			for (int j = 0; j < 8; j++) {
				if (CellConstant.isPlayableValue(data.getDataAt(nextX, j))) {
					view.getButtonAt(nextX, j).setBackground(possibleColor)
				}
			}
		}
	}

	/**
	 * Just after click
	 * 
	 * @param nextX
	 * @param nextY
	 */
	private void hidePossible(int nextX, int nextY) {
		if (horizontal) {
			for (int i = 0; i < 8; i++) {
				if (i == nextX) {
					view.getButtonAt(i, nextY).setBackground(lastClick)
				} else {
					if (data.getDataAt(i, nextY) == CellConstant.TAKEN
							|| data.getDataAt(i, nextY) == CellConstant.START) {

					} else {
						view.getButtonAt(i, nextY).setBackground(baseColor)
					}
				}
			}
		} else {
			for (int j = 0; j < 8; j++) {
				if (j == nextY) {
					view.getButtonAt(nextX, j).setBackground(lastClick)
				} else {
					if (data.getDataAt(nextX, j) == CellConstant.TAKEN
							|| data.getDataAt(nextX, j) == CellConstant.START) {

					} else {
						view.getButtonAt(nextX, j).setBackground(baseColor)
					}
				}
			}
		}
	}
}
