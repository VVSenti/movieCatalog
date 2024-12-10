package ru.sentyurin.util.exсeption;

public class NoDataInRepository extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoDataInRepository(String message) {
		super(message);
	}
}
