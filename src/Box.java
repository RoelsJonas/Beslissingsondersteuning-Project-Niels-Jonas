import java.util.Objects;

public class Box {
    private String ID;

    public Box(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Box{");
        sb.append("id=").append(ID);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return ID == box.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
