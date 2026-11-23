package mohamed.pres;

import mohamed.metier.imetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PresSpringAnnotation {
    public static void main(String[] args){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("mohamed");
        imetier metier = applicationContext.getBean(imetier.class);
        System.out.println("Le resultat avec spring Annotation est :"+metier.calcul());
    }
}
