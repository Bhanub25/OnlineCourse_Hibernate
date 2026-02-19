package com.course.bean;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ENROLLMENT_TBL")
public class Enrollment {

    @Id
    @Column(name = "ENROLLMENT_ID")
    private String enrollmentID;

    // 🔴 Relationship Mapping
    @ManyToOne
    @JoinColumn(name = "COURSE_ID")
    private Course course;

    @Column(name = "STUDENT_ID")
    private String studentID;

    @Column(name = "STUDENT_NAME")
    private String studentName;

    @Column(name = "AMOUNT_PAID")
    private double amountPaid;

    @Temporal(TemporalType.DATE)
    @Column(name = "ENROLLMENT_DATE")
    private Date enrollmentDate;

    @Column(name = "STATUS")
    private String status;

    public Enrollment() {}

    public String getEnrollmentID() {
        return enrollmentID;
    }
    public void setEnrollmentID(String enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }

    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getAmountPaid() {
        return amountPaid;
    }
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }
    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
