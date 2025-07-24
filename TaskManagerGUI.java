import javax.swing.*;  //builidng gui -- JFrame, JButton, JTextField
import java.awt.*;     //layout management and other gui utilities -- BorderLayout, GridLayout
import java.awt.event.*;  // event handling -- button clicks, key presses
import java.io.*;    //handling file i/o -- ObjectInputStream , O.OutputStream
import java.util.*;  // utility classes like List, ArrayList, Date
import java.util.List;
import javax.swing.table.DefaultTableModel;  //Model class used for working with tables. It holds the data for the table.


public class TaskManagerGUI extends JFrame {
    //JFrame - basic window container in Swing
    private DefaultTableModel taskTableModel;
    private JTable taskTable;  //displays the list of tasks

    //private JList<Task> taskList;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityComboBox;
    private JSpinner dueDateSpinner;
    private JButton addTaskButton, markCompleteButton, saveButton, loadButton, deleteTaskButton, modifyTaskButton;

    private static final String file_name = "tasks.ser";

    public TaskManagerGUI(){
        setTitle("Personal Task Manager");
        setSize(900,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); //align window to center
        setLayout(new BorderLayout());

        taskTableModel = new DefaultTableModel(new Object[]{"Task Name","Priority","Due Date","Status", "Description"},0);
        taskTable = new JTable(taskTableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);  //make the table scrollable
        add(scrollPane, BorderLayout.CENTER);

        //Task Details Panel - title, description, due date, priority
        JPanel taskDetailsPanel = new JPanel();
        taskDetailsPanel.setLayout(new GridLayout(5,2)); //Grid layout for task detail form
        
        taskDetailsPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        taskDetailsPanel.add(titleField); //add text field for title input

        taskDetailsPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);  // Text area for description (3 rows, 20 columns)
        taskDetailsPanel.add(new JScrollPane(descriptionArea));

        taskDetailsPanel.add(new JLabel("Due Date: "));
        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy");
        dueDateSpinner.setEditor(dateEditor);
        taskDetailsPanel.add(dueDateSpinner);  // Add due date spinner

        taskDetailsPanel.add(new JLabel("Priority:"));
        priorityComboBox = new JComboBox<>(new String[] { "High", "Medium", "Low" });
        taskDetailsPanel.add(priorityComboBox);  // Add priority combo box

        add(taskDetailsPanel, BorderLayout.NORTH);  // Add the task details panel to the top of the window
        
        JPanel buttonPanel = new JPanel();
        addTaskButton = new JButton("Add Task");
        markCompleteButton = new JButton("Mark as Completed");
        saveButton = new JButton("Save Tasks");
        loadButton = new JButton("Load Tasks");
        deleteTaskButton = new JButton("Delete Task");
        modifyTaskButton = new JButton("Modify Task");

        buttonPanel.add(addTaskButton);
        buttonPanel.add(markCompleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(modifyTaskButton);
        add(buttonPanel,BorderLayout.SOUTH);

        addTaskButton.addActionListener(e -> addTask());
        markCompleteButton.addActionListener(e -> markTaskComplete());
        saveButton.addActionListener(e -> saveTasks());
        loadButton.addActionListener(e -> loadTasks());
        deleteTaskButton.addActionListener(e -> deleteTask());
        modifyTaskButton.addActionListener(e -> modifyTask());
        }

        private void addTask(){
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            Date dueDate = (Date) dueDateSpinner.getValue();
            String priority = (String) priorityComboBox.getSelectedItem();

            if (title.isEmpty() || dueDate==null || priority==null){
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }
            Task newTask = new Task(title, description, dueDate, priority,"Pending");
            taskTableModel.addRow(new Object[]{newTask.getTitle(),newTask.getPriority(),newTask.getDueDate(),newTask.getStatus(), newTask.getDescription()});

            clearFields();
        }

        private void markTaskComplete(){
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow!=-1){
                taskTableModel.setValueAt("Completed", selectedRow, 4);
            }
            else{
                JOptionPane.showMessageDialog(this,"Please select a task");
            }
        }
        private void saveTasks(){
            try (ObjectOutputStream oost = new ObjectOutputStream(new FileOutputStream(file_name))){
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < taskTableModel.getRowCount(); i++) {
                String title = (String) taskTableModel.getValueAt(i, 0);
                String priority = (String) taskTableModel.getValueAt(i, 1);
                Date dueDate = (Date) taskTableModel.getValueAt(i, 2);
                String status = (String) taskTableModel.getValueAt(i, 3);
                String description = (String) taskTableModel.getValueAt(i, 4);  
                tasks.add(new Task(title, description, dueDate, priority, status)); 
            }
            oost.writeObject(tasks);
            JOptionPane.showMessageDialog(this, "Tasks saved successfully");
            }
            catch (IOException e){
                JOptionPane.showMessageDialog(this, "Error saving tasks.");
            }
        }

        private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file_name))) {
            List<Task> tasks = (List<Task>) ois.readObject();  // Deserialize tasks from file
            taskTableModel.setRowCount(0);  // Clear existing tasks in the model
            for (Task task : tasks) {
                taskTableModel.addRow(new Object[]{task.getTitle(), task.getPriority(), task.getDueDate(), task.getStatus(), task.getDescription()});  // Add loaded tasks back to the Table model
            }
            JOptionPane.showMessageDialog(this, "Tasks loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks.");
        }
    }
        private void deleteTask(){
            int selectedRow = taskTable.getSelectedRow();
            if(selectedRow != -1){
                taskTableModel.removeRow(selectedRow);
            }
            else{
                JOptionPane.showMessageDialog(this, "Please select a task to delete","Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        //needs work
        private void modifyTask() {
            int selectedRow = taskTable.getSelectedRow();
        
            if (selectedRow != -1) {
                String title =(String) taskTableModel.getValueAt(selectedRow, 0);
                String priority = (String) taskTableModel.getValueAt(selectedRow, 1);
                Date dueDate = (Date) taskTableModel.getValueAt(selectedRow, 2);
                String status = (String) taskTableModel.getValueAt(selectedRow, 3);
                String description = (String) taskTableModel.getValueAt(selectedRow, 4);  
        
                // Pre-fill the input fields with the selected task's current data
                titleField.setText(title);
                descriptionArea.setText(description);
                dueDateSpinner.setValue(dueDate);
                priorityComboBox.setSelectedItem(priority);
        
                // After user modifies and confirms, update the table
                int confirmation = JOptionPane.showConfirmDialog(this, "Modify task details?", "Confirm Modification", JOptionPane.YES_NO_OPTION);
        
                if (confirmation == JOptionPane.YES_OPTION) {
                    // Update the row in the table model
                    taskTableModel.setValueAt(titleField.getText(), selectedRow, 0);
                    taskTableModel.setValueAt((String) priorityComboBox.getSelectedItem(), selectedRow, 1);
                    taskTableModel.setValueAt((Date) dueDateSpinner.getValue(), selectedRow, 2);
                    taskTableModel.setValueAt(status, selectedRow, 3);
                    taskTableModel.setValueAt(descriptionArea.getText(), selectedRow, 4); 
            
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a task to modify", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    private void clearFields(){
        titleField.setText("");
        descriptionArea.setText("");
        dueDateSpinner.setValue(new Date());
        priorityComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()-> {
            TaskManagerGUI app = new TaskManagerGUI();
            app.setVisible(true);
        });
    }
    
}
