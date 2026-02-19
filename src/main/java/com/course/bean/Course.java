package com.course.bean;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "COURSE_TBL")
public class Course {

    @Id
    @Column(name = "COURSE_ID")
    private String courseID;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "PRICE")
    private double price;

    @Column(name = "TOTAL_SEATS")
    private int totalSeats;

    @Column(name = "AVAILABLE_SEATS")
    private int availableSeats;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    private Date startDate;

    // 🔴 Mandatory default constructor for Hibernate
    public Course() {}

    public String getCourseID() {
        return courseID;
    }
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
