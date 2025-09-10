package contactmanager;

public class Contact {
    private String name;
    private String phone;
    private String email;
    private boolean favorite;

    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.favorite = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String toCSV() {
        return String.format("%s,%s,%s,%s", name, phone, email, favorite);
    }

    @Override
    public String toString() {
        return String.format("Name: %s | Phone: %s | Email: %s | Favorite: %s",
                name, phone, email, favorite ? "Yes" : "No");
    }
}
