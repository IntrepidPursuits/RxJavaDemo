package intrepid.io.rxjavademo.events;

public class NumberGeneratedEvent {
    private int number;

    public NumberGeneratedEvent(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
