public class Main {

	public static void main(String[] args) {
		Source program = new Source();
		Card card = new Card(program);
		Output output = new Output(program, card);
	}
}
