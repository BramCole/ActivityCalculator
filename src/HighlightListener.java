import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

// This class is used to highlight fields when they have been left empty but need to be filled by the user
// Instantiate either a JTextComponent or JComboBox as a new HighlightListener to use it

public class HighlightListener implements DocumentListener{
	JTextComponent comp = null;
	JComboBox<String> comboComp = null;
	JSpinner spinnerComp = null;

	Color darkOrange = new Color(255,140,0);
	Color blue = new Color(0,0,255);
	Color grey = new Color(170,170,170);
	Border highlightBorder = BorderFactory.createLineBorder(darkOrange);
	Border blueBorder = BorderFactory.createLineBorder(blue);
	Border defaultBorder = BorderFactory.createLineBorder(grey);
	Border emptyBorder = BorderFactory.createEmptyBorder(0,2,0,0);
	Border compoundBorder = BorderFactory.createCompoundBorder(defaultBorder, emptyBorder);
	Border highlightCompoundBorder = BorderFactory.createCompoundBorder(highlightBorder, emptyBorder);
	boolean comboOn = false;
	boolean textOn = false;
	boolean spinnerOn = false;

	// Constructor for JTextComponent
	public HighlightListener(JTextComponent jtc) { 
		textOn = true;	// Set to true as this is a text field
		comp = jtc; 
		comp.getDocument().addDocumentListener(this); 
		unhighlightIfFilled(); 
	}  

	// Constructor for JComboBox
	public HighlightListener(JComboBox<String> combobox) {
		comboOn = true;
		comboComp = combobox;

		// Add action listener to trigger unhighlightIfFilled if user fills field
		comboComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unhighlightIfFilled();	//If action is performed, unhighlight the field if it is filled
			}
		});
	}

	// Constructor for JSpinner
	public HighlightListener(JSpinner spinner) {
		spinnerOn = true;
		spinnerComp = spinner;
		// Add listener to trigger unhighlightIfFilled if user fills field
		spinnerComp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				unhighlightIfFilled();
			}
		});
	}

	public void insertUpdate(DocumentEvent e) {
		unhighlightIfFilled();
	}   

	public void removeUpdate(DocumentEvent e) {
		unhighlightIfFilled(); 
	}   

	public void changedUpdate(DocumentEvent e) {
		unhighlightIfFilled();
	}

	// This method is called to highlight specific, wrongly empty fields
	public void highlightBorder() {
		if(textOn) 
			comp.setBorder(highlightCompoundBorder);
		if(comboOn)
			comboComp.setBorder(highlightCompoundBorder);
		if(spinnerOn)
			spinnerComp.setBorder(highlightCompoundBorder);
	}

	// This method is called to highlight specific, wrongly empty fields
	public void defaultBorder() {
		if(textOn)
			comp.setBorder(compoundBorder);
		else if(comboOn)
			comboComp.setBorder(compoundBorder);
		else if(spinnerOn)
			spinnerComp.setBorder(compoundBorder);
	}

	// This method will unhighlight fields (reset border to default) if they are filled
	private void unhighlightIfFilled() {

		if (comboOn) {	//If this component is a comboBox
			// If combo box is filled, unhighlight border
			if(comboComp.getSelectedItem() != null) 
				comboComp.setBorder(defaultBorder);
		}

		if(textOn) {	// If this component is a text field
			// If text box is filled, unhighlight border
			if (comp.getText().trim().length() != 0) {
				comp.setBorder(compoundBorder); 
				comp.setMargin(new Insets(1,1,1,1));
			}
		}

		if(spinnerOn) {
			if(! spinnerComp.getValue().toString().isEmpty()){
				spinnerComp.setBorder(compoundBorder);
			}
		}
	}
}
