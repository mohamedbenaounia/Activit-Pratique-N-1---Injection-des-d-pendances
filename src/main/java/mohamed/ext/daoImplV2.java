package mohamed.ext;
import mohamed.dao.idao;
import org.springframework.stereotype.Repository;

@Repository("d2")
public class daoImplV2 implements idao {
    //Par exemple version WebUI
    @Override
    public double getData() {
        System.out.println("Version de web service");
        return 20;
    }
}