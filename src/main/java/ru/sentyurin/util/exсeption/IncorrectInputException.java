package ru.sentyurin.util.exсeption;

public class IncorrectInputException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IncorrectInputException(String message) {
		super(message);
	}

}
