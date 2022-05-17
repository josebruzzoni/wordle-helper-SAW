package Utils;

import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.service.SecurityService;

public class UserFactory {
    public static User userWithName(String username){
        SecurityService ss = new SecurityService();
        byte[] salt = ss.getSalt();
        return new User(username, ss.hash("tangerine", salt), salt);
    }
}
