package springbook.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.User;

@Transactional  // <tx:method name="*" />와 동일.
public interface UserService {

	void add(User user);
	void deleteAll();
	void update(User user);	
	void upgradeLevels();
	
	
	@Transactional(readOnly = true)
	User get(String id);
	
	@Transactional(readOnly = true)
	List<User> getAll();
	
}
