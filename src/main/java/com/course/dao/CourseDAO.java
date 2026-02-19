package com.course.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.course.bean.Course;
import com.course.util.HibernateUtil;

public class CourseDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    // ✅ Find by PK (no query)
    public Course findCourse(String courseID) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Course.class, courseID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ View all (this is a query - unavoidable)
    public List<Course> viewAllCourses() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Course", Course.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ✅ Insert (no query)
    public boolean insertCourse(Course course) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(course);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Update available seats (NO HQL)
    public boolean updateAvailableSeats(String courseID, int newCount) {
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Course course = session.get(Course.class, courseID); // no query
            if (course == null) {
                tx.rollback();
                return false;
            }

            course.setAvailableSeats(newCount);
            session.merge(course); // automatic update

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Delete (no query)
    public boolean deleteCourse(String courseID) {
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Course course = session.get(Course.class, courseID); // no query
            if (course == null) {
                tx.rollback();
                return false;
            }

            session.remove(course);
            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
