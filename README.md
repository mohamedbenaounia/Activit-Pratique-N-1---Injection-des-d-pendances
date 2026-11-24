# Activit-Pratique-N-1---Injection-des-d-pendances
Ce projet Java illustre le couplage faible et l'injection des dépendances à travers une application simple composée d'une couche d'accès aux données (DAO) et d'une couche métier. Il montre l'évolution d'une injection faite à la main vers l'injection automatisée par le framework Spring.

Table des matières
Objectifs
Architecture
Structure du projet
Description des classes et fichiers
Types d'injection des dépendances
Technologies utilisées
Exécution
Auteur
Objectifs
Comprendre la différence entre couplage fort et couplage faible.
Programmer avec des interfaces plutôt qu'avec des classes concrètes.
Réaliser l'injection des dépendances de quatre façons différentes.
Pouvoir changer d'implémentation sans modifier le code métier.
Architecture

L'application est organisée en deux couches. La couche métier ne connaît que l'interface de la couche DAO, jamais ses implémentations.

┌──────────────────┐        ┌──────────────────┐        ┌──────────────────┐
│   Présentation   │ ─────► │      Métier      │ ─────► │       DAO        │
│  (classes main)  │        │  imetier         │        │  idao            │
│                  │        │  metierImpl      │        │  daoImpl         │
│                  │        │                  │        │  daoImplV2       │
└──────────────────┘        └──────────────────┘        └──────────────────┘
                              dépend de l'interface       implémentations
                              idao uniquement             interchangeables

Fonctionnement : metierImpl.calcul() appelle dao.getData(), ajoute 5 au résultat et le retourne.

Structure du projet
Projet/
├── pom.xml
├── README.md
├── config.txt                          (injection dynamique)
└── src/
    └── main/
        ├── java/
        │   └── mohamed/
        │       ├── dao/
        │       │   ├── idao.java           (interface DAO)
        │       │   └── daoImpl.java        (implémentation V1 : base de données)
        │       ├── ext/
        │       │   └── daoImplV2.java      (implémentation V2 : web service)
        │       ├── metier/
        │       │   ├── imetier.java        (interface métier)
        │       │   └── metierImpl.java     (implémentation métier)
        │       └── pres/
        │           ├── Presentation1.java  (injection statique)
        │           ├── Presentation2.java  (injection dynamique)
        │           ├── Presentation3.java  (Spring XML)
        │           └── Presentation4.java  (Spring annotations)
        └── resources/
            └── config.xml                  (configuration Spring XML)

Adaptez cet arbre si les noms de vos classes ou de vos packages sont différents.

Description des classes et fichiers
Package mohamed.dao
Fichier	Rôle
idao	Interface de la couche DAO. Déclare la méthode double getData().
daoImpl	Première implémentation. Simule la lecture de données depuis une base de données. Bean Spring d1.
Package mohamed.ext
Fichier	Rôle
daoImplV2	Deuxième implémentation. Simule la récupération des données via un web service et retourne 20. Bean Spring d2.
Package mohamed.metier
Fichier	Rôle
imetier	Interface de la couche métier. Déclare la méthode double calcul().
metierImpl	Implémentation métier. Contient un attribut de type idao (couplage faible), un constructeur vide, un constructeur paramétré et un setter setDao(). calcul() retourne getData() + 5. Bean Spring metier.
Package mohamed.pres
Fichier	Rôle
Presentation1	Teste l'injection par instanciation statique.
Presentation2	Teste l'injection par instanciation dynamique.
Presentation3	Teste Spring avec la configuration XML.
Presentation4	Teste Spring avec les annotations.
Fichiers de configuration
Fichier	Rôle
config.txt	Contient les noms complets des classes à instancier (une par ligne). Lu par Presentation2.
config.xml	Déclare les beans d et metier et l'injection de d dans metier. Lu par Presentation3.
pom.xml	Fichier Maven : dépendances du projet (Spring).
Types d'injection des dépendances
1. Instanciation statique

Les objets sont créés avec le mot-clé new directement dans le code, puis reliés avec le setter ou le constructeur.

Avantage : simple à comprendre.
Inconvénient : pour changer d'implémentation, il faut modifier le code et recompiler.
2. Instanciation dynamique

Les noms des classes sont lus dans config.txt. Les objets sont créés par réflexion (Class.forName, newInstance) et la dépendance est injectée en appelant setDao par réflexion.

Avantage : on change d'implémentation en modifiant seulement config.txt, sans recompilation.
Inconvénient : le code de réflexion est lourd à écrire.

Exemple de config.txt :

mohamed.ext.daoImplV2
mohamed.metier.metierImpl
3. Spring : version XML

Les beans et leurs dépendances sont déclarés dans config.xml. Spring crée les objets et appelle le setter setDao() grâce à la balise <property>.

xml
<bean id="d" class="mohamed.ext.daoImplV2"/>
<bean id="metier" class="mohamed.metier.metierImpl">
    <property name="dao" ref="d"/>
</bean>
4. Spring : version annotations

La configuration se fait directement dans le code avec des annotations :

Annotation	Utilisation
@Repository("d2")	Déclare daoImplV2 comme bean DAO nommé d2.
@Service("metier")	Déclare metierImpl comme bean métier nommé metier.
@Autowired	Demande à Spring d'injecter la dépendance.
@Qualifier("d2")	Choisit l'implémentation à injecter quand il y en a plusieurs.
Comparaison
Approche	Pour changer d'implémentation	Configuration
Statique	Modifier le code et recompiler	Dans le code
Dynamique	Modifier config.txt	Fichier texte
Spring XML	Modifier config.xml	Fichier XML
Spring annotations	Changer @Qualifier	Dans le code
Technologies utilisées
Java
Spring Framework (spring-context)
Maven
IntelliJ IDEA
Git et GitHub
Exécution
Cloner le dépôt :
git clone <lien-du-repository>
Ouvrir le projet dans IntelliJ IDEA et laisser Maven télécharger les dépendances.
Lancer la méthode main de la classe Presentation1, Presentation2, Presentation3 ou Presentation4.

Résultat attendu avec daoImplV2 :

Version de web service
Résultat = 25.0
