package maxit.display.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

//import org.apache.log4j.Logger;

public class GuiView extends JFrame implements ActionListener, IView {
	private static final long serialVersionUID = -6385506887416572804L;
	private static Logger log = Logger.getLogger(GuiView.class);

	private JTextField inputField;
	private JLabel scoreH;
	private JLabel scoreV;
	private JButton enterButton;
	private JTextArea textArea;
	private JLabel scoreHLabel;
	private JLabel scoreVLabel;

	private JButton[] buttons;

	private ITextInputListener textListener;
	private IClickListener clickListener;

	private static Color baseColor = new Color(224, 224, 224);

	public GuiView(ITextInputListener textListener, IClickListener clickListener) {
		this.textListener = textListener;
		this.clickListener = clickListener;

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblInput = new JLabel("Input:");
		panel_1.add(lblInput, BorderLayout.WEST);

		inputField = new JTextField();
		panel_1.add(inputField);
		inputField.setColumns(10);
		inputField.addActionListener(this);

		enterButton = new JButton("Envoyer");
		panel_1.add(enterButton, BorderLayout.EAST);
		enterButton.addActionListener(this);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel_2.add(panel, BorderLayout.NORTH);
		panel.setPreferredSize(new Dimension(100, 25));
		panel.setLayout(new MigLayout("", "[50px][8px,grow][50px][8px,grow]",
				"[14px][][][][][][grow]"));

		scoreHLabel = new JLabel("Score H:");
		panel.add(scoreHLabel, "cell 0 0,growx,aligny top");

		scoreH = new JLabel("####");
		panel.add(scoreH, "cell 1 0");

		scoreVLabel = new JLabel("Score V:");
		panel.add(scoreVLabel, "cell 2 0");

		scoreV = new JLabel("####");
		panel.add(scoreV, "cell 3 0");

		JPanel grid = new JPanel();
		grid.setPreferredSize(new Dimension(400, 400));
		panel_2.add(grid);
		grid.setLayout(new GridLayout(8, 8, 0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_2.add(scrollPane_1, BorderLayout.SOUTH);
		scrollPane_1.setPreferredSize(new Dimension(0, 100));

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane_1.setViewportView(textArea);

		buttons = new JButton[64];
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				JButton btnNewButton = new JButton();
				btnNewButton.setMargin(new Insets(2, 2, 2, 2));
				btnNewButton.setPreferredSize(new Dimension(50, 50));
				btnNewButton.addActionListener(this);
				btnNewButton.setBackground(baseColor);
				grid.add(btnNewButton);
				buttons[i + 8 * j] = btnNewButton;
			}
		}

		this.reset();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);
	}

	private void reset() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				buttons[i + 8 * j].setText("" + i + "x" + j);
				buttons[i + 8 * j].setEnabled(true);
			}
		}
		this.scoreH.setText("0");
		this.scoreV.setText("0");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o instanceof JButton) {
			JButton button = (JButton) o;
			if (button == enterButton) {
				onCommandChat();
			} else {
				onClick(button);
			}
		} else if (o instanceof JTextField) {
			onCommandChat();
		}
	}

	private void onCommandChat() {
		String command = inputField.getText();
		inputField.setText("");

		if (textListener != null) {
			textListener.onText(command);
		}
	}

	private void onClick(JButton button) {
		String command = button.getActionCommand();
		if (command.equals("##")) {
			log.debug("invalid button action: " + command);
			return;
		}
		String[] coords = command.split(":");
		if (coords.length != 2) {
			log.debug("invalid button action: " + command);
			return;
		}
		int cx = Integer.parseInt(coords[0]);
		int cy = Integer.parseInt(coords[1]);
		if (clickListener != null) {
			clickListener.onClick(cx, cy);
		}
	}

	public ITextInputListener getTextListener() {
		return textListener;
	}

	public IClickListener getClickListener() {
		return clickListener;
	}

	public JTextField getInputField() {
		return inputField;
	}

	public JLabel getScoreH() {
		return scoreH;
	}

	public JLabel getScoreV() {
		return scoreV;
	}

	public JButton getEnterButton() {
		return enterButton;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public JLabel getScoreHLabel() {
		return scoreHLabel;
	}

	public JLabel getScoreVLabel() {
		return scoreVLabel;
	}

	public JButton[] getButtons() {
		return buttons;
	}

	@Override
	public JButton getButtonAt(int i, int j) {
		return buttons[i + 8 * j];
	}

	public void setTitle(String title) {
		super.setTitle(title);
	}

}
