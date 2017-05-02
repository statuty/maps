package co.example.yuliya.maps.domain;

public class WorkingTime {
    private String from;
    private String to;

    public WorkingTime(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public WorkingTime() {
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
