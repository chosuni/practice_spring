package springbook.learningtest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Test {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		
		//정렬 문제.
		String[] strArr = {"a", "b", "c" };
		
		List<String> strList = Arrays.asList(strArr);
		Collections.sort(strList, Collections.reverseOrder());
		//Arrays.sort(strArr);
		
		System.out.println(strList);
		
		for(String strArrItem : strArr){
			System.out.println(strArrItem);
		}

		
		//한글 핸들링.
		String nickname = "hello_name한글";
		
		for(int inx = 0, length = nickname.length(); inx < length; inx++){
			if(Character.getType(nickname.charAt(inx)) == 5){
				System.out.println("한글 발견 "+ (inx + 1) + "번째");
				break;
			}
		}

		// 사칙연산
		
		String calStr = "3 + 4 * 10  + 35 / 7 + 80 * 8";
		
		//List로 넣고, *, /가 발견되면, 그 결과값을 재사용하여 계산한다.
		Set<String> exprSet = new HashSet<String>();
		exprSet.add("+");
		exprSet.add("*");
		exprSet.add("-");
		exprSet.add("/");
		
		Stack<String> calStack1 = new Stack<String>();
		Stack<String> calStack2 = new Stack<String>();
		List<String> calList = new ArrayList<String>();
		
		int startInx = 0;
		int endInx = 0;
		
		for(int inx = 0, length = calStr.length(); inx < length; inx++){
			
			String exprStr = Character.toString(calStr.charAt(inx));
			
			//사칙연산자 분리.
			if(exprSet.contains(exprStr)){
								
				//해당 문자열 추출.
				endInx = inx - 1 ;				
				String numStr1 = calStr.substring(startInx, endInx).trim();
				calList.add(numStr1);
				calList.add(exprStr);
				
				//시작 포인트 초기화.
				startInx = inx + 1; 
			}
			
		}
		
		//마지막 숫자 보정.
		String numStr2 = calStr.substring(startInx).trim();
		calList.add(numStr2);
		
		
		System.out.println("expr1: " + calList);
		
		//List를 Stack로 넣으면서 계산(*, /)
		Iterator<String> iter = calList.iterator();
		while(iter.hasNext()){
			String nextStr1 = (String)iter.next();
			if("*".equals(nextStr1) || "/".equals(nextStr1)){
				String prevStr = (String)calStack2.pop();
				String nextStr2 = (String)iter.next();
				double prev = Double.parseDouble(prevStr);
				double next = Double.parseDouble(nextStr2);
				
				double result = 0; 
				
				if("*".equals(nextStr1)){
					result = prev * next;
				} else {
					result = prev / next;
				}
								
				calStack2.push(Double.toString(result));
				
			} else {
				calStack2.push(nextStr1);
			}
		}
				
		
		System.out.println("expr2: " + calStack2);
		
		//하나씩 꺼내면서 계산.
		iter = calStack2.iterator();
		while(iter.hasNext()){
			
			String nextStr1 = (String)iter.next();
			if("+".equals(nextStr1) || "-".equals(nextStr1)){
				String prevStr = (String)calStack1.pop();
				String nextStr2 = (String)iter.next();
				double prev = Double.parseDouble(prevStr);
				double next = Double.parseDouble(nextStr2);
				
				double result = 0; 
				
				if("+".equals(nextStr1)){
					result = prev + next;
				} else {
					result = prev - next;
				}
								
				calStack1.push(Double.toString(result));
				
			} else {
				calStack1.push(nextStr1);
			}			
			
		}
		
		System.out.println("expr3: " + calStack1);
		
		
	}

	
	static class StaticClass {
		
		
	}
	
	class InnerClass {
		
	}
	
	
}
