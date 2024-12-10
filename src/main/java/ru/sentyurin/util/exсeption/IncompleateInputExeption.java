package ru.sentyurin.util.exсeption;

public class IncompleateInputExeption extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncompleateInputExeption(String message) {
		super(message);
	}
	
}
