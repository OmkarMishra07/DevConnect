package com.example.MergeX;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MergeXApplicationTests {

	@Test
	void contextLoads() {
		String key = Encoders.BASE64.encode(
				Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512).getEncoded()
		);
		System.out.println("Key "+key);
	}

}
