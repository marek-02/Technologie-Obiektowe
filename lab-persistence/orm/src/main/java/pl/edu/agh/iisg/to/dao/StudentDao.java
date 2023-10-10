package pl.edu.agh.iisg.to.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;

import javax.persistence.PersistenceException;

public class StudentDao extends GenericDao<Student> {

    public Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        try {
            Student student = save(new Student(firstName, lastName, indexNumber));
            return Optional.of(student);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Student> findByIndexNumber(final int indexNumber) {
        try {
            return currentSession().createQuery("SELECT s FROM Student s WHERE s.indexNumber = :indexNumber", Student.class)
                    .setParameter("indexNumber", indexNumber).uniqueResultOptional();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Map<Course, Float> createReport(final Student student) {
        Map<Course, Float> result = student.courseSet().stream().collect(Collectors.toMap(
                Function.identity(),
                c -> (float) c.gradeSet().stream().filter(g -> g.student() == student).mapToDouble(Grade::grade).average().orElse(0.0)));


        return result;
    }
}
