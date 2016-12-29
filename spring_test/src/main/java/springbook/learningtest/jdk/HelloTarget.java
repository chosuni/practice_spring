package springbook.learningtest.jdk;

public class HelloTarget implements Hello {

	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return "Hello " + name;
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return "Hi " + name;
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return "Thank You " + name;
	}

}
