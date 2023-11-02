import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return id == box.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
