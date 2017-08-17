package propulsar.qroo.DomainLayer.Objects;

/**
 * Created by maubocanegra on 10/04/17.
 */

public class StateCase {

    String complaintID;
    String complaintStatusID;
    String description;
    String color;
    String date;

    public StateCase(){}

    public String getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
    }

    public String getComplaintStatusID() {
        return complaintStatusID;
    }

    public void setComplaintStatusID(String complaintStatusID) {
        this.complaintStatusID = complaintStatusID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
