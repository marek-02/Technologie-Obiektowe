package pl.edu.agh.iisg.to.dao;

import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;

import javax.persistence.PersistenceException;

public class GradeDao extends GenericDao<Grade> {

    public boolean gradeStudent(final Student student, final Course course, final float grade) {
        try {
            if(!student.courseSet().contains(course)) return false;
            Grade gradeToSave = new Grade(student, course, grade);

            student.gradeSet().add(gradeToSave);
            course.gradeSet().add(gradeToSave);

            save(gradeToSave);

            return true;
        } catch (PersistenceException e) {
            System.out.println("ehe");
        }

        return false;
    }


}
