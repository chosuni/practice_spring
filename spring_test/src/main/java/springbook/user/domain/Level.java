package springbook.user.domain;

public enum Level {
	
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER); 
		
	private final int value;
	
	private final Level next;   //다음 단계로 업그레이드 할 레벨 정보를 저장함.
	
	
	Level(int value, Level next){
		this.value = value;
		this.next = next;
	}

	public int intValue(){
		return value;
	}
	
	public Level nextLevel(){
		return this.next;
	}
	
	
	public static Level valueOf(int value){
		switch(value) {
		case 1:
			return BASIC;
		case 2:
			return SILVER;
		case 3:
			return GOLD;
		default:
			throw new AssertionError("Unknown value: " + value);
		}
	}
	
	

	
}
