package com.arham.demo_project.Services;
import com.arham.demo_project.Model.Member;
import com.arham.demo_project.Model.UserObject;
import com.arham.demo_project.Repositry.MemberRepository;
import com.arham.demo_project.Repositry.MemberValidation;
import com.arham.demo_project.Repositry.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private MemberRepository repo;

    @Autowired
    private MemberValidation mepo;

    @Autowired
    private ReportRepository reportRepo;

    @Autowired
    private PasswordManager passwordManager;

    public UserObject processInfo(String authHeader){
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
        }
        String base64 = authHeader.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(base64));  // "username:password"
        String[] parts = decoded.split(":", 2);
        if(parts.length != 2)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials format");

        UserObject user=new UserObject();
        user.setName(parts[0]);
        user.setPassword(parts[1]);
        return user;
    }

    public UserObject userValidation(UserObject user){
        String username = user.getName();
        String password = user.getPassword();

        if(username.isEmpty() || !mepo.isMember(username) )
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No username exists");
        else if(password.isEmpty() || !passwordManager.matches(password, mepo.getPassword(username)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password Mismatch");

        if(mepo.setrole(username))
            user.setRole("ADMIN");
        else
            user.setRole("MEMBER");

        return user;
    }

    public void adduser(Member user){
        if(!mepo.isMember(user.getUsername())){
            user.setPassword(passwordManager.hash(user.getPassword()));
            repo.save(user);
        }
        else
            throw new IllegalArgumentException("User already exists");
    }

    public Member editById(Long userId, Member info){
        Member existing = repo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if(!existing.getUsername().equals(info.getUsername()))
            throw new NoSuchElementException("User name mismatch");
        if(info.getTotal_dues() < 0)
            throw new IllegalArgumentException("Total dues can't be less than zero");
        String role = info.getRole();
        if(role==null) role=existing.getRole();
        if((!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("MEMBER")))
            throw new IllegalArgumentException("Role must be ADMIN or MEMBER");
        if(info.getPassword()!=null && !info.getPassword().isEmpty())
            existing.setPassword(passwordManager.hash(info.getPassword()));
        existing.setName(info.getName());
        existing.setRole(role.toUpperCase());
        existing.setTotal_dues(info.getTotal_dues());
        repo.save(existing);
        return existing;
    }

    public Member deleteById(Long userId){
        Member m = repo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if(m.getTotal_dues() > 0)
            throw new IllegalArgumentException("User can't be deleted, total dues are pending");
        if(reportRepo.hasOpenBooks(userId))
            throw new IllegalArgumentException("User can't be deleted, books are still issued");
        repo.deleteById(userId);
        return m;
    }

    public void updateTotalDues(Long userid, int value){
          repo.updateTotalDues(userid,value);
    }

    public long getId(String username){
        return repo.getId(username);
    }

    public boolean validuser(Long id){
        if(!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid user");
        return repo.existsById(id);
    }
}
