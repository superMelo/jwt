package com.qyf.jwt;

import com.qyf.jwt.cache.RedisCache;
import com.qyf.jwt.cache.TokenCache;
import com.qyf.jwt.cache.UserCache;
import com.qyf.jwt.entity.Token;
import com.qyf.jwt.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Slf4j
class JwtApplicationTests {

	@Autowired
	TokenCache tokenCache;

	@Autowired
	UserCache userCache;

	@Test
	void contextLoads() {
	}

	@Test
	void pushList(){
		String id = UUID.randomUUID().toString();
		Token token = Token.builder().id(id)
				.userId("1").token(id + "_1").expTime(new Date()).ip("127.0.0.1").build();
		tokenCache.pushList("list", token);
		token = tokenCache.popList("list");
		log.info("token:{}", token);
	}

//	@Test
//	void popList(){
//		Token token = redisCache.popList("list");
//		log.info("token:{}", token);
//	}

	@Test
	void loadData(){
		User user = User.builder().id(UUID.randomUUID().toString())
				.username("qyf").password("123").build();
		userCache.pushList("user", user);
		List<User> users = userCache.findAll("user");
		log.info("users:{}", users);
	}
}
