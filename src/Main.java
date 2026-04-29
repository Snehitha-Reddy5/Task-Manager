import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
class Task {
    private String task;
    private String taskStatus;
    private int taskId;
    private int time;

    Task(String task, String taskStatus, int taskId, int time) {
        this.task = task;
        this.taskStatus = taskStatus;
        this.taskId = taskId;
        this.time = time;
    }

    public String getTask() {
        return task;
    }
    public String getTaskStatus() {
        return taskStatus;
    }
    public int getTaskId() {
        return taskId;
    }
    public int getTime() {
        return time;
    }
    public void setTask(String task) {
        this.task = task;
    }
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public void setTime(int time) {
        this.time = time;
    }

}


class TaskManager {
    ArrayList<Task> tasklist = new ArrayList<>();

    void addTask(String task, String taskStatus, int taskId, int time) {
        tasklist.add(new Task(task, taskStatus, taskId, time));
        System.out.println(task + " Task is added");
    }

    void deleteTask(int taskId) {
        boolean removed = tasklist.removeIf(t -> t.getTaskId() == taskId);
        if (removed) {
            System.out.println("Task is deleted");
        } else {
            System.out.println("not found");
        }
    }

    void markDone(int taskId) {
        boolean found = false;
        for (Task t : tasklist) {
            if (t.getTaskId() == taskId) {
                t.setTaskStatus("Done");
                found = true;
                System.out.println("Task marked as Done");
                break;
            }
        }
        if (!found) {
            System.out.println("not found");
        }
    }

    void viewTask() {
        System.out.println("No.of tasks present: " + tasklist.size());
        for (Task t : tasklist) {
            System.out.println("Task: " + t.getTask());
            System.out.println("Task Status(Done/not Done): " + t.getTaskStatus());
            System.out.println("Task Id: " + t.getTaskId());
            System.out.println("Task reminder: " + t.getTime() + " minutes");
        }
    }

    void saveToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"));

            for (Task t : tasklist) {
                writer.write(t.getTask() + "," + t.getTaskStatus() + "," + t.getTaskId() + "," + t.getTime());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file");
        }
    }

    void loadFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                //if the line doesnt have all the data, continue.
                if (parts.length < 4) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                String name = parts[0];
                String status = parts[1];
                int id = Integer.parseInt(parts[2]);
                int time = Integer.parseInt(parts[3]);

                tasklist.add(new Task(name, status, id, time));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No previous data found");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TaskManager manager = new TaskManager();
        manager.loadFromFile();


        while(true) {
            System.out.println("1.Add Task");
            System.out.println("2.Delete Task");
            System.out.println("3.Marking the Task as Done/not Done");
            System.out.println("4.View Tasks");
            System.out.println("5.Exit");
            System.out.println("Enter a option (1/2/3/4/5) : ");
            int choice;
            try {
                choice = sc.nextInt();
            } catch(Exception e) {
                System.out.println("Invalid input enter a number");
                continue;
            }
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter that task name: ");
                    String n = sc.nextLine();

                    System.out.println("enter the status:");
                    String m = sc.nextLine();

                    System.out.println("enter id:");
                    int i;
                    try {
                        i = sc.nextInt();
                        sc.nextLine();
                    } catch(Exception e) {
                        System.out.println("Invalid input. enter a number");
                        sc.nextLine();
                        continue;
                    }
                    System.out.println("Do you want to add the reminder for this task? (Yes/No)");
                    String s = sc.nextLine();
                    int t = 0;
                    if(s.equalsIgnoreCase("Yes")) {
                        System.out.println("Enter the reminder time (in minutes): ");

                        try {
                            t = sc.nextInt();
                        } catch(Exception e) {
                            System.out.println("Invalid input. enter a number");
                            sc.nextLine();
                            continue;
                        }

                    }
                    else {
                        t = 0;
                        System.out.println("No reminders set");
                    }

                    manager.addTask(n, m, i, t);
                    if (t > 0) {
                        String taskName = n;
                        final int reminderTime = t;
                        Runnable r = new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(reminderTime * 60 * 1000);
                                    System.out.println("Reminder: " + taskName);
                                } catch(InterruptedException e) {
                                    System.out.println("System interrupted");
                                }
                            }
                        };
                        Thread thread  = new Thread(r);
                        thread.start();
                    }
                    manager.saveToFile();
                    break;
                case 2:
                    System.out.println("Enter ID to delete:");
                    int deleteId;
                    try {
                        deleteId = sc.nextInt();
                    } catch(Exception e) {
                        System.out.println("Invalid input. enter a number");
                        sc.nextLine();
                        continue;
                    }
                    manager.deleteTask(deleteId);
                    manager.saveToFile();
                    break;
                case 3:
                    System.out.println("Enter ID to mark done:");
                    int doneId;
                    try {
                        doneId = sc.nextInt();
                    } catch(Exception e) {
                        System.out.println("Invalid input. enter a number");
                        sc.nextLine();
                        continue;
                    }
                    manager.markDone(doneId);
                    manager.saveToFile();
                    break;
                case 4:
                    manager.viewTask();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default :
                    System.out.println("Invalid option..");
            }
        }
    }
}