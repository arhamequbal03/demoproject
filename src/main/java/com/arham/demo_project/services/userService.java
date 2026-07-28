package com.arham.demo_project.services;
import com.arham.demo_project.model.member;
import com.arham.demo_project.model.userObject;
import com.arham.demo_project.repositry.memberRepositry;
import com.arham.demo_project.repositry.memberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import java.util.NoSuchElementException;

@Service
public class userService {

    @Autowired
    private memberRepositry repo;

    @Autowired
    private memberValidation mepo;

    public userObject processInfo(String authHeader){
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
        }
        String base64 = authHeader.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(base64));  // "username:password"
        String[] parts = decoded.split(":", 2);

        userObject user=new userObject();
        user.setName(parts[0]);
        user.setPassword(parts[1]);
        return user;
    }

    public userObject userValidation(userObject user){
        String username = user.getName();
        String password = user.getPassword();

        if(username.isEmpty() || !mepo.isMember(username) )
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No username exists");
        else if(password.isEmpty() || !mepo.passwordchecker(username,password))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password Mismatch");;

        if(mepo.setrole(username))
            user.setRole("ADMIN");
        else
            user.setRole("MEMBER");

        return user;
    }

    public void adduser(member user){
        repo.save(user);
    }

    public void editById(Long userId,member info){
        info.setId(userId);
        if(mepo.isMember(info.getUsername())) {
            repo.save(info);
        }else
            throw new NoSuchElementException("User not found");
    }

    public member deleteById(Long userId){
        member m = repo.getById(userId);
        if(repo.existsById(userId)) {
            repo.deleteById(userId);
            return m;
        }else
            throw new NoSuchElementException("User not found");
    }

    public void updateTotalDues(Long userid, int value){
          repo.updateTotalDues(userid,value);
    }

    public long getId(String username){
        return repo.getId(username);
    }
}
