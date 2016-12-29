package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class Calculator  {
	
	
	public  <T> T  lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
		
		BufferedReader br = null; 

		try {
						
			br = new BufferedReader(new FileReader(filePath));
			T res = initVal;			
			
			String line = null;
			
			while((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}

			return res;

		} catch(IOException ioe) {			

			System.out.println(ioe.getMessage());
			throw ioe;

		} finally {

			if(br != null) {
				try {
					br.close();
				} catch(IOException ioe) {

					System.out.println(ioe.getMessage());
				}
			}
		}

	}
	
	
	

	public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {

		BufferedReader br = null; 

		try {

			br = new BufferedReader(new FileReader(filePath));		

			return callback.doSomethingWithReader(br);

		} catch(IOException ioe) {			

			System.out.println(ioe.getMessage());
			throw ioe;

		} finally {

			if(br != null) {
				try {
					br.close();
				} catch(IOException ioe) {

					System.out.println(ioe.getMessage());
				}
			}
		}
	}
	
	
	
	public Integer calcSum(String filePath) throws IOException {
		
		
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {

			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				// TODO Auto-generated method stub
				return Integer.valueOf(line) + value;
			}	

	
		};
		
		
		return this.lineReadTemplate(filePath, sumCallback, 0);
	}
	
	
	public Integer calcMultiply(String filePath) throws IOException {
		
		
		LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {

			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				// TODO Auto-generated method stub
				return Integer.valueOf(line) * value;
			}		
		};
		
		
		return this.lineReadTemplate(filePath, multiplyCallback, 1);
	}
	
	
	public String concatenate(String filePath) throws IOException {
		LineCallback<String> concatenateCallback = new LineCallback<String>() {

			@Override
			public String doSomethingWithLine(String line, String value) {
				// TODO Auto-generated method stub
				return value + line;
			}
			
		};
		
		return this.lineReadTemplate(filePath, concatenateCallback, "");
		
	}

}
