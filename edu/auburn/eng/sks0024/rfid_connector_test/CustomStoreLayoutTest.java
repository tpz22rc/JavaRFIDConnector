package edu.auburn.eng.sks0024.rfid_connector_test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.auburn.eng.rfid_4710.manager_gui.Antenna;
import edu.auburn.eng.rfid_4710.manager_gui.JSONConfigurationFile;
import edu.auburn.eng.rfid_4710.manager_gui.LoadCancelledException;
import edu.auburn.eng.rfid_4710.manager_gui.ServerInfo;
import edu.auburn.eng.sks0024.rfid_connector.JavaRFIDConnector;
import edu.auburn.eng.sks0024.rfid_connector.StoreConfigurationKey;
import edu.auburn.eng.sks0024.rfid_connector.TagLocation;

public class CustomStoreLayoutTest {
	static void main(String[] args) {
		ConfigManagerForTest.main(null);
	}
	
	private static class ConfigManagerForTest {
		private static final String[] COMMON_LOCATIONS = new String[] {"Warehouse", "Loading Area", "Store Floor", "Back Room", "Exit"};
		protected Shell shlRfidConfigurationManager;
		private Text hostnameText;
		private StyledText styledText;
		private Button ant1IsEnabled, ant2IsEnabled, ant3IsEnabled, ant4IsEnabled;
		private Button ant1IsEntryPoint, ant2IsEntryPoint, ant3IsEntryPoint, ant4IsEntryPoint;
		private Combo ant1StoreAreaOne, ant2StoreAreaOne, ant3StoreAreaOne, ant4StoreAreaOne;
		private Combo ant1StoreAreaTwo, ant2StoreAreaTwo, ant3StoreAreaTwo, ant4StoreAreaTwo;
		private Button btnKillSelf, btnRunAway;
		private Button btnSaveStuff, btnLoadStuff;
		private Combo ant1InsertionLocation;
		private Text dbOwner;
		private Text dbPassword;
		private Text dbURL;
		private Combo ant2InsertionLocation;
		private Combo ant3InsertionLocation;
		private Combo ant4InsertionLocation;
		private static ArrayList<String> storeLocations;
		private Button btnEnterStoreLayout;
		private Label lblAddItemsTo_4;
		private Label lblAddItemsTo_3;
		private Label lblAddItemsTo_2;
		private Label lblAddItemsTo_1;
		
		
		public static void createStoreLocations(ArrayList<String> inputLocations) {
			storeLocations = inputLocations;
		}
		
		public static ArrayList<String> readStoreLocations() {
			return storeLocations == null? new ArrayList<String>() : storeLocations;
		}
		
		/**
		 * Runs the SWT RFID Configuration Manager
		 * @param args Command line arguments (not used)
		 */
		public static void main(String[] args) {
			ConfigManagerForTest window = new ConfigManagerForTest();
			window.open();
			
		}

		/**
		 * Redirects console output from standard out to a console in the GUI. Also creates all the GUI elements which
		 * constitute the configuration manager. Afterwards, while the GUI isn't closed it will continually refresh the page.
		 */
		public void open() {
			Display display = Display.getDefault();
			redirectSystemStreams();
			storeLocations = new ArrayList<String>(0);
			createContents();
			shlRfidConfigurationManager.open();
			shlRfidConfigurationManager.layout();
			while (!shlRfidConfigurationManager.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

		/**
		 * Creates all the GUI elements of the configuration manager
		 */
		protected void createContents() {
			setupConfigManagerShell();
			createHostnameUI();
			createAntennaOneUI();
			createQuitButton();
			createExecuteButton();
			createSaveButton();
			createLoadButton();
			createEnterStoreLayoutButton();
			createAntennaTwoUI();
			createAntennaThreeUI();
			createAntennaFourUI();	
			createConsoleUI();
			createDatabaseUI();
		}
		
		/**
		 * Creates the GUI elements related to the hostname text field.
		 */
		private void createHostnameUI() {
			Label lblHostname = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblHostname.setBounds(37, 23, 137, 15);
			lblHostname.setText("Reader Hostname/IP");
			
			hostnameText = new Text(shlRfidConfigurationManager, SWT.BORDER);
			hostnameText.setToolTipText("");
			hostnameText.setBounds(37, 52, 137, 21);
			hostnameText.setMessage("Ex: 192.168.225.50");
		}
		
		/**
		 * Creates the GUI elements related to the console view. This view displays everything which is sent to 
		 * standard out by the JavaRFIDConnector
		 */
		private void createConsoleUI() {
			styledText = new StyledText(shlRfidConfigurationManager, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
			styledText.setBounds(31, 476, 671, 188);
			styledText.addListener(SWT.Modify, new Listener(){
			    public void handleEvent(Event e){
			        styledText.setTopIndex(styledText.getLineCount() - 1);
			    }
			});
			
			Label lblConsole = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblConsole.setBounds(36, 455, 104, 15);
			lblConsole.setText("Console Output");
			
			Label lblDatabaseInformation = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblDatabaseInformation.setBounds(235, 23, 137, 15);
			lblDatabaseInformation.setText("Database Information");	
		}

		private void createDatabaseUI() {
			dbOwner = new Text(shlRfidConfigurationManager, SWT.BORDER);
			dbOwner.setMessage("owner/username");
			dbOwner.setBounds(234, 52, 111, 21);
			
			dbPassword = new Text(shlRfidConfigurationManager, SWT.BORDER | SWT.PASSWORD);
			dbPassword.setMessage("password");
			dbPassword.setBounds(374, 52, 104, 21);
			
			dbURL = new Text(shlRfidConfigurationManager, SWT.BORDER);
			dbURL.setMessage("URL");
			dbURL.setBounds(505, 52, 161, 21);
		}

		private void createEnterStoreLayoutButton() {
			btnEnterStoreLayout = new Button(shlRfidConfigurationManager, SWT.NONE);
			btnEnterStoreLayout.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
				}
			});
			btnEnterStoreLayout.setBounds(182, 692, 120, 25);
			btnEnterStoreLayout.setText("Enter Store Layout");
			btnEnterStoreLayout.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent e) {
					StoreLayoutWindow subwindow = new StoreLayoutWindow();
					subwindow.getCustomLayout();
					populateAndResetDDLs();
				}
				public void mouseUp(MouseEvent e) { }
				public void mouseDoubleClick(MouseEvent e) { }
			});
		}
		
		private void populateAndResetDDLs() {
			if (storeLocations == null) { return; }
			
			ant1StoreAreaOne.setItems(storeLocations.toArray(new String[]{}));
			ant1StoreAreaTwo.setItems(storeLocations.toArray(new String[]{}));
			ant2StoreAreaOne.setItems(storeLocations.toArray(new String[]{}));
			ant2StoreAreaTwo.setItems(storeLocations.toArray(new String[]{}));
			ant3StoreAreaOne.setItems(storeLocations.toArray(new String[]{}));
			ant3StoreAreaTwo.setItems(storeLocations.toArray(new String[]{}));
			ant4StoreAreaOne.setItems(storeLocations.toArray(new String[]{}));
			ant4StoreAreaTwo.setItems(storeLocations.toArray(new String[]{}));
		}
		
		/**
		 * Creates all the GUI elements related to setting up RFID antenna number 4.
		 */
		private void createAntennaFourUI() {
			ant4IsEnabled = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant4IsEnabled.setText("Enabled");
			ant4IsEnabled.setBounds(37, 367, 65, 16);
			ant4IsEnabled.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					toggleAntenna4Fields();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) { }
			});
			
			Label lblAntenna4 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAntenna4.setText("Antenna 4");
			lblAntenna4.setBounds(119, 368, 55, 15);
			
			ant4IsEntryPoint = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant4IsEntryPoint.setEnabled(ant4IsEnabled.getSelection());
			ant4IsEntryPoint.setText("Entry point?");
			ant4IsEntryPoint.setBounds(314, 412, 93, 16);
			ant4IsEntryPoint.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					toggleAntenna4InsertionField();			
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
			
			Label lblBetwixt_4 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblBetwixt_4.setText("Between");
			lblBetwixt_4.setBounds(206, 368, 55, 15);
			
			ant4StoreAreaOne = new Combo(shlRfidConfigurationManager,  SWT.READ_ONLY);
			ant4StoreAreaOne.setEnabled(ant4IsEnabled.getSelection());
			ant4StoreAreaOne.setBounds(301, 365, 91, 23);
			ant4StoreAreaOne.setItems(COMMON_LOCATIONS);
			ant4StoreAreaOne.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant4InsertionLocation.setItems(new String[] { ant4StoreAreaOne.getText(), ant4StoreAreaTwo.getText() });
				}
			});
			
			Label lblAnd4 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAnd4.setAlignment(SWT.CENTER);
			lblAnd4.setText("And");
			lblAnd4.setBounds(436, 368, 55, 15);
			
			ant4StoreAreaTwo = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant4StoreAreaTwo.setEnabled(ant4IsEnabled.getSelection());
			ant4StoreAreaTwo.setBounds(547, 365, 91, 23);
			ant4StoreAreaTwo.setItems(COMMON_LOCATIONS);
			ant4StoreAreaTwo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant4InsertionLocation.setItems(new String[] { ant4StoreAreaOne.getText(), ant4StoreAreaTwo.getText() });
				}
			});
			
			lblAddItemsTo_4 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAddItemsTo_4.setBounds(436, 413, 85, 15);
			lblAddItemsTo_4.setText("Add Items To");
			
			ant4InsertionLocation = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant4InsertionLocation.setEnabled(ant4IsEntryPoint.getSelection());
			ant4InsertionLocation.setBounds(562, 410, 76, 21);
			ant4InsertionLocation.setItems(COMMON_LOCATIONS);
		}
		
		/**
		 * Creates all the GUI elements related to setting up RFID antenna number 3.
		 */
		private void createAntennaThreeUI() {
			ant3IsEnabled = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant3IsEnabled.setText("Enabled");
			ant3IsEnabled.setBounds(37, 280, 65, 16);
			ant3IsEnabled.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					toggleAntenna3Fields();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) { }
			});
			
			Label lblAntenna3 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAntenna3.setText("Antenna 3");
			lblAntenna3.setBounds(119, 281, 55, 15);
			
			ant3IsEntryPoint = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant3IsEntryPoint.setEnabled(ant3IsEnabled.getSelection());
			ant3IsEntryPoint.setText("Entry point?");
			ant3IsEntryPoint.setBounds(314, 322, 93, 16);
			ant3IsEntryPoint.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					toggleAntenna3InsertionField();	
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
			
			Label lblBetwixt_3 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblBetwixt_3.setText("Between");
			lblBetwixt_3.setBounds(206, 281, 55, 15);
			
			ant3StoreAreaOne = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant3StoreAreaOne.setEnabled(ant3IsEnabled.getSelection());
			ant3StoreAreaOne.setBounds(301, 278, 91, 23);
			ant3StoreAreaOne.setItems(COMMON_LOCATIONS);
			ant3StoreAreaOne.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant3InsertionLocation.setItems(new String[] { ant3StoreAreaOne.getText(), ant3StoreAreaTwo.getText() });
				}
			});
			
			Label lblAnd3 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAnd3.setAlignment(SWT.CENTER);
			lblAnd3.setText("And");
			lblAnd3.setBounds(436, 281, 55, 15);
			
			ant3StoreAreaTwo = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant3StoreAreaTwo.setEnabled(ant3IsEnabled.getSelection());
			ant3StoreAreaTwo.setBounds(547, 278, 91, 23);
			ant3StoreAreaTwo.setItems(COMMON_LOCATIONS);
			ant3StoreAreaTwo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant3InsertionLocation.setItems(new String[] { ant3StoreAreaOne.getText(), ant3StoreAreaTwo.getText() });
				}
			});
			
			lblAddItemsTo_3 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAddItemsTo_3.setText("Add Items To");
			lblAddItemsTo_3.setBounds(436, 323, 85, 15);
			
			ant3InsertionLocation = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant3InsertionLocation.setEnabled(ant3IsEntryPoint.getSelection());
			ant3InsertionLocation.setBounds(562, 320, 76, 21);
			ant3InsertionLocation.setItems(COMMON_LOCATIONS);
		}
		
		/**
		 * Creates all the GUI elements related to setting up RFID antenna number 2.
		 */
		private void createAntennaTwoUI() {
			ant2IsEnabled = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant2IsEnabled.setText("Enabled");
			ant2IsEnabled.setBounds(37, 192, 65, 16);
			ant2IsEnabled.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					toggleAntenna2Fields();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) { }
			});
			
			Label lblAntenna2 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAntenna2.setText("Antenna 2");
			lblAntenna2.setBounds(119, 193, 55, 15);
			
			ant2IsEntryPoint = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant2IsEntryPoint.setEnabled(ant2IsEnabled.getSelection());
			ant2IsEntryPoint.setText("Entry point?");
			ant2IsEntryPoint.setBounds(314, 236, 93, 16);
			ant2IsEntryPoint.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					toggleAntenna2InsertionField();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
			
			Label lblBetwixt_2 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblBetwixt_2.setText("Between");
			lblBetwixt_2.setBounds(206, 193, 55, 15);
			
			ant2StoreAreaOne = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant2StoreAreaOne.setEnabled(ant2IsEnabled.getSelection());
			ant2StoreAreaOne.setBounds(301, 190, 91, 23);
			ant2StoreAreaOne.setItems(COMMON_LOCATIONS);
			ant2StoreAreaOne.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant2InsertionLocation.setItems(new String[] { ant2StoreAreaOne.getText(), ant2StoreAreaTwo.getText() });
				}
			});
			
			Label lblAnd2 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAnd2.setAlignment(SWT.CENTER);
			lblAnd2.setText("And");
			lblAnd2.setBounds(436, 193, 55, 15);
			
			ant2StoreAreaTwo = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant2StoreAreaTwo.setEnabled(ant2IsEnabled.getSelection());
			ant2StoreAreaTwo.setBounds(547, 190, 91, 23);
			ant2StoreAreaTwo.setItems(COMMON_LOCATIONS);
			ant2StoreAreaTwo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant2InsertionLocation.setItems(new String[] { ant2StoreAreaOne.getText(), ant2StoreAreaTwo.getText() });
				}
			});
			
			lblAddItemsTo_2 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAddItemsTo_2.setText("Add Items To");
			lblAddItemsTo_2.setBounds(436, 237, 85, 15);
			
			ant2InsertionLocation = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant2InsertionLocation.setEnabled(ant2IsEntryPoint.getSelection());
			ant2InsertionLocation.setBounds(562, 234, 76, 21);
		}
		
		/**
		 * Creates all the GUI elements related to setting up RFID antenna number 1.
		 */
		private void createAntennaOneUI() {
			ant1IsEnabled = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant1IsEnabled.setBounds(37, 110, 65, 16);
			ant1IsEnabled.setText("Enabled");
			ant1IsEnabled.addSelectionListener(new SelectionListener() {	
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					toggleAntenna1Fields();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
			
			Label lblAntenna1 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAntenna1.setBounds(119, 111, 55, 15);
			lblAntenna1.setText("Antenna 1");
			
			ant1IsEntryPoint = new Button(shlRfidConfigurationManager, SWT.CHECK);
			ant1IsEntryPoint.setEnabled(ant1IsEnabled.getSelection());
			ant1IsEntryPoint.setBounds(314, 150, 93, 16);
			ant1IsEntryPoint.setText("Entry point?");
			ant1IsEntryPoint.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					toggleAntenna1InsertionField();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
			
			Label lblBetwixt_1 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblBetwixt_1.setBounds(206, 111, 55, 15);
			lblBetwixt_1.setText("Between");
			
			ant1StoreAreaOne = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant1StoreAreaOne.setEnabled(ant1IsEnabled.getSelection());
			ant1StoreAreaOne.setBounds(301, 108, 91, 23);
			ant1StoreAreaOne.setItems(storeLocations.toArray(new String[]{}));
			ant1StoreAreaOne.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant1InsertionLocation.setItems(new String[] { ant1StoreAreaOne.getText(), ant1StoreAreaTwo.getText() });
				}
			});
			
			Label lblAnd1 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAnd1.setAlignment(SWT.CENTER);
			lblAnd1.setBounds(436, 111, 55, 15);
			lblAnd1.setText("And");
			
			ant1StoreAreaTwo = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant1StoreAreaTwo.setEnabled(ant1IsEnabled.getSelection());
			ant1StoreAreaTwo.setBounds(547, 108, 91, 23);
			ant1StoreAreaTwo.setItems(COMMON_LOCATIONS);
			ant1StoreAreaTwo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					ant1InsertionLocation.setItems(new String[] { ant1StoreAreaOne.getText(), ant1StoreAreaTwo.getText() });
				}
			});
			
			lblAddItemsTo_1 = new Label(shlRfidConfigurationManager, SWT.NONE);
			lblAddItemsTo_1.setText("Add Items To");
			lblAddItemsTo_1.setBounds(436, 151, 85, 15);
				
			ant1InsertionLocation = new Combo(shlRfidConfigurationManager, SWT.READ_ONLY);
			ant1InsertionLocation.setEnabled(ant1IsEntryPoint.getSelection());
			ant1InsertionLocation.setBounds(563, 148, 75, 21);
		}

		/**
		 * Creates the load button and sets its  mouseDown controller to load an RFID configuration file
		 * to populate the GUIs and setup the antenna information.
		 */
		private void createLoadButton() {
			btnLoadStuff = new Button(shlRfidConfigurationManager, SWT.NONE);
			btnLoadStuff.setToolTipText("Load existing configuration");
			btnLoadStuff.setBounds(37, 736, 120, 25);
			btnLoadStuff.setText("Load Configuration");
			btnLoadStuff.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent e) {
					populateManagerFromConfigFile();
				}
				public void mouseUp(MouseEvent e) { }
				public void mouseDoubleClick(MouseEvent e) { }
			});
		}
		
		/**
		 * Creates the save button and sets its mouseDown controller to save the antenna
		 * GUI fields as a configuration file
		 */
		private void createSaveButton() {
			btnSaveStuff = new Button(shlRfidConfigurationManager, SWT.NONE);
			btnSaveStuff.setToolTipText("Save the entered configuration for later use");
			btnSaveStuff.setBounds(37, 692, 120, 25);
			btnSaveStuff.setText("Save Configuration");
			btnSaveStuff.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent e) {
					ArrayList<Antenna> antennaList = (ArrayList<Antenna>) getAntennaListFromFields();				
					JSONConfigurationFile js = new JSONConfigurationFile();
					ServerInfo si = getServerInfoFromFields();
					js.saveConfiguration(hostnameText.getText(), antennaList, si, storeLocations); 
				}
				public void mouseUp(MouseEvent e) { }
				public void mouseDoubleClick(MouseEvent e) { }
			});
		}
		
		/**
		 * Creates the execute button and sets its mouseDown controller to begin executing the JavaRFIDConnector
		 */
		private void createExecuteButton() {
			btnRunAway = new Button(shlRfidConfigurationManager, SWT.NONE);
			btnRunAway.setToolTipText("Will run Java Connector with current settings");
			btnRunAway.setBounds(494, 692, 75, 25);
			btnRunAway.setText("Execute");
			btnRunAway.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent arg0) {
					launchJavaRFIDConnector();
				}
				public void mouseDoubleClick(MouseEvent arg0) {	}
				public void mouseUp(MouseEvent arg0) { }
			});
		}
		
		/**
		 * Creates the quit button and sets its mouseDown controller to exit the program
		 */
		private void createQuitButton() {
			btnKillSelf = new Button(shlRfidConfigurationManager, SWT.NONE);
			btnKillSelf.setToolTipText("Closes the application");
			btnKillSelf.setBounds(612, 692, 75, 25);
			btnKillSelf.setText("Quit");
			btnKillSelf.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent e) {
					System.exit(0);
				}
				public void mouseDoubleClick(MouseEvent arg0) {	}
				public void mouseUp(MouseEvent arg0) { }
			});
		}
		
		/**
		 * Creates the SWT shell that the configuration manager runs in and sets a mouseDown controller so that
		 * whenever the user clicks the shell, it forces focus on the shell.
		 */
		private void setupConfigManagerShell() {
			shlRfidConfigurationManager = new Shell();
			shlRfidConfigurationManager.setImage(null);
			shlRfidConfigurationManager.setToolTipText("You are reading the tooltip text");
			shlRfidConfigurationManager.setSize(750, 830);
			shlRfidConfigurationManager.setText("RFID Configuration Manager");
			shlRfidConfigurationManager.setLayout(null);
			shlRfidConfigurationManager.setFocus();
			shlRfidConfigurationManager.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent e) {
					shlRfidConfigurationManager.forceFocus();
				}
				public void mouseUp(MouseEvent e) { }
				public void mouseDoubleClick(MouseEvent e) { }
			});
		}
		
		/**
		 * Redirects the standard output stream so that instead of displaying to the Java console, it
		 * will display the information in the GUI console for the user to see.
		 */
		private void redirectSystemStreams() {
			  OutputStream out = new OutputStream() {
			    @Override
			    public void write(final int b) throws IOException {
			      updateConsole(String.valueOf((char) b));
			    }

				@Override
			    public void write(byte[] b, int off, int len) throws IOException {
			      updateConsole(new String(b, off, len));
			    }
			 
			    @Override
			    public void write(byte[] b) throws IOException {
			      write(b, 0, b.length);
			    }
			  };
			 
			  System.setOut(new PrintStream(out, true));
			  System.setErr(new PrintStream(out, true));
			}
		
		/**
		 * Updates the GUI console with the latest information which is being sent to the standard output stream.
		 * @param text The text to be added to the GUI console.
		 */
		private void updateConsole(final String text) {
			Display.getDefault().syncExec(new Runnable() {
			    public void run() {
			        styledText.append(text);
			    }
			});
		}
		
		/**
		 * Extracts all the information from the Antenna fields in order to generate a list of
		 * Antenna objects.
		 * @return an ArrayList of Antenna objects which are created from the Antenna GUI contents.
		 */
		private ArrayList<Antenna> getAntennaListFromFields() {
			ArrayList<Antenna> antennaList = new ArrayList<Antenna>();
			Antenna antenna1 = new Antenna();
			antenna1.setStoreAreaOne(ant1StoreAreaOne.getText());
			antenna1.setStoreAreaTwo(ant1StoreAreaTwo.getText());
			antenna1.setEnabled(ant1IsEnabled.getSelection());
			antenna1.setEntryPoint(ant1IsEntryPoint.getSelection());
			antenna1.setAntennaID(1);
			antenna1.setInsertionLocation(ant1InsertionLocation.getText());
			antennaList.add(antenna1);
			
			Antenna antenna2 = new Antenna();
			antenna2.setStoreAreaOne(ant2StoreAreaOne.getText());
			antenna2.setStoreAreaTwo(ant2StoreAreaTwo.getText());
			antenna2.setEnabled(ant2IsEnabled.getSelection());
			antenna2.setEntryPoint(ant2IsEntryPoint.getSelection());
			antenna2.setAntennaID(2);
			antennaList.add(antenna2);
			
			Antenna antenna3 = new Antenna();
			antenna3.setStoreAreaOne(ant3StoreAreaOne.getText());
			antenna3.setStoreAreaTwo(ant3StoreAreaTwo.getText());
			antenna3.setEnabled(ant3IsEnabled.getSelection());
			antenna3.setEntryPoint(ant3IsEntryPoint.getSelection());
			antenna3.setAntennaID(3);
			antennaList.add(antenna3);
			
			Antenna antenna4 = new Antenna();
			antenna4.setStoreAreaOne(ant4StoreAreaOne.getText());
			antenna4.setStoreAreaTwo(ant4StoreAreaTwo.getText());
			antenna4.setEnabled(ant4IsEnabled.getSelection());
			antenna4.setEntryPoint(ant4IsEntryPoint.getSelection());
			antenna4.setAntennaID(4);
			antennaList.add(antenna4);	
			return antennaList;
		}
		
		/**
		 * Loads an Antenna object's fields to its related GUI fields in the configuration manager
		 * @param antennaOne an Antenna which relates to the Antenna 1 GUI fields
		 */
		private void loadAntennaOneProperties(Antenna antennaOne) {
			ant1StoreAreaOne.setText(antennaOne.getStoreAreaOne());
			ant1StoreAreaTwo.setText(antennaOne.getStoreAreaTwo());
			ant1IsEnabled.setSelection(antennaOne.isEnabled());
			ant1IsEntryPoint.setSelection(antennaOne.isEntryPoint());
			ant1StoreAreaOne.setEnabled(ant1IsEnabled.getSelection());
			ant1StoreAreaTwo.setEnabled(ant1IsEnabled.getSelection());
			ant1IsEntryPoint.setEnabled(ant1IsEnabled.getSelection());
			ant1InsertionLocation.setEnabled(ant1IsEnabled.getSelection() && ant1IsEntryPoint.getSelection());
			ant1InsertionLocation.setText(antennaOne.getInsertionLocation());
		}
		
		/**
		 * Loads an Antenna object's fields to its related GUI fields in the configuration manager
		 * @param antennaOne an Antenna which relates to the Antenna 2 GUI fields
		 */
		private void loadAntennaTwoProperties(Antenna antennaTwo) {
			ant2StoreAreaOne.setText(antennaTwo.getStoreAreaOne());
			ant2StoreAreaTwo.setText(antennaTwo.getStoreAreaTwo());
			ant2IsEnabled.setSelection(antennaTwo.isEnabled());
			ant2IsEntryPoint.setSelection(antennaTwo.isEntryPoint());
			ant2StoreAreaOne.setEnabled(ant2IsEnabled.getSelection());
			ant2StoreAreaTwo.setEnabled(ant2IsEnabled.getSelection());
			ant2IsEntryPoint.setEnabled(ant2IsEnabled.getSelection());
			ant2InsertionLocation.setEnabled(ant2IsEnabled.getSelection() && ant2IsEntryPoint.getSelection());
			ant2InsertionLocation.setText(antennaTwo.getInsertionLocation());
		}
		
		/**
		 * Loads an Antenna object's fields to its related GUI fields in the configuration manager
		 * @param antennaOne an Antenna which relates to the Antenna 3 GUI fields
		 */
		private void loadAntennaThreeProperties(Antenna antennaThree) {
			ant3StoreAreaOne.setText(antennaThree.getStoreAreaOne());
			ant3StoreAreaTwo.setText(antennaThree.getStoreAreaTwo());
			ant3IsEnabled.setSelection(antennaThree.isEnabled());
			ant3IsEntryPoint.setSelection(antennaThree.isEntryPoint());
			ant3StoreAreaOne.setEnabled(ant3IsEnabled.getSelection());
			ant3StoreAreaTwo.setEnabled(ant3IsEnabled.getSelection());
			ant3IsEntryPoint.setEnabled(ant3IsEnabled.getSelection());
			ant3InsertionLocation.setEnabled(ant3IsEnabled.getSelection() && ant3IsEntryPoint.getSelection());
			ant3InsertionLocation.setText(antennaThree.getInsertionLocation());
		}
		
		/**
		 * Loads an Antenna object's fields to its related GUI fields in the configuration manager
		 * @param antennaOne an Antenna which relates to the Antenna 4 GUI fields
		 */
		private void loadAntennaFourProperties(Antenna antennaFour) {
			ant4StoreAreaOne.setText(antennaFour.getStoreAreaOne());
			ant4StoreAreaTwo.setText(antennaFour.getStoreAreaTwo());
			ant4IsEnabled.setSelection(antennaFour.isEnabled());
			ant4IsEntryPoint.setSelection(antennaFour.isEntryPoint());
			ant4StoreAreaOne.setEnabled(ant4IsEnabled.getSelection());
			ant4StoreAreaTwo.setEnabled(ant4IsEnabled.getSelection());
			ant4IsEntryPoint.setEnabled(ant4IsEnabled.getSelection());
			ant4InsertionLocation.setEnabled(ant4IsEnabled.getSelection() && ant4IsEntryPoint.getSelection());
			ant4InsertionLocation.setText(antennaFour.getInsertionLocation());
		}
		
		/**
		 * Loads a configuration file and uses it to populate the Antenna and Server GUI fields
		 */
		private void populateManagerFromConfigFile() {
			JSONConfigurationFile js = new JSONConfigurationFile();
			try {js.loadConfiguration();} catch (LoadCancelledException ex) { System.out.println(ex); return; }
			hostnameText.setText(js.getHostname());
			ServerInfo serverInfo = js.getServerInfo();
			storeLocations = (ArrayList<String>) js.getStoreLocations();
			populateAndResetDDLs();
			ArrayList<Antenna> antennaList = js.getAntennaList();
			loadAntennaProperties(antennaList);
			loadServerInfoProperties(serverInfo);
		}
		
		private void loadServerInfoProperties(ServerInfo serverInfo) {
			String owner = serverInfo.getOwner();
			String password = serverInfo.getPassword();
			String url = serverInfo.getUrl();
			dbOwner.setText(owner);
			dbPassword.setText(password);
			dbURL.setText(url);
			
		}
		
		/**
		 * Takes a list of Antenna objects and loads each of them into their related GUI fields.
		 * @param antennaList an ArrayList of Antenna objects which are to be loaded to the Antenna fields
		 */
		private void loadAntennaProperties(ArrayList<Antenna> antennaList) {
			Antenna antennaOne = antennaList.get(0);
			loadAntennaOneProperties(antennaOne);
			
			Antenna antennaTwo = antennaList.get(1);
			loadAntennaTwoProperties(antennaTwo);
			
			Antenna antennaThree = antennaList.get(2);
			loadAntennaThreeProperties(antennaThree);
			
			Antenna antennaFour = antennaList.get(3);
			loadAntennaFourProperties(antennaFour);
		}
		
		/**
		 * Pulls information from the hostname, antenna, and server information GUI fields 
		 * in order to set up the JavaRFIDConnector and launch it.
		 * @throws Exception 
		 */
		private void launchJavaRFIDConnector() {
			JavaRFIDConnector connector = new JavaRFIDConnector();
			List<Antenna> antennaList = getAntennaListFromFields();			
			String hostname = hostnameText.getText();
			ServerInfo serverInfo = getServerInfoFromFields();
			try {
				connector.testBootstrap(hostname, serverInfo, antennaList);
			} catch (Exception e) {
				System.out.println("Please fill out all the fields (including server info, hostname, and at least one antenna)");
			}
			System.out.println("**************Store Locations**************");
			for (String location : storeLocations) {
				System.out.println(location);
			}
			System.out.println("**************Defined Reader Locations and Transitions**************");
			for (Entry<StoreConfigurationKey, TagLocation> mapEntry : JavaRFIDConnector.getStoreConfigurationMap().entrySet()) {
				System.out.println(mapEntry.getKey() + " ---> New Location: " + mapEntry.getValue());
			}
			
		}
		
		/**
		 * Pulls the fields from the server information GUI fields in order to create a ServerInfo object
		 * which is then returned.
		 * @return a ServerInfo object containing all the server information data stored in the configuration
		 * manager.
		 */
		private ServerInfo getServerInfoFromFields() {
			ServerInfo si = new ServerInfo();
			String owner = dbOwner.getText();
			String password = dbPassword.getText();
			String url = dbURL.getText();
			si.setOwner(owner);
			si.setPassword(password);
			//"http://aurfid.herokuapp.com/"
			si.setUrl(url);
			return si;
		}
		
		/**
		 * Toggles the GUI elements related to Antenna 4 from enabled to disabled an disabled to enabled.
		 */
		private void toggleAntenna4Fields() {
			//^ is XOR
			ant4StoreAreaOne.setEnabled(ant4StoreAreaOne.getEnabled() ^ true);
			ant4StoreAreaTwo.setEnabled(ant4StoreAreaTwo.getEnabled() ^ true);
			ant4IsEntryPoint.setEnabled(ant4IsEntryPoint.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the GUI elements related to Antenna 3 from enabled to disabled an disabled to enabled.
		 */
		private void toggleAntenna3Fields() {
			//^ is XOR
			ant3StoreAreaOne.setEnabled(ant3StoreAreaOne.getEnabled() ^ true);
			ant3StoreAreaTwo.setEnabled(ant3StoreAreaTwo.getEnabled() ^ true);
			ant3IsEntryPoint.setEnabled(ant3IsEntryPoint.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the GUI elements related to Antenna 2 from enabled to disabled an disabled to enabled.
		 */
		private void toggleAntenna2Fields() {
			//^ is XOR
			ant2StoreAreaOne.setEnabled(ant2StoreAreaOne.getEnabled() ^ true);
			ant2StoreAreaTwo.setEnabled(ant2StoreAreaTwo.getEnabled() ^ true);
			ant2IsEntryPoint.setEnabled(ant2IsEntryPoint.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the GUI elements related to Antenna 1 from enabled to disabled an disabled to enabled.
		 */
		private void toggleAntenna1Fields() {
			//^ is XOR
			ant1StoreAreaOne.setEnabled(ant1StoreAreaOne.getEnabled() ^ true);
			ant1StoreAreaTwo.setEnabled(ant1StoreAreaTwo.getEnabled() ^ true);
			ant1IsEntryPoint.setEnabled(ant1IsEntryPoint.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the insertion location for antenna 1 from on to off and off to on whenever the
		 * isEntryPoint checkbox for antenna 1 is unchecked or checked respectively.
		 */
		private void toggleAntenna1InsertionField() {
			ant1InsertionLocation.setEnabled(ant1InsertionLocation.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the insertion location for antenna 2 from on to off and off to on whenever the
		 * isEntryPoint checkbox for antenna 2 is unchecked or checked respectively.
		 */
		private void toggleAntenna2InsertionField() {
			ant2InsertionLocation.setEnabled(ant2InsertionLocation.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the insertion location for antenna 3 from on to off and off to on whenever the
		 * isEntryPoint checkbox for antenna 3 is unchecked or checked respectively.
		 */
		private void toggleAntenna3InsertionField() {
			ant3InsertionLocation.setEnabled(ant3InsertionLocation.getEnabled() ^ true);
		}
		
		/**
		 * Toggles the insertion location for antenna 4 from on to off and off to on whenever the
		 * isEntryPoint checkbox for antenna 4 is unchecked or checked respectively.
		 */
		private void toggleAntenna4InsertionField() {
			ant4InsertionLocation.setEnabled(ant4InsertionLocation.getEnabled() ^ true);
		}
		
		private class StoreLayoutWindow {

			protected Shell shlCreateStoreLayout;
			private Text loc1Text;
			private Text loc2Text;
			private Text loc3Text;
			private Text loc4Text;
			private Text loc5Text;
			private Text loc6Text;
			private Text loc7Text;
			private Text loc8Text;
			private ArrayList<String> storeLayout;

			/**
			 * Open the window.
			 */
			public void open() {
				Display display = Display.getDefault();
				createContents();
				shlCreateStoreLayout.open();
				shlCreateStoreLayout.layout();
				while (!shlCreateStoreLayout.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			}
			
			public ArrayList<String> getCustomLayout() {
				storeLayout = ConfigManagerForTest.readStoreLocations();
				try {
					StoreLayoutWindow window = new StoreLayoutWindow();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return storeLayout;
			}

			/**
			 * Create contents of the window.
			 */
			protected void createContents() {
				shlCreateStoreLayout = new Shell();
				shlCreateStoreLayout.setSize(558, 322);
				shlCreateStoreLayout.setText("Create Store Layout");
				createLocationUI();
				createLayoutLabel();
				createSaveButton();
				createLoadButton();
			}

			private void createLayoutLabel() {
				Label layoutLabel = new Label(shlCreateStoreLayout, SWT.WRAP);
				layoutLabel.setBounds(357, 84, 139, 109);
				layoutLabel.setText("Please enter names for the locations within your store. You may enter a maximum of 8 locations to be supported by this scanner.");
			}

			private void createLoadButton() {
				Button btnCancel = new Button(shlCreateStoreLayout, SWT.NONE);
				btnCancel.setBounds(442, 216, 75, 25);
				btnCancel.setText("Cancel");
				btnCancel.addMouseListener(new MouseListener() {
					public void mouseDown(MouseEvent e) {
						shlCreateStoreLayout.dispose();
					}
					public void mouseUp(MouseEvent e) { }
					public void mouseDoubleClick(MouseEvent e) { }
				});
			}

			private void createSaveButton() {
				Button btnSaveLayout = new Button(shlCreateStoreLayout, SWT.NONE);
				btnSaveLayout.setBounds(357, 216, 75, 25);
				btnSaveLayout.setText("Save Layout");
				btnSaveLayout.addMouseListener(new MouseListener() {
					public void mouseDown(MouseEvent e) {
						sendStoreLayoutToConfigManager();
						shlCreateStoreLayout.dispose();
					}
					public void mouseUp(MouseEvent e) { }
					public void mouseDoubleClick(MouseEvent e) { }
				});
			}

			private void createLocationUI() {
				createLocation1UI();
				createLocation2UI();
				createLocation3UI();
				createLocation4UI();
				createLocation5UI();
				createLocation6UI();
				createLocation7UI();
				createLocation8UI();
			}

			private void createLocation8UI() {
				loc8Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc8Text.setBounds(139, 216, 156, 21);
				
				Label lblLocation_8 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_8.setText("Location #8:");
				lblLocation_8.setBounds(35, 219, 85, 15);
			}

			private void createLocation7UI() {
				loc7Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc7Text.setBounds(139, 189, 156, 21);
				
				Label lblLocation_7 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_7.setText("Location #7:");
				lblLocation_7.setBounds(35, 192, 85, 15);
			}

			private void createLocation6UI() {
				loc6Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc6Text.setBounds(139, 162, 156, 21);
				
				Label lblLocation_6 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_6.setText("Location #6:");
				lblLocation_6.setBounds(35, 165, 85, 15);
			}

			private void createLocation5UI() {
				loc5Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc5Text.setBounds(139, 135, 156, 21);
				
				Label lblLocation_5 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_5.setText("Location #5:");
				lblLocation_5.setBounds(35, 138, 85, 15);
			}

			private void createLocation4UI() {
				loc4Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc4Text.setBounds(139, 108, 156, 21);
				
				Label lblLocation_4 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_4.setText("Location #4:");
				lblLocation_4.setBounds(35, 111, 85, 15);
			}

			private void createLocation3UI() {
				loc3Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc3Text.setBounds(139, 81, 156, 21);
				
				Label lblLocation_3 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_3.setText("Location #3:");
				lblLocation_3.setBounds(35, 84, 85, 15);
			}

			private void createLocation2UI() {
				loc2Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc2Text.setBounds(139, 54, 156, 21);
				
				Label lblLocation_2 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_2.setBounds(35, 57, 85, 15);
				lblLocation_2.setText("Location #2:");
			}

			private void createLocation1UI() {
				loc1Text = new Text(shlCreateStoreLayout, SWT.BORDER);
				loc1Text.setBounds(139, 27, 156, 21);
				
				Label lblLocation_1 = new Label(shlCreateStoreLayout, SWT.NONE);
				lblLocation_1.setText("Location #1:");
				lblLocation_1.setBounds(35, 30, 85, 15);
			}

			private ArrayList<String> getStoreLocations() {
				Set<String> locations = new HashSet<String>();
				HashMap<Integer, String> locationTextFieldStrings = getLocationsFromFields();
				for (int i = 0; i < locationTextFieldStrings.size(); i++) {
					String location = locationTextFieldStrings.get(new Integer(i + 1));
					if (notNullOrEmpty(location)) {
						locations.add(location);
					}
				}
				return new ArrayList<String>(locations);
			}

			private boolean notNullOrEmpty(String location) {
				return location != null && !location.equals("");
			}

			private void sendStoreLayoutToConfigManager() {
				storeLayout = getStoreLocations();
				ConfigManagerForTest.createStoreLocations(storeLayout);
			}
			
			private HashMap<Integer, String> getLocationsFromFields() {
				HashMap<Integer, String> locations = new HashMap<Integer, String>();
				locations.put(1, loc1Text.getText().trim());
				locations.put(2, loc2Text.getText().trim());
				locations.put(3, loc3Text.getText().trim());
				locations.put(4, loc4Text.getText().trim());
				locations.put(5, loc5Text.getText().trim());
				locations.put(6, loc6Text.getText().trim());
				locations.put(7, loc7Text.getText().trim());
				locations.put(8, loc8Text.getText().trim());
				return locations;
			}
		}
	}
}
