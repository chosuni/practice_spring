package springbook.user.service;

import org.springframework.context.annotation.Import;

//메타 에너테이션을 사용한 새로운 에너테이션 정의.
@Import(value=SqlServiceContext.class)
public @interface EnableSqlService {

}
