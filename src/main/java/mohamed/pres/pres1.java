package mohamed.pres;
import mohamed.ext.daoImplV2;
import mohamed.metier.metierImpl;

public class pres1 {
    public static void main(String[] args){
        System.out.println(" ******** Activite-Pratique-1-Injection-Dependances ******** ");
        daoImplV2 a = new daoImplV2();

        // using cunstructeur parametrer
        // metierImpl b = new metierImpl(a);

        // using setter
        metierImpl b = new metierImpl();
        b.setDao(a);

        System.out.println(b.calcul());
    }
}
