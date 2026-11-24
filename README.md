# Activit-Pratique-N-1---Injection-des-d-pendances
Compte rendu : Inversion de contrôle et injection des dépendances

Nom et prénom : Mohamed Benaounia
Module : Architecture JEE
Année universitaire : 2026-2027

Objectif

Mettre en pratique le couplage faible et l'injection des dépendances en Java, selon trois approches :

Instanciation statique
Instanciation dynamique (réflexion)
Framework Spring (version XML puis version annotations)

Ressource suivie : https://www.youtube.com/watch?v=vOLqabN-n2k

Structure du projet
src/main/java
└── mohamed
    ├── dao
    │   ├── idao.java            (interface)
    │   └── daoImpl.java         (implémentation V1)
    ├── ext
    │   └── daoImplV2.java       (implémentation V2, version web service)
    ├── metier
    │   ├── imetier.java         (interface)
    │   └── metierImpl.java      (implémentation avec couplage faible)
    └── pres
        ├── Presentation1.java   (injection statique)
        ├── Presentation2.java   (injection dynamique)
        ├── Presentation3.java   (Spring XML)
        └── Presentation4.java   (Spring annotations)
src/main/resources
├── config.txt                   (utilisé par l'injection dynamique)
└── config.xml                   (utilisé par Spring XML)
Partie 1
1. Interface IDao

L'interface déclare une seule méthode, getData, qui fournit la donnée.

java
package mohamed.dao;

public interface idao {
    double getData();
}
2. Implémentation de IDao

Première version (accès à une base de données) :

java
package mohamed.dao;

import org.springframework.stereotype.Repository;

@Repository("d1")
public class daoImpl implements idao {
    @Override
    public double getData() {
        System.out.println("Version base de données");
        return 10;
    }
}

Deuxième version (web service), placée dans le package mohamed.ext pour montrer qu'on peut changer d'implémentation sans modifier le métier :

java
package mohamed.ext;

import mohamed.dao.idao;
import org.springframework.stereotype.Repository;

@Repository("d2")
public class daoImplV2 implements idao {
    @Override
    public double getData() {
        System.out.println("Version de web service");
        return 20;
    }
}
3. Interface IMetier
java
package mohamed.metier;

public interface imetier {
    double calcul();
}
4. Implémentation de IMetier avec couplage faible

La classe métier ne dépend pas d'une implémentation concrète de idao, mais uniquement de l'interface. La dépendance lui est fournie de l'extérieur (constructeur ou setter).

java
package mohamed.metier;

import mohamed.dao.idao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("metier")
public class metierImpl implements imetier {
    @Autowired
    @Qualifier("d2")
    private idao dao;

    public metierImpl() {}

    public metierImpl(idao dao) { // constructeur paramétré
        this.dao = dao;
    }

    public void setDao(idao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double a = dao.getData();
        return a + 5;
    }
}

Le couplage est faible car pour changer de source de données, il suffit de fournir une autre implémentation de idao. Le code de metierImpl ne change pas.

5. Injection des dépendances
a. Par instanciation statique

Les objets sont créés avec new dans le code. Changer d'implémentation oblige à modifier et recompiler le code.

java
package mohamed.pres;

import mohamed.ext.daoImplV2;
import mohamed.metier.metierImpl;

public class Presentation1 {
    public static void main(String[] args) {
        daoImplV2 dao = new daoImplV2();
        metierImpl metier = new metierImpl();
        metier.setDao(dao); // injection via le setter
        // ou : new metierImpl(dao) via le constructeur
        System.out.println("Résultat = " + metier.calcul());
    }
}

Résultat attendu :

Version de web service
Résultat = 25.0
b. Par instanciation dynamique

Les noms des classes sont lus dans un fichier config.txt. Les objets sont créés par réflexion, sans recompilation.

config.txt :

mohamed.ext.daoImplV2
mohamed.metier.metierImpl
java
package mohamed.pres;

import mohamed.dao.idao;
import mohamed.metier.imetier;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Scanner;

public class Presentation2 {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File("config.txt"));
        String daoClassName = scanner.nextLine();
        Class<?> cDao = Class.forName(daoClassName);
        idao dao = (idao) cDao.getDeclaredConstructor().newInstance();

        String metierClassName = scanner.nextLine();
        Class<?> cMetier = Class.forName(metierClassName);
        imetier metier = (imetier) cMetier.getDeclaredConstructor().newInstance();

        Method setDao = cMetier.getMethod("setDao", idao.class);
        setDao.invoke(metier, dao); // injection par réflexion

        System.out.println("Résultat = " + metier.calcul());
    }
}

Pour passer de la V2 à la V1, il suffit de modifier la première ligne de config.txt.

c. Avec le framework Spring
Version XML

config.xml :

xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="d" class="mohamed.ext.daoImplV2"/>
    <bean id="metier" class="mohamed.metier.metierImpl">
        <property name="dao" ref="d"/>
    </bean>
</beans>
java
package mohamed.pres;

import mohamed.metier.imetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Presentation3 {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        imetier metier = (imetier) context.getBean("metier");
        System.out.println("Résultat = " + metier.calcul());
    }
}

La balise <property name="dao" ref="d"/> appelle le setter setDao pour injecter le bean d.

Version annotations

Les annotations remplacent le fichier XML :

@Repository("d2") sur daoImplV2 déclare le bean DAO.
@Service("metier") sur metierImpl déclare le bean métier.
@Autowired demande l'injection de la dépendance.
@Qualifier("d2") choisit l'implémentation à injecter lorsqu'il y en a plusieurs.
java
package mohamed.pres;

import mohamed.metier.imetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Presentation4 {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext("mohamed.ext", "mohamed.dao", "mohamed.metier");
        imetier metier = context.getBean(imetier.class);
        System.out.println("Résultat = " + metier.calcul());
    }
}
Dépendances Maven
xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.1.x</version>
</dependency>
Comparaison des approches
Approche	Changer d'implémentation	Avantage	Inconvénient
Statique	Modifier le code et recompiler	Simple	Couplage fort dans la classe de présentation
Dynamique	Modifier config.txt	Pas de recompilation	Code de réflexion à écrire
Spring XML	Modifier config.xml	Configuration externe, code propre	Fichier XML verbeux
Spring annotations	Changer le @Qualifier	Concis	Configuration dans le code
Conclusion

Le couplage faible, obtenu grâce aux interfaces, rend l'application ouverte à l'extension et fermée à la modification. L'injection des dépendances, d'abord réalisée à la main (statique, dynamique), est automatisée par Spring, qui gère la création et la liaison des objets.

Historique de la séance

Un commit et un push ont été effectués environ toutes les 30 minutes :

Commit	Contenu
1	Création du projet, interfaces idao et imetier
2	Implémentations daoImpl, daoImplV2 et metierImpl
3	Injection statique et dynamique
4	Spring version XML
5	Spring version annotations et README
