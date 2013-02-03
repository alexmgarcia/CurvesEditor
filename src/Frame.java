import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 *         This class represents a JFrame frame It will contain the menus of the
 *         application
 */
public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	// Panel to be used to call some methods
	private Panel panel;

	// Initial width and height of the window
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	// Items that can be activated/disabled by an external call (from panel)
	private List<JMenuItem> activableItems;
	private JMenuItem printItem;

	/**
	 * Constructs a Frame
	 */
	public Frame() {
		setSize(WIDTH, HEIGHT);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createPointsMenu());
		menuBar.add(createCurvesMenu());
		menuBar.add(createOptionsMenu());
		menuBar.add(createHelpMenu());
		setJMenuBar(menuBar);
		panel = new Panel(this);
		setContentPane(panel);
	}

	/**
	 * Creates the File menu
	 * 
	 * @return the created menu
	 */
	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createFileMenuItem("New"));
		printItem = createFileMenuItem("Print to PostScript");
		printItem.setEnabled(false);
		menu.add(printItem);
		menu.add(new JSeparator());
		menu.add(createFileMenuItem("Exit"));
		return menu;
	}

	/**
	 * Shows a Message Dialog box with success state
	 * 
	 * @param success
	 *            success state
	 */
	public void showInformationMessage(boolean success) {
		if (success)
			JOptionPane
					.showMessageDialog(null,
							"The print was successful.\nIt was written to file Print.ps");
		else
			JOptionPane
					.showMessageDialog(null,
							"There was an error while printing the selected area.\nPlease try again");
	}

	/**
	 * Creates an item in File menu
	 * 
	 * @param text
	 *            text of the item
	 * @return the created item
	 */
	private JMenuItem createFileMenuItem(String text) {
		JMenuItem item = new JMenuItem(text);
		class ListenerItemMenu implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("New")) {
					disableItems();
					panel.clearDraw();
				} else if (e.getActionCommand().equals("Print to PostScript")) {
					JOptionPane
							.showMessageDialog(
									null,
									"Please select the print area by drawing a rectangle\nclicking in initial point and end point of the desired\nrectangle area");
					panel.activatePrintingBox();
					disableItemsSaveState();
					disablePrintItem();
				} else if (e.getActionCommand().equals("Exit"))
					System.exit(0);
			}
		}

		item.addActionListener(new ListenerItemMenu());
		return item;
	}

	/**
	 * Creates the Points menu
	 * 
	 * @return the created menu
	 */
	private JMenu createPointsMenu() {
		JMenu menu = new JMenu("Points of polyline");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem menuItem = createPointsMenuItem("4 points");
		menuItem.setSelected(true);
		group.add(menuItem);
		menu.add(menuItem);
		menuItem = createPointsMenuItem("7 points");
		group.add(menuItem);
		menu.add(menuItem);
		menuItem = createPointsMenuItem("10 points");
		group.add(menuItem);
		menu.add(menuItem);
		return menu;
	}

	/**
	 * Creates the Curves menu
	 * 
	 * @return the created menu
	 */
	private JMenu createCurvesMenu() {
		activableItems = new LinkedList<JMenuItem>();
		JMenu menu = new JMenu("Curves");
		JCheckBoxMenuItem item;
		item = createCurvesMenuItem("Bézier");
		item.setSelected(false);
		item.setEnabled(false);
		menu.add(item);
		activableItems.add(item);
		item = createCurvesMenuItem("B-spline");
		item.setSelected(false);
		item.setEnabled(false);
		menu.add(item);
		activableItems.add(item);
		item = createCurvesMenuItem("Catmull-Rom");
		item.setSelected(false);
		item.setEnabled(false);
		menu.add(item);
		activableItems.add(item);
		return menu;
	}

	/**
	 * Creates the Options menu
	 * 
	 * @return the created menu
	 */
	private JMenu createOptionsMenu() {
		JMenu menu = new JMenu("Options");
		JMenuItem item;
		item = createOptionsMenuItem("Convex Hull");
		item.setEnabled(false);
		item.setSelected(false);
		activableItems.add(item);
		menu.add(item);
		item = createOptionsMenuItem("Polyline");
		item.setSelected(true);
		item.setEnabled(false);
		activableItems.add(item);
		menu.add(item);
		return menu;
	}

	/**
	 * Creates the Help menu
	 * 
	 * @return the created menu
	 */
	private JMenu createHelpMenu() {
		JMenu menu = new JMenu("Help");

		menu.add(createHelpMenuItem("About"));
		return menu;
	}

	/**
	 * Creates an item in Points menu
	 * 
	 * @param text
	 *            text of the item
	 * @return the created item
	 */
	private JRadioButtonMenuItem createPointsMenuItem(String texto) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(texto);
		class ListenerItemMenu implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("4 points")) {
					disableItems();
					panel.setNumPoints(4);
					panel.clearDraw();
				} else if (event.getActionCommand().equals("7 points")) {
					disableItems();
					panel.setNumPoints(7);
					panel.clearDraw();
				}

				else if (event.getActionCommand().equals("10 points")) {
					disableItems();
					panel.setNumPoints(10);
					panel.clearDraw();

				}
			}
		}

		item.addActionListener(new ListenerItemMenu());

		return item;
	}

	/**
	 * Creates an item in Options menu
	 * 
	 * @param text
	 *            text of the item
	 * @return the created item
	 */
	private JCheckBoxMenuItem createOptionsMenuItem(String text) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);

		class ListenerItemMenu implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Convex Hull")) {
					panel.changeConvexHullVisibility();
				} else if (event.getActionCommand().equals("Polyline")) {
					panel.changePolylineVisibility();
				}
			}
		}

		item.addActionListener(new ListenerItemMenu());

		return item;
	}

	/**
	 * Creates an item in Curves menu
	 * 
	 * @param text
	 *            text of the item
	 * @return the created item
	 */
	private JCheckBoxMenuItem createCurvesMenuItem(String text) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);

		class ListenerItemMenu implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Bézier")) {
					panel.changeBezierCurvesVisibility();
				} else if (event.getActionCommand().equals("B-spline")) {
					panel.changeBSplineCurvesVisibility();
				} else if (event.getActionCommand().equals("Catmull-Rom")) {
					panel.changeCatmullRomCurvesVisibility();
				}
			}
		}

		item.addActionListener(new ListenerItemMenu());

		return item;
	}

	/**
	 * Creates an item in Help menu
	 * 
	 * @param text
	 *            text of the item
	 * @return the created item
	 */
	private JMenuItem createHelpMenuItem(String text) {
		JMenuItem item = new JMenuItem(text);

		class ListenerItemMenu implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("About"))
					JOptionPane
							.showMessageDialog(
									null,
									"Curves Editor\nA curves editor which supports B\u00E9zier, B-Spline\nand Catmull-Rom curves\nAlexandre Martins Garcia 34625\nP5 G23");
			}
		}

		item.addActionListener(new ListenerItemMenu());
		return item;
	}

	/**
	 * Activates the items that can be activated
	 */
	public void activateItems() {
		for (JMenuItem item : activableItems) {
			item.setEnabled(true);
		}
	}

	/**
	 * Activates the Print item option
	 */
	public void activatePrintItem() {
		printItem.setEnabled(true);
	}

	/**
	 * Disables the Print item option
	 */
	public void disablePrintItem() {
		printItem.setEnabled(false);
	}

	/**
	 * Disables the items that can be activated
	 */
	public void disableItems() {
		for (JMenuItem item : activableItems) {
			item.setSelected(item.getText().equals("Polyline"));
			item.setEnabled(false);
		}
	}

	/**
	 * Disables the items that can be activated without changing it's state
	 */
	private void disableItemsSaveState() {
		for (JMenuItem item : activableItems) {
			item.setEnabled(false);
		}
	}
}
