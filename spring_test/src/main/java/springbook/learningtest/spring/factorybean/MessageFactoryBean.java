package springbook.learningtest.spring.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {

	String text;
	
	
	//생성에 필요한 값을 설정함.
	public void setText(String text){
		this.text = text;
	}
	
	
	@Override
	public Message getObject() throws Exception {
		// TODO Auto-generated method stub
		return Message.newMessage(this.text);
	}

	
	@Override
	public Class<? extends Message> getObjectType() {
		// TODO Auto-generated method stub
		return Message.class;
	}

	
	/*
	 * getObject() 메소드가 싱글톤인지 여부를 반환한다. 이 팩토리 빈은 매번 호출할 때 마다 새로운 Object를 만들므로 false로 설정한다.
	 * 이것은 팩토리 빈의 동작방식에 대한 설정이고, 만들어진 빈 오브잭트는 싱글톤으로 스프링이 관리해 줄 수 있다.
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	

}
