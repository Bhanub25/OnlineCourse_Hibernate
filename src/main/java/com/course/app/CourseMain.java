package com.course.app;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.course.bean.Course;
import com.course.service.EnrollmentService;
import com.course.util.ActiveEnrollmentException;
import com.course.util.CourseFullException;
import com.course.util.HibernateUtil;
import com.course.util.ValidationException;

public class CourseMain {

    private static final EnrollmentService enrollmentService = new EnrollmentService();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n Online Learning Platform Console");
            System.out.println("1. Add New Course");
            System.out.println("2. View Course Details");
            System.out.println("3. View All Courses");
            System.out.println("4. Subscribe / Enroll");
            System.out.println("5. Cancel Enrollment");
            System.out.println("6. Remove Course");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    try {
                        System.out.print("Enter Course ID: ");
                        String courseID = sc.nextLine();

                        System.out.print("Enter Title: ");
                        String title = sc.nextLine();

                        System.out.print("Enter Price: ");
                        double price = sc.nextDouble();

                        System.out.print("Enter Total Seats: ");
                        int totalSeats = sc.nextInt();
                        sc.nextLine();

                        Course course = new Course();
                        course.setCourseID(courseID);
                        course.setTitle(title);
                        course.setPrice(price);
                        course.setTotalSeats(totalSeats);
                        course.setAvailableSeats(totalSeats);
                        course.setStartDate(new Date());

                        boolean result = enrollmentService.addNewCourse(course);
                        System.out.println(result ? "Course added successfully" : "Failed to add course");

                    } catch (ValidationException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Enter Course ID: ");
                    String cid = sc.nextLine();

                    Course c = enrollmentService.viewCourseDetails(cid);
                    if (c != null) {
                        System.out.println("\nCourse Details:");
                        System.out.println("CourseID: " + c.getCourseID());
                        System.out.println("Title: " + c.getTitle());
                        System.out.println("Price: " + c.getPrice());
                        System.out.println("Total Seats: " + c.getTotalSeats());
                        System.out.println("Available Seats: " + c.getAvailableSeats());
                        System.out.println("Start Date: " + c.getStartDate());
                    } else {
                        System.out.println("Course not found!");
                    }
                    break;

                case 3:
                    List<Course> list = enrollmentService.viewAllCourses();
                    System.out.println("\nAll Courses:");
                    for (Course course : list) {
                        System.out.println(
                            "CourseID: " + course.getCourseID() +
                            " | Title: " + course.getTitle() +
                            " | Price: " + course.getPrice() +
                            " | Total Seats: " + course.getTotalSeats() +
                            " | Available Seats: " + course.getAvailableSeats() +
                            " | Start Date: " + course.getStartDate()
                        );
                    }
                    break;

                case 4:
                    try {
                        System.out.print("Enter Course ID: ");
                        String courseID = sc.nextLine();

                        System.out.print("Enter Student ID: ");
                        String studentID = sc.nextLine();

                        System.out.print("Enter Student Name: ");
                        String studentName = sc.nextLine();

                        System.out.print("Enter Payment Amount: ");
                        double paymentAmount = sc.nextDouble();
                        sc.nextLine();

                        boolean r = enrollmentService.enrollStudent(
                                courseID, studentID, studentName, paymentAmount, new Date()
                        );
                        System.out.println(r ? "Result: Enrollment successful" : "Result: Enrollment failed");

                    } catch (ValidationException | CourseFullException e) {
                        System.out.println("Result: " + e.getMessage());
                    }
                    break;

                case 5:
                    try {
                        System.out.print("Enter Enrollment ID to cancel: ");
                        String enrollmentID = sc.nextLine();

                        System.out.print("Issue refund? (yes/no): ");
                        String refund = sc.nextLine();
                        boolean issueRefund = refund.equalsIgnoreCase("yes");

                        boolean r = enrollmentService.cancelEnrollment(enrollmentID, issueRefund);
                        System.out.println(r ? "Result: Cancelled successfully" : "Result: Failed");

                    } catch (ValidationException e) {
                        System.out.println("Result: " + e.getMessage());
                    }
                    break;

                case 6:
                    try {
                        System.out.print("Enter Course ID to remove: ");
                        String removeID = sc.nextLine();

                        boolean r = enrollmentService.removeCourse(removeID);
                        System.out.println(r ? "Result: Course removed successfully"
                                : "Result: Cannot remove course (active enrollments?)");

                    } catch (ValidationException | ActiveEnrollmentException e) {
                        System.out.println("Result: " + e.getMessage());
                    }
                    break;

                case 0:
                    System.out.println("Exiting platform.");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }

        sc.close();
        HibernateUtil.getSessionFactory().close(); // ✅ close hibernate
    }
}
