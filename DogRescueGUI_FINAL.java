package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DogRescueGUI_FINAL extends JFrame {

    /**
	 * @author Jack Norton
	 * @author Reese Schumacher
	 */
	
	private static final long serialVersionUID = 1L;
	
	private final String dbURL = "jdbc:mysql://localhost:3306/dog_rescue";
//    private final String username = "root";
//    private final String password = "password";
    private static Connection conn;
//    private JTable table;
    static String tableSelected = "";
    static boolean editable = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DogRescueGUI_FINAL gui = new DogRescueGUI_FINAL();
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.setBounds(200, 50, 600, 600);
            gui.setVisible(true);
        });
    }
    
    /*
     * UPDATE dog SET buildingid = 111118101 WHERE buildingid = 1;
     */

    public DogRescueGUI_FINAL() {
        super("Dog Rescue Database");
        
        Box hBoxUsername = Box.createHorizontalBox();
        hBoxUsername.add(Box.createHorizontalGlue());
        hBoxUsername.add(new JLabel("Username: "));
        TextField usernameTF = new TextField(10);
        usernameTF.setColumns(1);
        hBoxUsername.add(usernameTF);
        hBoxUsername.add(Box.createHorizontalGlue());

        Box hBoxPass = Box.createHorizontalBox();
        hBoxPass.add(Box.createHorizontalGlue());
        hBoxPass.add(new JLabel("Password: "));
        TextField passwordTF = new TextField(10);
        passwordTF.setEchoChar('*');
        hBoxPass.add(passwordTF);
        passwordTF.setColumns(1);
        hBoxPass.add(Box.createHorizontalGlue());

        Box vBoxTables = Box.createVerticalBox();
        vBoxTables.add(Box.createVerticalGlue());
        vBoxTables.add(hBoxUsername);
        vBoxTables.add(Box.createVerticalGlue());
        vBoxTables.add(hBoxPass);
        vBoxTables.add(Box.createVerticalGlue());
        
        vBoxTables.add(Box.createVerticalGlue());
        vBoxTables.setAlignmentX(CENTER_ALIGNMENT);
        vBoxTables.setAlignmentY(CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String username = usernameTF.getText();
                String password = passwordTF.getText();
                if (!username.isEmpty() && !password.isEmpty()) {
                    try {
                        conn = DriverManager.getConnection(dbURL, username, password);
                        mainWindow(conn);
                        vBoxTables.remove(loginButton);
                        vBoxTables.remove(hBoxUsername);
                        vBoxTables.remove(hBoxPass);
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to connect to database: " + ex.getMessage(),
                                "Connection Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Username or password missing", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        
        JPanel Login = new JPanel();
        Login.add(vBoxTables);
        vBoxTables.add(loginButton);
        vBoxTables.add(new JPanel());
        
//        FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
//		layout.setVgap(250);
//		layout.setAlignment(FlowLayout.CENTER);
//		
//		Login.setLayout(layout);
//        
//        add(Login);
        add(vBoxTables);
    }

    private void mainWindow(Connection conn) {
//    	JFrame mainWindow = new JFrame();
    	Box hBoxButtons = Box.createHorizontalBox();
    	Box hBoxButtons2 = Box.createHorizontalBox();
    	Box hBoxContent = Box.createHorizontalBox();
    	Box vBoxContent = Box.createVerticalBox();
    	JPanel panelButtons = new JPanel(new GridBagLayout());
    	hBoxContent.add(panelButtons);
    	
    	
    	JPanel tableContent = new JPanel();
    	
    	FlowLayout flow = new FlowLayout();
//    	GridLayout grid = new GridLayout();
    	flow.setAlignment(FlowLayout.LEFT);
    	
    	
    	flow.setHgap(50);
    	
    	hBoxContent.add(vBoxContent);
    	
    	vBoxContent.add(Box.createVerticalGlue());
    	vBoxContent.add(hBoxButtons);
    	vBoxContent.add(tableContent);
    	vBoxContent.add(hBoxButtons2);
    	vBoxContent.add(Box.createVerticalGlue());
    	
    	//create buttons above and below table
    	JButton AddButton = new JButton("Add");
		JButton DeleteButton = new JButton("Remove");
		JButton SearchButton = new JButton("Search");
		JButton ClearButton = new JButton("Clear");
		JButton RefreshButton = new JButton("Refresh");
		JButton EditButton = new JButton("Edit");
		
		//buttons above table
		hBoxButtons.add(AddButton);
		hBoxButtons.add(DeleteButton);
		hBoxButtons.add(SearchButton);
		
		//buttons below table
		hBoxButtons2.add(EditButton);
		hBoxButtons2.add(ClearButton);
		hBoxButtons2.add(RefreshButton);
		
		EditButton.addActionListener(new ActionListener() {
			// TODO Edit Button
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton saveButton = new JButton("Update");

				if (tableSelected.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No Table Selected", "Error", JOptionPane.ERROR_MESSAGE);

				} else {

					JFrame inputframe = new JFrame("Edit a Row");

					JTextField KeyField = new JTextField(10);
					JTextField ItemField = new JTextField(10);
					JTextField ChangeField = new JTextField(10);
					JTextField KeyNameField = new JTextField(10);

					saveButton.addActionListener(e1 -> {

						boolean update = true;

						String Key = "";
						String KeyName = "";
						String Item = "";
						String Change = "";

						if (KeyField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Must include primary key!", "Error",
									JOptionPane.ERROR_MESSAGE);
							update = false;
						} else
							Key = KeyField.getText();
						
						if (KeyNameField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Must include name of primary key!", "Error",
									JOptionPane.ERROR_MESSAGE);
							update = false;
						} else
							KeyName = KeyNameField.getText();

						if (ItemField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Must include field to update!", "Error",
									JOptionPane.ERROR_MESSAGE);
							update = false;
						} else
							Item = ItemField.getText();

						Change = ChangeField.getText();

						String sql = "";
						try {
							Statement stmt = conn.createStatement();

							System.out.println("Updating item in the table...");
//							UPDATE dog SET buildingid = 111118101 WHERE buildingid = 1;
							sql = "UPDATE " + tableSelected + " SET "+ Item + " = '" + Change + "' WHERE "+ KeyName + " = '" + Key + "'";
							if (update == true) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}
						} catch (SQLException e2) {
							e2.printStackTrace();
						}

						inputframe.dispose();
					});

					JPanel inputPanel = new JPanel();
					
					Box hBoxTemp = Box.createHorizontalBox();
					Box vBox1 = Box.createVerticalBox();
					hBoxTemp.add(vBox1);
					Box vBox2 = Box.createVerticalBox();
					hBoxTemp.add(vBox2);
					
					vBox1.add(new JLabel("Name of Primary Key:"));
					vBox2.add(KeyNameField);
					vBox1.add(new JLabel("Primary Key of Object to Change:"));
					vBox2.add(KeyField);
					vBox1.add(new JLabel("Column to Update:"));
					vBox2.add(ItemField);
					vBox1.add(new JLabel("New Value:"));
					vBox2.add(ChangeField);
					
					inputPanel.add(hBoxTemp);

					inputPanel.add(saveButton);

					inputframe.add(inputPanel);

					inputframe.pack();
					inputframe.setVisible(true);

				} 
			}
			
		});
		
		tableSelected = "dog";
		adjustPanelTable(tableSelected, conn, tableContent);
		
        
		JButton button1 = new JButton("Budget");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Budget", conn, tableContent);
            	tableSelected = "budget";
            }
        });
//        button1.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton button2 = new JButton("Building");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Building", conn, tableContent);
            	tableSelected = "building";
            }
        });
        JButton button3 = new JButton("Customer");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Customer", conn, tableContent);
            	tableSelected = "customer";
            }
        });
        JButton button4 = new JButton("Dog");
        button4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Dog", conn, tableContent);
            	tableSelected = "dog";
            }
        });
        JButton button5 = new JButton("Employee");
        button5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Employee", conn, tableContent);
            	tableSelected = "employee";
            }
        });
        JButton button6 = new JButton("Fundraiser");
        button6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Fundrasier", conn, tableContent);
            	tableSelected = "fundrasier";
            }
        });
        JButton button7 = new JButton("Medical Records");
        button7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Medical_Records", conn, tableContent);
            	tableSelected = "medical_records";
            }
        });
        JButton button8 = new JButton("Social Media");
        button8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Social_Media", conn, tableContent);
            	tableSelected = "social_media";
            }
        });
        JButton button9 = new JButton("Transactions");
        button9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Transactions", conn, tableContent);
            	tableSelected = "transactions";
            }
        });
        JButton button10 = new JButton("Transportation");
        button10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable("Transportation", conn, tableContent);
            	tableSelected = "transportation";
            }
        });
        
        RefreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.show();
            	adjustPanelTable(tableSelected, conn, tableContent);
            }
        });

        
        
        
//        panelButtons.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
//        
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,5,5,5);
        
        panelButtons.add(button1, c);
        c.gridy=1;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button2, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button3, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button4, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button5, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button6, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button7, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button8, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button9, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
        panelButtons.add(button10, c);
        c.gridy++;
//        panelButtons.add(Box.createVerticalStrut(10));
//        panelButtons.add(ClearButton, c);

        
        
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel1.setAlignmentY(CENTER_ALIGNMENT);
        
        panel1.add(hBoxContent);
        
        add(panel1);
        
        
//        
//        add(hBoxContent);
        
        
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        
        ClearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	vBoxContent.hide();
            	adjustPanelTable("", conn, tableContent);
            	tableSelected = "";
            }
        });
        
        AddButton.addActionListener(add);
        
		DeleteButton.addActionListener(remove);
		
		SearchButton.addActionListener(search);
    }
    
//    public void generateSearchTable(String column, String userInput, JFrame inputFrame, Connection conn) {
//    	JPanel panel = new JPanel();
//    	DefaultTableModel model = (DefaultTableModel) table.getModel();
//        model.addRow(new Object[]{});
//        
//        try {
//            String query = "SELECT * FROM " + tableSelected + " WHERE " + column + " = '" + userInput + "'";
//            PreparedStatement statement = conn.prepareStatement(query);
//            statement.executeUpdate();
//            statement.close();
//        }
//        catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null, "Failed to search row: " + ex.getMessage(),
//                    "Search Error", JOptionPane.ERROR_MESSAGE);
//        }
//
//    	JTable table = new JTable(model);
//        JScrollPane scrollPane = new JScrollPane(table);
//        
//        JViewport view = new JViewport();
//        view.setView(table.getTableHeader());
//        scrollPane.setViewport(view);
//        panel.removeAll();
//        
//        Box vBoxTable = Box.createVerticalBox();
//        vBoxTable.add(scrollPane);
//        vBoxTable.add(table);
//
//        panel.add(vBoxTable);
//        
//        panel.revalidate();
//        panel.repaint();
//    }
    
    // TODO update table model
    private void adjustPanelTable(String tableName, Connection conn, JPanel panel) {

        try {
        	if (tableName.isEmpty()) {
        		panel.removeAll();
        		panel.revalidate();
                panel.repaint();
                return;
        	}
        	
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();
            DefaultTableModel model = new DefaultTableModel();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                model.addColumn(metaData.getColumnLabel(columnIndex));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    row[columnIndex - 1] = rs.getObject(columnIndex);
                }
                model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            
            JViewport view = new JViewport();
            view.setView(table.getTableHeader());
            scrollPane.setViewport(view);
            panel.removeAll();
            
            Box vBoxTable = Box.createVerticalBox();
            vBoxTable.add(scrollPane);
            vBoxTable.add(table);

            panel.add(vBoxTable);
            
            panel.revalidate();
            panel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve data from table: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // TODO Create & Add Table with search parameters
 	private static void createSearchTable(Connection conn, JPanel panel, String[] ColumnArr, JTextField[] TextArr) {
 		int n = TextArr.length;
 		
 		String sql = "SELECT * FROM " + tableSelected + " where ";

 		boolean isEmpty = true;
 		for (int i=0; i<TextArr.length; i++) {
 			if (!TextArr[i].getText().isEmpty()) isEmpty = false;
 			
 		}
 		if (isEmpty == true) {
 			JOptionPane.showMessageDialog(null, "No parameters included", "Error", JOptionPane.ERROR_MESSAGE);
 			return;
 		}

 		boolean and = false;
 		for (int i=0; i<n; i++) {
 			
 			if (!TextArr[i].getText().isEmpty()) {
 				if (and == true) sql += " and ";
 				sql += "" + ColumnArr[i] + " = '" + TextArr[i].getText() + "'";
 				and = true;
 			}
 		}

 		Statement stmt;
 		try {
 			stmt = conn.createStatement();
 			System.out.println(sql);
 			ResultSet rs = stmt.executeQuery(sql);
 			ResultSetMetaData metaData = rs.getMetaData();
 			
 			int columnCount = metaData.getColumnCount();
             DefaultTableModel model = new DefaultTableModel();
             for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                 model.addColumn(metaData.getColumnLabel(columnIndex));
             }

             while (rs.next()) {
                 Object[] row = new Object[columnCount];
                 for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                     row[columnIndex - 1] = rs.getObject(columnIndex);
                 }
                 model.addRow(row);
             }

             JTable table = new JTable(model);
             JScrollPane scrollPane = new JScrollPane(table);
             
             JViewport view = new JViewport();
             view.setView(table.getTableHeader());
             scrollPane.setViewport(view);
             panel.removeAll();
             
             Box vBoxTable = Box.createVerticalBox();
             vBoxTable.add(scrollPane);
             vBoxTable.add(table);

             panel.add(vBoxTable);
             
             panel.revalidate();
             panel.repaint();

         } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Failed to retrieve data from table: " + ex.getMessage(),
                     "Error", JOptionPane.ERROR_MESSAGE);
         }
 		
 	}
 
    static ActionListener add = new ActionListener() {
    	// TODO Add
		@Override
		public void actionPerformed(ActionEvent e) {
		
			JButton saveButton = new JButton("Save");

			if (tableSelected.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No Table Selected", "Error", JOptionPane.ERROR_MESSAGE);

			} else if (tableSelected == "budget") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField BudgetYearField = new JTextField(10);
				JTextField IncomeField = new JTextField(10);
				JTextField ExpenseField = new JTextField(10);

				saveButton.addActionListener(e1 -> {

					boolean update = true;

					String BudgetYear = "";
					int income = 0;
					int expense = 0;

					if (BudgetYearField.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Must include primary key!", "Error",
								JOptionPane.ERROR_MESSAGE);
						update = false;
					} else
						BudgetYear = BudgetYearField.getText();

					if (IncomeField.getText().isEmpty()) {
						income = 0;
					} else
						income = Integer.parseInt(IncomeField.getText());

					if (ExpenseField.getText().isEmpty()) {
						expense = 0;
					} else
						expense = Integer.parseInt(ExpenseField.getText());

					String sql = "";
					try {
						Statement stmt = conn.createStatement();

						System.out.println("Inserting records into the table...");
						sql = "INSERT INTO budget VALUES ('" + BudgetYear + "', '" + income + "', '" + expense + "')";
						if (update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Budget Year:"));
				inputPanel.add(BudgetYearField);
				inputPanel.add(new JLabel("Year's Total Income:"));
				inputPanel.add(IncomeField);
				inputPanel.add(new JLabel("Year's Total Expenses:"));
				inputPanel.add(ExpenseField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "building") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField BuildingIDField = new JTextField(10);
				JTextField StateField = new JTextField(10);
				JTextField CityField = new JTextField(10);
				JTextField ZipcodeField = new JTextField(10);
				JTextField AddressField = new JTextField(10);

				saveButton.addActionListener(e1 -> {
					boolean update = true;
					
					int buildingid = 0;
					if (BuildingIDField.getText().isEmpty()) {
						update = false;
						JOptionPane.showMessageDialog(null, "Must include primary key!", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else
						buildingid = Integer.parseInt(BuildingIDField.getText());

					String state = StateField.getText();
					if (StateField.getText().isEmpty())
						state = "N/A";

					String city = CityField.getText();
					if (CityField.getText().isEmpty())
						city = "N/A";

					int zipcode;
					if (ZipcodeField.getText().isEmpty()) {
						zipcode = 0;
					} else
						zipcode = Integer.parseInt(ZipcodeField.getText());

					String address = AddressField.getText();
					if (AddressField.getText().isEmpty())
						address = "N/A";
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						
						System.out.println("Inserting records into the table...");          
						sql = "INSERT INTO building VALUES ('"+buildingid+"', '" + state + "', '"+city+"', '"+zipcode+"', '"+address+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}
					
					
					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("State:"));
				inputPanel.add(StateField);
				inputPanel.add(new JLabel("City:"));
				inputPanel.add(CityField);
				inputPanel.add(new JLabel("Zipcode:"));
				inputPanel.add(ZipcodeField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "customer") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField FNameField = new JTextField(10);
				JTextField MInitField = new JTextField(10);
				JTextField LNameField = new JTextField(10);
				JTextField CustomerIDField = new JTextField(10);
				JTextField BDateField = new JTextField(10);
				JTextField AddressField = new JTextField(10);
				JTextField SexField = new JTextField(10);
				JTextField EmailField = new JTextField(10);

				saveButton.addActionListener(e1 -> {

					boolean update = true; 
					
					int customerid = 0;
					if (CustomerIDField.getText().isEmpty()) {
						update = false;
					} else
						customerid = Integer.parseInt(CustomerIDField.getText());
					
					String Fname="";
					if (FNameField.getText().isEmpty()) Fname = "N/A";
					else Fname = FNameField.getText();
					
					String MInit = "";
					if (MInitField.getText().isEmpty()) MInit = "N/A";
					else MInit = MInitField.getText();
					
					String LName = "";
					if (LNameField.getText().isEmpty()) LName = "N/A";
					else LName = LNameField.getText();
					
					String BDate = "";
					if (BDateField.getText().isEmpty()) BDate = "0000-00-00";
					else BDate = BDateField.getText();
					
					String Address = "";
					if (AddressField.getText().isEmpty()) Address = "N/A";
					else Address = AddressField.getText();
					
					String Sex = "";
					if (SexField.getText().isEmpty()) Sex = "N/A";
					else Sex = SexField.getText();
					
					String Email = "";
					if (EmailField.getText().isEmpty()) Email = "N/A";
					else Email = EmailField.getText();
					
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting customer into the table...");          
						sql = "INSERT INTO customer VALUES ('"+Fname+"', '" + MInit + "', '"+LName+"', '"+customerid+"', '"+BDate+"', '"+Address+"', '"+Sex+"', '"+Email+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("First Name:"));
				inputPanel.add(FNameField);
				inputPanel.add(new JLabel("Middle Initial:"));
				inputPanel.add(MInitField);
				inputPanel.add(new JLabel("Last Name:"));
				inputPanel.add(LNameField);
				inputPanel.add(new JLabel("Customer ID:"));
				inputPanel.add(CustomerIDField);
				inputPanel.add(new JLabel("Birth Date:"));
				inputPanel.add(BDateField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);
				inputPanel.add(new JLabel("Gender:"));
				inputPanel.add(SexField);
				inputPanel.add(new JLabel("e-Mail address:"));
				inputPanel.add(EmailField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "dog") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField FNameField = new JTextField(10);
				JTextField BreedField = new JTextField(10);
				JTextField EyeColorField = new JTextField(10);
				JTextField WeightField = new JTextField(10);
				JTextField HeightField = new JTextField(10);
				JTextField SexField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				JTextField DogIDField = new JTextField(10);
				JTextField MediaIDField = new JTextField(10);
				JTextField AdoptedField = new JTextField(10);
				JTextField RecordIDField = new JTextField(10);

				saveButton.addActionListener(e1 -> {
					
					boolean update = true;
					
					String DogID = DogIDField.getText();
					if (DogIDField.getText().isEmpty())
						update = false;
					
					String FName="";
					if (FNameField.getText().isEmpty()) FName = "N/A";
					else FName = FNameField.getText();
					
					String Breed="";
					if (BreedField.getText().isEmpty()) Breed = "N/A";
					else Breed = BreedField.getText();

					String EyeColor="";
					if (EyeColorField.getText().isEmpty()) EyeColor = "N/A";
					else EyeColor = EyeColorField.getText();
					
					String Weight="";
					if (WeightField.getText().isEmpty()) Weight = "N/A";
					else Weight = WeightField.getText();
					
					String Height="";
					if (HeightField.getText().isEmpty()) Height = "N/A";
					else Height = HeightField.getText();
					
					String Sex="";
					if (SexField.getText().isEmpty()) Sex = "N/A";
					else Sex = SexField.getText();
					
					String BuildingID="";
					if (BuildingIDField.getText().isEmpty()) BuildingID = "N/A";
					else BuildingID = BuildingIDField.getText();
					
					String MediaID="";
					if (MediaIDField.getText().isEmpty()) MediaID = "N/A";
					else MediaID = MediaIDField.getText();
					
					String Adopted="";
					if (AdoptedField.getText().isEmpty()) Adopted = "N/A";
					else Adopted = AdoptedField.getText();
					
					String RecordID="";
					if (RecordIDField.getText().isEmpty()) RecordID = "N/A";
					else RecordID = RecordIDField.getText();
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting dog into the table...");       

						sql = "INSERT INTO dog VALUES ('"+FName+"', '" + Breed + "', '"+EyeColor+"', '"+Weight+"', '"+Height+"', '"+Sex+"', '"+BuildingID+"', '"+DogID+"', '"+MediaID+"', '"+Adopted+"', '"+RecordID+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				Box hBox1 = Box.createHorizontalBox();
				Box hBox2 = Box.createHorizontalBox();
				Box vBox = Box.createVerticalBox();

				hBox1.add(new JLabel("First Name:"));
				hBox1.add(FNameField);
				hBox1.add(new JLabel("Breed(s)"));
				hBox1.add(BreedField);
				hBox1.add(new JLabel("Eye Color(s)"));
				hBox1.add(EyeColorField);
				hBox1.add(new JLabel("Weight:"));
				hBox1.add(WeightField);
				hBox1.add(new JLabel("Height:"));
				hBox1.add(HeightField);
				hBox1.add(new JLabel("Sex:"));
				hBox1.add(SexField);
				hBox2.add(new JLabel("Building ID:"));
				hBox2.add(BuildingIDField);
				hBox2.add(new JLabel("Dog ID:"));
				hBox2.add(DogIDField);
				hBox2.add(new JLabel("Media ID:"));
				hBox2.add(MediaIDField);
				hBox2.add(new JLabel("Adopted (Y/N):"));
				hBox2.add(AdoptedField);
				hBox2.add(new JLabel("Medical Record ID:"));
				hBox2.add(RecordIDField);

				vBox.add(hBox1);
				vBox.add(hBox2);
				vBox.add(saveButton);

				inputPanel.add(vBox);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "employee") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField EmployeeIDField = new JTextField(10);
				JTextField FNameField = new JTextField(10);
				JTextField MInitField = new JTextField(10);
				JTextField LNameField = new JTextField(10);
				JTextField DOBField = new JTextField(10);
				JTextField AddressField = new JTextField(10);
				JTextField SexField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);

				saveButton.addActionListener(e1 -> {
					
					boolean update = true;
					
					int EmployeeID = 0;
					if (EmployeeIDField.getText().isEmpty()) {
						update = false;
					} else
						EmployeeID = Integer.parseInt(EmployeeIDField.getText());
					
					String FName="";
					if (FNameField.getText().isEmpty()) FName = "N/A";
					else FName = FNameField.getText();

					String MInit="";
					if (MInitField.getText().isEmpty()) MInit = "N/A";
					else MInit = MInitField.getText();
					
					String LName="";
					if (LNameField.getText().isEmpty()) LName = "N/A";
					else LName = LNameField.getText();
					
					String DOB="";
					if (DOBField.getText().isEmpty()) DOB = "N/A";
					else DOB = DOBField.getText();
					
					String Address="";
					if (AddressField.getText().isEmpty()) Address = "N/A";
					else Address = AddressField.getText();
					
					String Sex="";
					if (SexField.getText().isEmpty()) Sex = "N/A";
					else Sex = SexField.getText();
					
					String BuildingID="";
					if (BuildingIDField.getText().isEmpty()) BuildingID = "N/A";
					else BuildingID = BuildingIDField.getText();
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting dog into the table...");       


						sql = "INSERT INTO employee VALUES ('"+EmployeeID+"', '" + FName + "', '"+MInit+"', '"+LName+"', '"+DOB+"', '"+Address+"', '"+Sex+"', '"+BuildingID+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Employee ID:"));
				inputPanel.add(EmployeeIDField);
				inputPanel.add(new JLabel("First Name:"));
				inputPanel.add(FNameField);
				inputPanel.add(new JLabel("Middle Initial:"));
				inputPanel.add(MInitField);
				inputPanel.add(new JLabel("Last Name:"));
				inputPanel.add(LNameField);
				inputPanel.add(new JLabel("Date of Birth:"));
				inputPanel.add(DOBField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);
				inputPanel.add(new JLabel("Gender:"));
				inputPanel.add(SexField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "fundrasier") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField FundraiserIDField = new JTextField(10);
				JTextField ProfitField = new JTextField(10);
				JTextField FRDescriptionField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				JTextField StateField = new JTextField(10);
				JTextField CityField = new JTextField(10);
				JTextField ZipcodeField = new JTextField(10);
				JTextField AddressField = new JTextField(10);

				saveButton.addActionListener(e1 -> {
					
					boolean update = true;
					
					int FundrasierID = 0;
					if (FundraiserIDField.getText().isEmpty()) {
						update = false;
					} else
						FundrasierID = Integer.parseInt(FundraiserIDField.getText());
					
					String Profit="";
					if (ProfitField.getText().isEmpty()) Profit = "N/A";
					else Profit = ProfitField.getText();
					
					String FRDescription="";
					if (FRDescriptionField.getText().isEmpty()) FRDescription = "N/A";
					else FRDescription = FRDescriptionField.getText();
					
					String BuildingID="";
					if (BuildingIDField.getText().isEmpty()) BuildingID = "N/A";
					else BuildingID = BuildingIDField.getText();
					
					String State="";
					if (StateField.getText().isEmpty()) State = "N/A";
					else State = StateField.getText();
					
					String City="";
					if (CityField.getText().isEmpty()) City = "N/A";
					else City = CityField.getText();
					
					String Zipcode="";
					if (ZipcodeField.getText().isEmpty()) Zipcode = "N/A";
					else Zipcode = ZipcodeField.getText();
					
					String Address="";
					if (AddressField.getText().isEmpty()) Address = "N/A";
					else Address = AddressField.getText();

					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting fundraiser into the table...");       


						sql = "INSERT INTO "+tableSelected+" VALUES ('"+FundrasierID+"', '" + Profit + "', '"+FRDescription+"', '"+BuildingID+"', '"+State+"', '"+City+"', '"+Zipcode+"', '"+Address+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}
					
					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Fundraiser ID:"));
				inputPanel.add(FundraiserIDField);
				inputPanel.add(new JLabel("Profit:"));
				inputPanel.add(ProfitField);
				inputPanel.add(new JLabel("Description:"));
				inputPanel.add(FRDescriptionField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("State:"));
				inputPanel.add(StateField);
				inputPanel.add(new JLabel("City:"));
				inputPanel.add(CityField);
				inputPanel.add(new JLabel("Zipcode:"));
				inputPanel.add(ZipcodeField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "medical_records") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField MedDescField = new JTextField(10);
				JTextField DogIDField = new JTextField(10);
				JTextField RecordIDField = new JTextField(10);

				saveButton.addActionListener(e1 -> {

					boolean update = true;
					
					String MedDesc="";
					if (MedDescField.getText().isEmpty()) MedDesc = "N/A";
					else MedDesc = MedDescField.getText();

					String DogID="";
					if (DogIDField.getText().isEmpty()) DogID = "N/A";
					else DogID = DogIDField.getText();

					int RecordID=0;

					if (RecordIDField.getText().isEmpty()) {
						update=false;
					} else
						RecordID = Integer.parseInt(RecordIDField.getText());

					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting medical record into the table...");    

						sql = "INSERT INTO "+tableSelected+" VALUES ('"+MedDesc+"', '" + DogID + "', '"+RecordID+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Medical Description:"));
				inputPanel.add(MedDescField);
				inputPanel.add(new JLabel("Dog ID:"));
				inputPanel.add(DogIDField);
				inputPanel.add(new JLabel("Record ID:"));
				inputPanel.add(RecordIDField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "social_media") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField URLField = new JTextField(10);
				JTextField UsernameField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				JTextField MediaIDField = new JTextField(10);

				saveButton.addActionListener(e1 -> {

					boolean update = true;
					
					int mediaID=0;
					if (MediaIDField.getText().isEmpty()) {
						update=false;
					} else
						mediaID = Integer.parseInt(MediaIDField.getText());
					
					String Username="";
					if (UsernameField.getText().isEmpty()) Username = "N/A";
					else Username = UsernameField.getText();
					
					String BuildingID="";
					if (BuildingIDField.getText().isEmpty()) BuildingID = "N/A";
					else BuildingID = BuildingIDField.getText();
					
					String URL="";
					if (URLField.getText().isEmpty()) URL = "N/A";
					else URL = URLField.getText();
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting social media into the table...");    

						sql = "INSERT INTO "+tableSelected+" VALUES ('"+URL+"', '" + Username + "', '"+BuildingID+"', '"+mediaID+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("URL:"));
				inputPanel.add(URLField);
				inputPanel.add(new JLabel("Username:"));
				inputPanel.add(UsernameField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("Media ID:"));
				inputPanel.add(MediaIDField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "transactions") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField CustomerIDField = new JTextField(10);
				JTextField DogIDField = new JTextField(10);
				JTextField TransactionDateField = new JTextField(10);
				JTextField TransactionIDField = new JTextField(10);

				saveButton.addActionListener(e1 -> {

					boolean update = true;
					
					int transactionid = 0;
					if (TransactionIDField.getText().isEmpty()) {
						update = false;
					} else
						transactionid = Integer.parseInt(TransactionIDField.getText());
					
					String CustomerID="";
					if (CustomerIDField.getText().isEmpty()) CustomerID = "N/A";
					else CustomerID = CustomerIDField.getText();
					
					String DogID="";
					if (DogIDField.getText().isEmpty()) DogID = "N/A";
					else DogID = DogIDField.getText();
					
					String TransactionDate="";
					if (TransactionDateField.getText().isEmpty()) TransactionDate = "0000-00-00";
					else TransactionDate = TransactionDateField.getText();
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting transaction into the table...");    

						sql = "INSERT INTO "+tableSelected+" VALUES ('"+CustomerID+"', '" + DogID + "', '"+TransactionDate+"', '"+transactionid+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Customer ID:"));
				inputPanel.add(CustomerIDField);
				inputPanel.add(new JLabel("Dog ID:"));
				inputPanel.add(DogIDField);
				inputPanel.add(new JLabel("Transaction Date:"));
				inputPanel.add(TransactionDateField);
				inputPanel.add(new JLabel("Transaction ID:"));
				inputPanel.add(TransactionIDField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			} else if (tableSelected == "transportation") {

				JFrame inputframe = new JFrame("Add a Row");

				JTextField TransportIDField = new JTextField(10);
				JTextField TransportCostField = new JTextField(10);
				JTextField RescueDateField = new JTextField(10);
				JTextField RescueLocField = new JTextField(10);

				saveButton.addActionListener(e1 -> {
					boolean update = true;
					
					int transportid;
					if (TransportIDField.getText().isEmpty()) {
						transportid = 0;
					} else
						transportid = Integer.parseInt(TransportIDField.getText());
					
					String TransportCost="";
					if (TransportCostField.getText().isEmpty()) TransportCost = "N/A";
					else TransportCost = TransportCostField.getText();
					
					String RescueDate="";
					if (RescueDateField.getText().isEmpty()) RescueDate = "N/A";
					else RescueDate = RescueDateField.getText();
					
					String RescueLoc="";
					if (RescueLocField.getText().isEmpty()) RescueLoc = "N/A";
					else RescueLoc = RescueLocField.getText();
					
					String sql = "";
					try {
						Statement stmt = conn.createStatement();
						System.out.println("Inserting transportation into the table...");    

						sql = "INSERT INTO "+tableSelected+" VALUES ('"+transportid+"', '" + TransportCost + "', '"+RescueDate+"', '"+RescueLoc+"')";						
						if(update == true) {
							System.out.println(sql);
							stmt.executeUpdate(sql);
						}	
					} catch (SQLException e2) {
						//  Auto-generated catch block
						e2.printStackTrace();
					}

					inputframe.dispose();
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Transport ID:"));
				inputPanel.add(TransportIDField);
				inputPanel.add(new JLabel("Cost of Transport:"));
				inputPanel.add(TransportCostField);
				inputPanel.add(new JLabel("Rescue Date:"));
				inputPanel.add(RescueDateField);
				inputPanel.add(new JLabel("Rescue Location:"));
				inputPanel.add(RescueLocField);

				inputPanel.add(saveButton);

				inputframe.add(inputPanel);

				inputframe.pack();
				inputframe.setVisible(true);

			}
		}
	};
    
	static ActionListener remove = new ActionListener() {

		// TODO Remove Button
		public void actionPerformed(ActionEvent e) {
			//  Auto-generated method stub
			{
				JButton removeButton = new JButton("Remove");
				
				if (tableSelected.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No Table Selected", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (tableSelected == "budget"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField BudgetYearField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						int BudgetYear = 0;

						boolean isInt = true;
						for (int i = 0; i < BudgetYearField.getText().length(); i++) {
							if (!Character.isDigit(BudgetYearField.getText().charAt(i)))
								isInt = false;
						}

						boolean retry = false;

						if (BudgetYearField.getText().isEmpty() || isInt == false) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							BudgetYear = Integer.parseInt(BudgetYearField.getText());
							retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("removing budget from the table...");          
							sql = "DELETE FROM budget WHERE budgetyear = "+BudgetYear+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}
						
						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Budget ID:"));
					deletePanel.add(BudgetYearField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "building"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField BuildingIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						int buildingid = 0;

						boolean isInt = true;
						for (int i = 0; i < BuildingIDField.getText().length(); i++) {
							if (!Character.isDigit(BuildingIDField.getText().charAt(i)))
								isInt = false;
						}

						boolean retry = false;

						if (BuildingIDField.getText().isEmpty() || isInt == false) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							buildingid = Integer.parseInt(BuildingIDField.getText());
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM building WHERE buildingid = "+buildingid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}

						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Building ID:"));
					deletePanel.add(BuildingIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "customer"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField CustomerIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String customerid="";

						
						boolean retry = false;

						if (CustomerIDField.getText().isEmpty()) {
							retry=true;
						} else {
							customerid = CustomerIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM customer WHERE customerid = "+customerid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}
						
						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Customer ID:"));
					deletePanel.add(CustomerIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "dog"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField DogIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String dogid="";

						

						boolean retry = false;

						if (DogIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							dogid = DogIDField.getText();
//						retry = false;
						}

						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM dog WHERE dogid = "+dogid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}
						
						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Dog ID:"));
					deletePanel.add(DogIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "employee"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField EmployeeIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String employeeid = "";

						boolean retry = false;

						if (EmployeeIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							employeeid = EmployeeIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM employee WHERE employeeid = "+employeeid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}

						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Employee ID:"));
					deletePanel.add(EmployeeIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "fundrasier"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField FundraiserIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String fundrasierid="";

						boolean retry = false;

						if (FundraiserIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							fundrasierid = FundraiserIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM fundrasier WHERE fundrasierid = "+fundrasierid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}

						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Fundraiser ID:"));
					deletePanel.add(FundraiserIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "medical_records"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField RecordIDField = new JTextField(10);
					
					removeButton.addActionListener(e1 -> {

						String RecordID="";

						boolean retry = false;

						if (RecordIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							RecordID = RecordIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM medical_records WHERE recordid = "+RecordID+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}
						
						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Record ID:"));
					deletePanel.add(RecordIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				}else if (tableSelected == "social_media"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField Social_MediaIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String social_mediaid="";

						boolean retry = false;

						if (Social_MediaIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							social_mediaid = Social_MediaIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM social_media WHERE mediaid = "+social_mediaid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}

						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Social Media ID:"));
					deletePanel.add(Social_MediaIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "transactions"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField TransactionsIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String transactionsid="";

						boolean retry = false;

						if (TransactionsIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							transactionsid = TransactionsIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM transactions WHERE transactionid = "+transactionsid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}

						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Transactions ID:"));
					deletePanel.add(TransactionsIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} else if (tableSelected == "transportation"){

					JFrame deleteFrame = new JFrame("Delete a Row");

					JTextField TransportationIDField = new JTextField(10);

					
					removeButton.addActionListener(e1 -> {

						String transportid="";

						boolean retry = false;

						if (TransportationIDField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Please Enter a valid key to delete", "Error",
									JOptionPane.ERROR_MESSAGE);
							retry = true;
						} else {
							transportid = TransportationIDField.getText();
//						retry = false;
						}
						
						String sql = "";
						try {
							Statement stmt = conn.createStatement();
							
							System.out.println("Inserting records into the table...");          
							sql = "DELETE FROM transportation WHERE transportid = "+transportid+"";						
							if(retry == false) {
								System.out.println(sql);
								stmt.executeUpdate(sql);
							}	
						} catch (SQLException e2) {
							//  Auto-generated catch block
							e2.printStackTrace();
						}

						if (retry == false)
							deleteFrame.dispose();
					});

					JPanel deletePanel = new JPanel();

					deletePanel.add(new JLabel("Transportation ID:"));
					deletePanel.add(TransportationIDField);

					deletePanel.add(removeButton);

					deleteFrame.add(deletePanel);

					deleteFrame.pack();
					deleteFrame.setVisible(true);

				} 
			}
		}
		
	};
		
	static ActionListener search = new ActionListener() {
		// TODO Search
		
		public void actionPerformed(ActionEvent e) {
			JButton searchButton = new JButton("Search");
        	JFrame inputFrame;
        	
        	if (tableSelected.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No Table Selected", "Error", JOptionPane.ERROR_MESSAGE);

			} else if (tableSelected == "budget") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField BudgetYearField = new JTextField(10);
				JTextField IncomeField = new JTextField(10);
				JTextField ExpenseField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {
					
					JTextField[] TextArr = {BudgetYearField, IncomeField, ExpenseField};	//array containing all textfields
					String[] ColumnArr = {"budgetyear", "annualincome", "annualexpense"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);

				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Budget Year:"));
				inputPanel.add(BudgetYearField);
				inputPanel.add(new JLabel("Year's Total Income:"));
				inputPanel.add(IncomeField);
				inputPanel.add(new JLabel("Year's Total Expenses:"));
				inputPanel.add(ExpenseField);
				
				inputPanel.add(searchButton);
				
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "building") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);				

				JTextField BuildingIDField = new JTextField(10);
				JTextField StateField = new JTextField(10);
				JTextField CityField = new JTextField(10);
				JTextField ZipcodeField = new JTextField(10);
				JTextField AddressField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {
					
					JTextField[] TextArr = {BuildingIDField, StateField, CityField, ZipcodeField, AddressField};	//array containing all textfields
					String[] ColumnArr = {"buildingid", "state", "city", "zipcode", "address"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("State:"));
				inputPanel.add(StateField);
				inputPanel.add(new JLabel("City:"));
				inputPanel.add(CityField);
				inputPanel.add(new JLabel("Zipcode:"));
				inputPanel.add(ZipcodeField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);

				inputPanel.add(searchButton);

				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "customer") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField FNameField = new JTextField(10);
				JTextField MInitField = new JTextField(10);
				JTextField LNameField = new JTextField(10);
				JTextField CustomerIDField = new JTextField(10);
				JTextField BDateField = new JTextField(10);
				JTextField AddressField = new JTextField(10);
				JTextField SexField = new JTextField(10);
				JTextField EmailField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {FNameField, MInitField, LNameField, CustomerIDField, BDateField, AddressField, SexField, EmailField};	//array containing all textfields
					String[] ColumnArr = {"fname", "minit", "lname", "customerid", "bdate", "address", "sex", "email"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("First Name:"));
				inputPanel.add(FNameField);
				inputPanel.add(new JLabel("Middle Initial:"));
				inputPanel.add(MInitField);
				inputPanel.add(new JLabel("Last Name:"));
				inputPanel.add(LNameField);
				inputPanel.add(new JLabel("Customer ID:"));
				inputPanel.add(CustomerIDField);
				inputPanel.add(new JLabel("Birth Date:"));
				inputPanel.add(BDateField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);
				inputPanel.add(new JLabel("Gender:"));
				inputPanel.add(SexField);
				inputPanel.add(new JLabel("e-Mail address:"));
				inputPanel.add(EmailField);

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "dog") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField FNameField = new JTextField(10);
				JTextField BreedField = new JTextField(10);
				JTextField EyeColorField = new JTextField(10);
				JTextField WeightField = new JTextField(10);
				JTextField HeightField = new JTextField(10);
				JTextField SexField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				JTextField DogIDField = new JTextField(10);
				JTextField MediaIDField = new JTextField(10);
				JTextField AdoptedField = new JTextField(10);
				JTextField RecordIDField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {FNameField, BreedField, EyeColorField, WeightField, HeightField, SexField, BuildingIDField, DogIDField, MediaIDField, AdoptedField, RecordIDField};	//array containing all textfields
					String[] ColumnArr = {"fname", "breed", "eyecolor", "weight", "height", "sex", "buildingid", "dogid", "mediaid", "adopted", "recordid"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				
				
				
				inputPanel.add(new JLabel("First Name:"));
				inputPanel.add(FNameField);
				inputPanel.add(new JLabel("Breed(s)"));
				inputPanel.add(BreedField);
				inputPanel.add(new JLabel("Eye Color(s)"));
				inputPanel.add(EyeColorField);
				inputPanel.add(new JLabel("Weight:"));
				inputPanel.add(WeightField);
				inputPanel.add(new JLabel("Height:"));
				inputPanel.add(HeightField);
				inputPanel.add(new JLabel("Sex:"));
				inputPanel.add(SexField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("Dog ID:"));
				inputPanel.add(DogIDField);
				inputPanel.add(new JLabel("Media ID:"));
				inputPanel.add(MediaIDField);
				inputPanel.add(new JLabel("Adopted (Y/N):"));
				inputPanel.add(AdoptedField);
				inputPanel.add(new JLabel("Medical Record ID:"));
				inputPanel.add(RecordIDField);
				
				

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(900, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "employee") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField EmployeeIDField = new JTextField(10);
				JTextField FNameField = new JTextField(10);
				JTextField MInitField = new JTextField(10);
				JTextField LNameField = new JTextField(10);
				JTextField DOBField = new JTextField(10);
				JTextField AddressField = new JTextField(10);
				JTextField SexField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {EmployeeIDField, FNameField, MInitField, LNameField, DOBField, AddressField, SexField, BuildingIDField};	//array containing all textfields
					String[] ColumnArr = {"employeeid", "fname", "minit", "lname", "dob", "address", "sex", "buildingid"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				
				
				
				inputPanel.add(new JLabel("Employee ID:"));
				inputPanel.add(EmployeeIDField);
				inputPanel.add(new JLabel("First Name:"));
				inputPanel.add(FNameField);
				inputPanel.add(new JLabel("Middle Initial:"));
				inputPanel.add(MInitField);
				inputPanel.add(new JLabel("Last Name:"));
				inputPanel.add(LNameField);
				inputPanel.add(new JLabel("Date of Birth:"));
				inputPanel.add(DOBField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);
				inputPanel.add(new JLabel("Gender:"));
				inputPanel.add(SexField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);

				

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "fundrasier") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField FundraiserIDField = new JTextField(10);
				JTextField ProfitField = new JTextField(10);
				JTextField FRDescriptionField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				JTextField StateField = new JTextField(10);
				JTextField CityField = new JTextField(10);
				JTextField ZipcodeField = new JTextField(10);
				JTextField AddressField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {FundraiserIDField, ProfitField, FRDescriptionField, BuildingIDField, StateField, CityField, ZipcodeField, AddressField};	//array containing all textfields
					String[] ColumnArr = {"fundrasierid", "profit", "frdescription", "buildingid", "state", "city", "zipcode", "address"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				
				inputPanel.add(new JLabel("Fundraiser ID:"));
				inputPanel.add(FundraiserIDField);
				inputPanel.add(new JLabel("Profit:"));
				inputPanel.add(ProfitField);
				inputPanel.add(new JLabel("Description:"));
				inputPanel.add(FRDescriptionField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("State:"));
				inputPanel.add(StateField);
				inputPanel.add(new JLabel("City:"));
				inputPanel.add(CityField);
				inputPanel.add(new JLabel("Zipcode:"));
				inputPanel.add(ZipcodeField);
				inputPanel.add(new JLabel("Address:"));
				inputPanel.add(AddressField);

				

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "medical_records") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField MedDescField = new JTextField(10);
				JTextField DogIDField = new JTextField(10);
				JTextField RecordIDField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {MedDescField, DogIDField, RecordIDField};	//array containing all textfields
					String[] ColumnArr = {"meddescription", "dogid", "recordid"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Medical Description:"));
				inputPanel.add(MedDescField);
				inputPanel.add(new JLabel("Dog ID:"));
				inputPanel.add(DogIDField);
				inputPanel.add(new JLabel("Record ID:"));
				inputPanel.add(RecordIDField);

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "social_media") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField URLField = new JTextField(10);
				JTextField UsernameField = new JTextField(10);
				JTextField BuildingIDField = new JTextField(10);
				JTextField MediaIDField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {URLField, UsernameField, BuildingIDField, MediaIDField};	//array containing all textfields
					String[] ColumnArr = {"url", "username", "buildingid", "mediaid"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("URL:"));
				inputPanel.add(URLField);
				inputPanel.add(new JLabel("Username:"));
				inputPanel.add(UsernameField);
				inputPanel.add(new JLabel("Building ID:"));
				inputPanel.add(BuildingIDField);
				inputPanel.add(new JLabel("Media ID:"));
				inputPanel.add(MediaIDField);

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			} else if (tableSelected == "transactions") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField CustomerIDField = new JTextField(10);
				JTextField DogIDField = new JTextField(10);
				JTextField TransactionDateField = new JTextField(10);
				JTextField TransactionIDField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {CustomerIDField, DogIDField, TransactionDateField, TransactionIDField};	//array containing all textfields
					String[] ColumnArr = {"customerid", "dogid", "transactiondate", "transactionid"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Customer ID:"));
				inputPanel.add(CustomerIDField);
				inputPanel.add(new JLabel("Dog ID:"));
				inputPanel.add(DogIDField);
				inputPanel.add(new JLabel("Transaction Date:"));
				inputPanel.add(TransactionDateField);
				inputPanel.add(new JLabel("Transaction ID:"));
				inputPanel.add(TransactionIDField);

				inputPanel.add(searchButton);
				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			}  else if (tableSelected == "transportation") {

				inputFrame = new JFrame("Search Rows");
				inputFrame.setLocationRelativeTo(null);

				JTextField TransportIDField = new JTextField(10);
				JTextField TransportCostField = new JTextField(10);
				JTextField RescueDateField = new JTextField(10);
				JTextField RescueLocField = new JTextField(10);
				
				JPanel tablePanel = new JPanel();
				
				searchButton.addActionListener(e1 -> {

					JTextField[] TextArr = {TransportIDField, TransportCostField, RescueDateField, RescueLocField};	//array containing all textfields
					String[] ColumnArr = {"transportid", "transportcost", "rescuedate", "rescueloc"};		//array containing the variable name for each column, in same order as text fields
					
					createSearchTable(conn, tablePanel, ColumnArr, TextArr);
					
				});

				JPanel inputPanel = new JPanel();
				inputPanel.add(new JLabel("Transport ID:"));
				inputPanel.add(TransportIDField);
				inputPanel.add(new JLabel("Cost of Transport:"));
				inputPanel.add(TransportCostField);
				inputPanel.add(new JLabel("Rescue Date:"));
				inputPanel.add(RescueDateField);
				inputPanel.add(new JLabel("Rescue Location:"));
				inputPanel.add(RescueLocField);

				inputPanel.add(searchButton);

				Box Search = Box.createVerticalBox();
				
				Search.add(inputPanel);
				Search.add(tablePanel);
				
				inputFrame.add(Search);

				inputFrame.pack();
				inputFrame.setSize(800, 300);
				inputFrame.setVisible(true);

			}
		}
		
	};

	


}