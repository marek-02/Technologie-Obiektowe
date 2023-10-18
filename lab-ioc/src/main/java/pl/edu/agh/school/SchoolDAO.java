package pl.edu.agh.school;

import java.util.Collections;
import java.util.List;

import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.persistence.IPersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolDAO {

    public final Logger log;

    private final List<Teacher> teachers;

    private final List<SchoolClass> classes;

    private final IPersistenceManager manager;

    public SchoolDAO(IPersistenceManager manager, Logger log) {
        this.manager = manager;
        teachers = manager.loadTeachers();
        classes = manager.loadClasses();
        this.log = log;
    }

    public void addTeacher(Teacher teacher) {
        if (!teachers.contains(teacher)) {
            teachers.add(teacher);
            manager.saveTeachers(teachers);
            log.log("Added " + teacher.toString());
        }
    }

    public void addClass(SchoolClass newClass) {
        if (!classes.contains(newClass)) {
            classes.add(newClass);
            manager.saveClasses(classes);
            log.log("Added " + newClass.toString());
        }
    }

    public List<SchoolClass> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    public List<Teacher> getTeachers() {
        return Collections.unmodifiableList(teachers);
    }
}
