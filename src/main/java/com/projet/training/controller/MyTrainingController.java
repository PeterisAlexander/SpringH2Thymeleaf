package com.projet.training.controller;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projet.training.entities.LoginEntity;
import com.projet.training.repository.LoginRepository;
import com.projet.training.services.LoginService;

@RestController
@RequestMapping("/api")
public class MyTrainingController {
	
	@Autowired
	private LoginRepository lr;
	
	@Autowired
	private LoginService ls;
	
	@PostMapping(value="/login")
	@CrossOrigin(origins="http://localhost:4200")
	public LoginEntity login(@RequestBody LoginEntity login) throws Exception {
		String username = login.getUsername();
		
		String password = login.getPassword();
		
		if((username == null && password == null) || (username == "" && password == "")) {
			throw new Exception("Username and Password are empty");
		}
		
		LoginEntity loginObj = ls.fetchLoginByUsernameAndPassword(username, password);
		if(loginObj == null) {
			throw new Exception("Bad credentials");
		}
		
		return loginObj;
	}
	
	@GetMapping("/users")
	@CrossOrigin(origins="http://localhost:4200")
	public Iterable<LoginEntity> listUsers() {
	    return lr.findAll();
	}
	
	@GetMapping(value = "/login/{id}", produces = "application/json")
	@CrossOrigin(origins="http://localhost:4200")
    public ResponseEntity<LoginEntity> get(@PathVariable int id) {
        try{
        	LoginEntity l = ls.findUser(id);
            return ResponseEntity.ok(l);
        }catch ( Exception e ){
            return ResponseEntity.notFound().build();
        }
    }
	
	@PostMapping(value="/createUser")
	@CrossOrigin(origins="http://localhost:4200")
	public LoginEntity loginOrRegister(@RequestBody LoginEntity login) throws Exception {
		
		if(login.getUsername() == null || login.getUsername() == "") {
			throw new Exception("User " + login.getUsername() +" is already exist");
		}
		
		LoginEntity loginObj = ls.fetchLoginByUsername(login.getUsername());
		
		loginObj = ls.saveLogin(login);
		
		if(loginObj == null) {
			throw new Exception("Bad credentials");		
		}
		
		return loginObj;
	}
	
	@GetMapping("/generator")
	@CrossOrigin(origins="http://localhost:4200")
	public ResponseEntity<ArrayList<LoginEntity>> randomPerson(@RequestParam("nbPerson") Integer nbPerson) {
		
		ArrayList<LoginEntity> loginList = new ArrayList<>();	
		
		LoginEntity login = new LoginEntity();
		
		for(int i = 0; i< nbPerson; i++) {
			String usernameAndPassword = randomString();
			String firstname = randomString();
			String lastname = randomString();
			LocalDate birthdate = randomDate();

			login.setId(i);
			login.setUsername(usernameAndPassword);
			login.setPassword(usernameAndPassword);
			login.setLastname(lastname);
			login.setFirstname(firstname);
			login.setBirthdate(randomDate());
			
			
			loginList.add(ls.saveLogin(new LoginEntity(
					i,
					usernameAndPassword.toLowerCase(),
					usernameAndPassword.toLowerCase(),
					lastname.toUpperCase(),
					firstname.toLowerCase(),
					birthdate
				))
			);
			
		}
		
		return ResponseEntity.ok().body(loginList);
	}

	/**
	 * @return 
	 */
	@GetMapping("/randomString")
	private String randomString() {
		String abcd = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String randomString = "";
		
		Random rand = new Random();
		
		int low = 3;
		int high = 15;
		int size = rand.nextInt(high-low) + low;
		
		char[] text = new char[size];
		
		for(int i = 0; i < size; i++) {
			text[i] = abcd.charAt(rand.nextInt(abcd.length()));
		}
		
		for(int i = 0; i < text.length; i++) {
			randomString += text[i];
		}
		
		return randomString;
	}
	
	private LocalDate randomDate() {
		long start = LocalDate.of(1970, 1, 1).toEpochDay();
	    long end = LocalDate.of(2000, 1, 1).toEpochDay();
	    long randomDay = ThreadLocalRandom.current().nextLong(start, end);
	    LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
	    
	    return randomDate;
	}

}
