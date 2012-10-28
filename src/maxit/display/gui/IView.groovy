package maxit.display.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

interface IView {
	public ITextInputListener getTextListener();

	public IClickListener getClickListener();

	public JTextField getInputField();

	public JLabel getScoreH();

	public JLabel getScoreV();

	public JButton getEnterButton();

	public JTextArea getTextArea();

	public JLabel getScoreHLabel();

	public JLabel getScoreVLabel();

	public JButton[] getButtons();

	public JButton getButtonAt(int i, int j);

	public void setTitle(String title);
}
