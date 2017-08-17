package propulsar.qroo.DomainLayer.Objects;

/**
 * Created by maubocanegra on 16/03/17.
 */

public class Event {

    public Event(){}

    public int ID;
    public int Category;
    public String Title;
    public String Description;
    public String Url;
    public String ImageUrl;
    public String StartTime;
    public String EndTime;
    public String Place;
    public String InsDate;

    public String getInsDate() {
        return InsDate;
    }

    public void setInsDate(String insDate) {
        InsDate = insDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCategory() {
        return Category;
    }

    public void setCategory(int category) {
        Category = category;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }
}
