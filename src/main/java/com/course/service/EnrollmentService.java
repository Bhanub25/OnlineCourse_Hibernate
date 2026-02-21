package com.course.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.course.bean.Course;
import com.course.bean.Enrollment;
import com.course.dao.CourseDAO;
import com.course.dao.EnrollmentDAO;
import com.course.util.ActiveEnrollmentException;
import com.course.util.CourseFullException;
import com.course.util.HibernateUtil;
import com.course.util.ValidationException;

public class EnrollmentService {

    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public Course viewCourseDetails(String courseID) {
        if (courseID == null || courseID.trim().isEmpty()) return null;
        return courseDAO.findCourse(courseID);
    }

    public List<Course> viewAllCourses() {
        return courseDAO.viewAllCourses();
    }

    public boolean addNewCourse(Course course) throws ValidationException {

        if (course == null ||
            course.getCourseID() == null || course.getCourseID().trim().isEmpty() ||
            course.getTitle() == null || course.getTitle().trim().isEmpty() ||
            course.getPrice() < 0 ||
            course.getTotalSeats() <= 0) {
            throw new ValidationException("Invalid course details");
        }

        // initialize fields
        if (course.getAvailableSeats() <= 0) {
            course.setAvailableSeats(course.getTotalSeats());
        }
        if (course.getStartDate() == null) {
            course.setStartDate(new Date());
        }

        if (courseDAO.findCourse(course.getCourseID()) != null) {
            throw new ValidationException("Course ID already exists");
        }

        return courseDAO.insertCourse(course);
    }

    public boolean removeCourse(String courseID)
            throws ValidationException, ActiveEnrollmentException {

        if (courseID == null || courseID.trim().isEmpty()) {
            throw new ValidationException("Course ID cannot be empty");
        }

        int activeCount = enrollmentDAO.getActiveEnrollmentCount(courseID);
        if (activeCount > 0) {
            throw new ActiveEnrollmentException("Active enrollments exist");
        }

        return courseDAO.deleteCourse(courseID);
    }

    public boolean enrollStudent(String courseID,
                                 String studentID,
                                 String studentName,
                                 double paymentAmount,
                                 Date enrollmentDate)
            throws ValidationException, CourseFullException {

        if (courseID == null || courseID.trim().isEmpty() ||
            studentID == null || studentID.trim().isEmpty() ||
            studentName == null || studentName.trim().isEmpty() ||
            paymentAmount < 0 ||
            enrollmentDate == null) {
            throw new ValidationException("Invalid enrollment inputs");
        }

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Course course = session.get(Course.class, courseID);
            if (course == null) {
                tx.rollback();
                return false;
            }

            if (course.getAvailableSeats() <= 0) {
                throw new CourseFullException("No seats available");
            }

            if (paymentAmount < course.getPrice()) {
                throw new ValidationException("Insufficient payment");
            }

            // seat--
            course.setAvailableSeats(course.getAvailableSeats() - 1);
            session.merge(course);

            // insert enrollment
            Enrollment enroll = new Enrollment();
            enroll.setEnrollmentID(enrollmentDAO.generateEnrollmentID());
            enroll.setCourse(course);               
            enroll.setStudentID(studentID);
            enroll.setStudentName(studentName);
            enroll.setAmountPaid(paymentAmount);    
            enroll.setEnrollmentDate(enrollmentDate);
            enroll.setStatus("ACTIVE");

            session.persist(enroll);

            tx.commit();
            return true;

        } catch (ValidationException | CourseFullException e) {
            if (tx != null) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelEnrollment(String enrollmentID, boolean issueRefund)
            throws ValidationException {

        if (enrollmentID == null || enrollmentID.trim().isEmpty()) {
            throw new ValidationException("Invalid enrollment ID");
        }

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Enrollment enroll = session.get(Enrollment.class, enrollmentID);
            if (enroll == null) {
                tx.rollback();
                return false;
            }

            if ("CANCELLED".equalsIgnoreCase(enroll.getStatus())) {
                tx.commit();
                return true;
            }

            // mark cancelled
            enroll.setStatus("CANCELLED");
            session.merge(enroll);

            // seat++ back
            Course course = enroll.getCourse();
            if (course != null) {
                course.setAvailableSeats(course.getAvailableSeats() + 1);
                session.merge(course);
            }

            if (issueRefund) {
                // refund logic placeholder
            }

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}

