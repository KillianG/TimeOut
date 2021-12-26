package fr.aryboo2.timeOut;

public enum GameState {

	inGame, reloading, waiting, doubletimeingame, invicible;

	private static GameState currentState;

	public static void setState(GameState gs) {

		GameState.currentState = gs;

	}
	
	public static boolean isState(GameState gs){
		return GameState.currentState == gs;
		
	}

	public static GameState getState() {
		return GameState.currentState;
	}

}
