package gui;

import java.awt.Dimension;
import javax.swing.JPanel;

class CustomPanel extends JPanel
{

	public CustomPanel(int i, int j)
	{
		dimension = new Dimension(i, j);
		resize(dimension);
		validate();
	}

	public Dimension minimumSize() {
		return dimension;
	}

	public Dimension preferredSize() {
		return size();
	}

	public Dimension dimension;
}