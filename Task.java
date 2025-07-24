import java.io.Serializable;
import java.util.Date;

//objects of the Task class can be serialized (converted into byte stream)
public class Task implements Serializable{
    private String title;
    private String description;
    private Date dueDate;
    private String priority;
    private String status;

    public Task(String title, String description, Date dueDate, String priority, String status){
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override //generate error if not overridden
    public String toString(){
        return "Task[title="+title+", priority="+priority+", dueDate="+dueDate+", status="+status+"]";
    }

}

