public class Main {
    public static void main(String[] args) {
        UniqueEventsQueue<Node> queue = new UniqueEventsQueue<>();
        queue.add(new Node(1, 1.0));
        for (int i = 0; i < 1_000_000_00; i++) {
            queue.add(new Node((int) (Math.random() * 1000), Math.random()));
            queue.get();
        }

        System.out.println("QUEUE = " + queue);
    }

    static class Node {
        Integer number;
        Double value;

        Node(Integer number, Double value) {
            this.number = number;
            this.value = value;
        }

        @Override
        public String toString() {
            return number.toString() + ' ' + value.toString();
        }
    }
}
