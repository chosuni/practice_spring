package springbook.learningtest.spring.ioc.bean;


public class ConsolePrinter implements Printer {

	@Override
	public void print(String message) {
		// TODO Auto-generated method stub
		System.out.println(message);

	}

}
