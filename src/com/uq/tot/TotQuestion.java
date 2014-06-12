package com.uq.tot;

import java.io.File;

public class TotQuestion {

    private File picOne;
	private File picTwo;
	private String txtQuestion;
	private String sender;
	
	public TotQuestion(File picOne, File picTwo, String txtQuestion,
			String sender) {
		super();
		this.picOne = picOne;
		this.picTwo = picTwo;
		this.txtQuestion = txtQuestion;
		this.sender = sender;
	}
	
	
}
