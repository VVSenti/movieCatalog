package ru.sentyurin.util.exeption;

public class InconsistentInputException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InconsistentInputException(String message) {
		super(message);
	}

}
