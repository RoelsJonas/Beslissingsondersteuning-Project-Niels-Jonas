public class Box {
    private int id;

    public Box(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Box{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
