package gui;

import java.awt.Panel;
import java.awt.Dimension;

class CustomPanel extends Panel
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