package engine.main;

public class Timer {

	public static int ticks = 0;
	
	public static int getTimeSec(){
		return ticks / 60;
	}
	
	public static int getTicks(){
		return ticks;
	}
	
}
