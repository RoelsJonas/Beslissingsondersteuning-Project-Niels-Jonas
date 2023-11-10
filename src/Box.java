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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Box otherBox = (Box) obj;
        return ID.equals(otherBox.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
