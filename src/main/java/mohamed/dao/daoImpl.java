package mohamed.dao;
import org.springframework.stereotype.Repository;

@Repository("d")
public class daoImpl implements idao {
    //Par exemple version DataBase
    @Override
    public double getData() {
        System.out.println("Version de base de donnees");
        return 10;
    }
}
