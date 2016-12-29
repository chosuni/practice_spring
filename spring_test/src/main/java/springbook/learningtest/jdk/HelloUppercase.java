package springbook.learningtest.jdk;

public class HelloUppercase implements Hello {
	
	Hello hello;      //위임할 Target Object: 다른 Proxy를 추가할 수 있으므로 Interface로 지정함.
	
	public HelloUppercase(Hello hello){
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return hello.sayHello(name).toUpperCase();   // 위임과 부가기능 적용.
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return hello.sayThankYou(name).toUpperCase();
	}

}
