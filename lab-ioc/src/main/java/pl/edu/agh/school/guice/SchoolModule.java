package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import pl.edu.agh.logger.ConsoleMessageSerializer;
import pl.edu.agh.logger.FileMessageSerializer;
import pl.edu.agh.logger.IMessageSerializer;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.persistence.IPersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends AbstractModule {
    @Override
    public void configure() {
        bind(IPersistenceManager.class).to(SerializablePersistenceManager.class);

        bind(String.class).annotatedWith(Names.named("classes")).toInstance("classesNamed.dat");
        bind(String.class).annotatedWith(Names.named("teachers")).toInstance("teachersNamed.dat");
//        bind(SerializablePersistenceManager.class).toInstance(new SerializablePersistenceManager("te.dat", "cl.dat", new Logger()));
//        żeby działało wywalić @Injecty i Namedy z setterów

        Multibinder<IMessageSerializer> messageBinder = Multibinder.newSetBinder(binder(), IMessageSerializer.class);
        messageBinder.addBinding().toInstance(new FileMessageSerializer("persistence.log"));
        messageBinder.addBinding().to(ConsoleMessageSerializer.class);
    }
}
