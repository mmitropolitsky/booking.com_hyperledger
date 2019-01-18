package org.tudelft.blockchain.booking.otawebapp.repository.hyperledger;

import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.user.IdemixUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.IdentityHashMap;
import java.util.Map;

@Component
public class UserRepository {

    private Map<String, User> users;
    private Map<String, IdemixUser> idemixUsers;

    @PostConstruct
    public void setUp() throws Exception {
        users = new IdentityHashMap<>();
        idemixUsers = new IdentityHashMap<>();
    }

    /**
     * Get an existing user
     * @param username
     * @return the corresponding User object or null if not enrolled
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Get an existing Idemix user
     * @param username
     * @return the corresponding IdemixUser object or null if not enrolled
     */
    public IdemixUser getIdemixUser(String username) {
        return idemixUsers.get(username);
    }

    public void putUser(User user) {
        users.put(user.getName(), user);
    }

    public void putIdemixUser(IdemixUser user) {
        idemixUsers.put(user.getName(), user);
    }

}
