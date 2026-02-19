package com.course.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.course.bean.Course;
import com.course.bean.Enrollment;
import com.course.util.HibernateUtil;

public class EnrollmentDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    // ✅ Only for custom ID format ENR0001...
    public String generateEnrollmentID() {
        try (Session session = sessionFactory.openSession()) {

            String maxId = session.createQuery(
                    "select max(e.enrollmentID) from Enrollment e", String.class
            ).uniqueResult();

            int next = 1;
            if (maxId != null) {
                next = Integer.parseInt(maxId.substring(3)) + 1;
            }
            return String.format("ENR%04d", next);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ No query: just persist
    public boolean recordEnrollment(Enrollment enroll, String courseID) {
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Course course = session.get(Course.class, courseID); // no query
            if (course == null) {
                tx.rollback();
                return false;
            }

            if (enroll.getEnrollmentID() == null || enroll.getEnrollmentID().isBlank()) {
                enroll.setEnrollmentID(generateEnrollmentID());
            }

            enroll.setCourse(course);
            if (enroll.getEnrollmentDate() == null) {
                enroll.setEnrollmentDate(new Date());
            }

            session.persist(enroll); // ✅ automatic insert

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // ✅ No HQL update: load object + change + merge
    public boolean cancelEnrollment(String enrollmentID) {
        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Enrollment enroll = session.get(Enrollment.class, enrollmentID); // no query
            if (enroll == null) {
                tx.rollback();
                return false;
            }

            enroll.setStatus("CANCELLED");
            session.merge(enroll); // ✅ automatic update

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // ✅ No HQL needed: just get enrollment by id
    public Enrollment findEnrollment(String enrollmentID) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Enrollment.class, enrollmentID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Enrollment history - without HQL you can use Criteria API,
    // but simplest is HQL (recommended). If you want Criteria, tell me.
    public List<Enrollment> getEnrollmentHistory(String studentID) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Enrollment e where e.studentID = :sid order by e.enrollmentDate desc",
                    Enrollment.class
            ).setParameter("sid", studentID).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ✅ Active count - simplest with HQL
    public int getActiveEnrollmentCount(String courseID) {
        try (Session session = sessionFactory.openSession()) {

            Long count = session.createQuery(
                    "select count(e) from Enrollment e where e.course.courseID = :cid and e.status = 'ACTIVE'",
                    Long.class
            ).setParameter("cid", courseID).uniqueResult();

            return (count == null) ? 0 : count.intValue();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
