package pl.edu.agh.school.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jdk.jfr.Name;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.SchoolClass;
import pl.edu.agh.school.Teacher;

import javax.inject.Inject;
import javax.inject.Named;

public final class SerializablePersistenceManager implements IPersistenceManager {

    private final Logger log;

    private String teachersStorageFileName;

    private String classStorageFileName;


    public SerializablePersistenceManager() {
        this("teachers.dat", "classes.dat", new Logger());
    }
    @Inject
    public SerializablePersistenceManager(String teachersStorageFileName, String classStorageFileName, Logger log) {
        this.teachersStorageFileName = teachersStorageFileName;
        this.classStorageFileName = classStorageFileName;
        this.log = log;

//        System.out.println(log);
    }

    public void saveTeachers(List<Teacher> teachers) {
        if (teachers == null) {
            throw new IllegalArgumentException();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(teachersStorageFileName))) {
            oos.writeObject(teachers);
            log.log(String.format("Saving teachers %s to file %s.\n", teachers, teachersStorageFileName));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            log.log("There was an error while saving the teachers data", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Teacher> loadTeachers() {
        ArrayList<Teacher> res = null;
        try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(teachersStorageFileName))) {
            res = (ArrayList<Teacher>) ios.readObject();
            log.log(String.format("Loading teachers %s from file %s.\n", res, teachersStorageFileName));
        } catch (FileNotFoundException e) {
            res = new ArrayList<>();
        } catch (IOException e) {
            log.log("There was an error while loading the teachers data", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return res;
    }

    public void saveClasses(List<SchoolClass> classes) {
        if (classes == null) {
            throw new IllegalArgumentException();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(classStorageFileName))) {
            oos.writeObject(classes);
            log.log(String.format("Saving classes %s to file %s.\n", classes, classStorageFileName));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            log.log("There was an error while saving the classes data", e);
        }
    }

    public List<SchoolClass> loadClasses() {
        ArrayList<SchoolClass> res = null;
        try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(classStorageFileName))) {
            res = (ArrayList<SchoolClass>) ios.readObject();
            log.log(String.format("Loading classes %s from file %s.\n", res, classStorageFileName));
        } catch (FileNotFoundException e) {
            res = new ArrayList<>();
        } catch (IOException e) {
            log.log("There was an error while loading the classes data", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return res;
    }

    @Inject
    public void setClassStorageFileName(@Named("classes") String classStorageFileName) {
        this.classStorageFileName = classStorageFileName;
    }

    @Inject
    public void setTeachersStorageFileName(@Named("teachers") String teachersStorageFileName) {
        this.teachersStorageFileName = teachersStorageFileName;
    }
}
