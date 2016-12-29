package springbook.learningtest;

import java.io.IOException;
import java.io.InputStream;

import springbook.learningtest.Test.StaticClass;

public class OuterClass {
	
	
	StaticClass tc = new StaticClass();

	Test testClass = new Test();
	
	Test.InnerClass tic = testClass.new InnerClass();
	
	
	Object anonyObj = new InputStream(){
		
		
		public void print(){
			
			System.out.println("keke");
		}

		@Override
		public int read() throws IOException {
			// TODO Auto-generated method stub
			return 0;
		}
	};

	
}
