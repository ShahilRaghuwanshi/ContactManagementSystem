package contactmanager;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class ContactManager {

    static class Contact {
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

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public boolean isFavorite() { return favorite; }
        public void setFavorite(boolean favorite) { this.favorite = favorite; }

        public String toCSV() {
            return String.format("%s,%s,%s,%s", name, phone, email, favorite);
        }

        @Override
        public String toString() {
            return String.format("Name: %s | Phone: %s | Email: %s | Favorite: %s",
                    name, phone, email, favorite ? "Yes" : "No");
        }
    }

    private static ArrayList<Contact> contacts = new ArrayList<>();

    // Regex Patterns for validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\n===== Contact Management System =====");
            System.out.println("1. Add Contact");
            System.out.println("2. View All Contacts");
            System.out.println("3. View Favorite Contacts");
            System.out.println("4. Search Contact");
            System.out.println("5. Update Contact");
            System.out.println("6. Delete Contact");
            System.out.println("7. Export to CSV");
            System.out.println("8. Import from CSV");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // discard invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    addContact(scanner);
                    break;
                case 2:
                    viewContacts(false);
                    break;
                case 3:
                    viewContacts(true);
                    break;
                case 4:
                    searchContact(scanner);
                    break;
                case 5:
                    updateContact(scanner);
                    break;
                case 6:
                    deleteContact(scanner);
                    break;
                case 7:
                    exportToCSV();
                    break;
                case 8:
                    importFromCSV();
                    break;
                case 9:
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice! Please select between 1-9.");
            }
        } while (choice != 9);

        scanner.close();
    }

    private static void addContact(Scanner scanner) {
        String name;
        String phone;
        String email;

        // Input and validate name
        while (true) {
            System.out.print("Enter name (letters and spaces only): ");
            name = scanner.nextLine().trim();
            if (!NAME_PATTERN.matcher(name).matches()) {
                System.out.println("Invalid name! Please use letters and spaces only.");
            } else if (findContactIndexByName(name) != -1) {
                System.out.println("Contact with this name already exists!");
                return;
            } else {
                break;
            }
        }

        // Input and validate phone
        while (true) {
            System.out.print("Enter phone number (10 digits): ");
            phone = scanner.nextLine().trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
            } else {
                break;
            }
        }

        // Input and validate email
        while (true) {
            System.out.print("Enter email: ");
            email = scanner.nextLine().trim();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format! Try again.");
            } else {
                break;
            }
        }

        Contact contact = new Contact(name, phone, email);

        // Ask if mark as favorite
        System.out.print("Mark as favorite? (yes/no): ");
        String favInput = scanner.nextLine().trim().toLowerCase();
        contact.setFavorite(favInput.equals("yes") || favInput.equals("y"));

        contacts.add(contact);
        System.out.println("Contact added successfully!");
    }

    private static void viewContacts(boolean favoritesOnly) {
        List<Contact> listToShow = favoritesOnly
                ? contacts.stream().filter(Contact::isFavorite).toList()
                : contacts;

        if (listToShow.isEmpty()) {
            System.out.println(favoritesOnly ? "No favorite contacts found." : "No contacts found.");
            return;
        }

        // Sort alphabetically by name
        listToShow.sort(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));

        System.out.println(favoritesOnly ? "\nFavorite Contacts:" : "\nAll Contacts:");
        int pageSize = 5;
        int total = listToShow.size();
        int pages = (total + pageSize - 1) / pageSize;

        try (Scanner scanner = new Scanner(System.in)) {
			for (int page = 0; page < pages; page++) {
			    int start = page * pageSize;
			    int end = Math.min(start + pageSize, total);

			    for (int i = start; i < end; i++) {
			        System.out.println(listToShow.get(i));
			    }

			    if (page < pages - 1) {
			        System.out.print("Press Enter to see next page...");
			        scanner.nextLine();
			    }
			}
		}
    }

    private static void searchContact(Scanner scanner) {
        System.out.print("Enter keyword to search (name, phone, or email): ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        boolean found = false;
        for (Contact c : contacts) {
            if (c.getName().toLowerCase().contains(keyword) ||
                c.getPhone().toLowerCase().contains(keyword) ||
                c.getEmail().toLowerCase().contains(keyword)) {
                System.out.println(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No contact found matching the keyword.");
        }
    }

    private static void updateContact(Scanner scanner) {
        System.out.print("Enter the name of the contact to update: ");
        String name = scanner.nextLine().trim();

        int index = findContactIndexByName(name);
        if (index == -1) {
            System.out.println("Contact not found.");
            return;
        }

        Contact contact = contacts.get(index);

        // Update phone with validation
        while (true) {
            System.out.print("Enter new phone number (10 digits) or leave blank to keep current (" + contact.getPhone() + "): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) {
                break; // keep current phone
            } else if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
            } else {
                contact.setPhone(phone);
                break;
            }
        }

        // Update email with validation
        while (true) {
            System.out.print("Enter new email or leave blank to keep current (" + contact.getEmail() + "): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                break; // keep current email
            } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format! Try again.");
            } else {
                contact.setEmail(email);
                break;
            }
        }

        // Update favorite status
        System.out.print("Change favorite status? (yes/no) or leave blank to keep current (" + (contact.isFavorite() ? "Yes" : "No") + "): ");
        String favInput = scanner.nextLine().trim().toLowerCase();
        if (favInput.equals("yes") || favInput.equals("y")) {
            contact.setFavorite(true);
        } else if (favInput.equals("no") || favInput.equals("n")) {
            contact.setFavorite(false);
        }

        System.out.println("Contact updated successfully!");
    }

    private static void deleteContact(Scanner scanner) {
        System.out.print("Enter the name of the contact to delete: ");
        String name = scanner.nextLine().trim();

        int index = findContactIndexByName(name);
        if (index == -1) {
            System.out.println("Contact not found.");
            return;
        }

        contacts.remove(index);
        System.out.println("Contact deleted successfully!");
    }

    private static int findContactIndexByName(String name) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private static void exportToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("contacts.csv"))) {
            // Write header
            writer.write("Name,Phone,Email,Favorite");
            writer.newLine();

            for (Contact c : contacts) {
                writer.write(c.toCSV());
                writer.newLine();
            }
            System.out.println("Contacts exported to contacts.csv successfully!");
        } catch (IOException e) {
            System.out.println("Error exporting contacts: " + e.getMessage());
        }
    }

    private static void importFromCSV() {
        File file = new File("contacts.csv");
        if (!file.exists()) {
            System.out.println("contacts.csv not found!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            contacts.clear();
            int imported = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String name = parts[0];
                    String phone = parts[1];
                    String email = parts[2];
                    boolean favorite = Boolean.parseBoolean(parts[3]);
                    Contact c = new Contact(name, phone, email);
                    c.setFavorite(favorite);
                    contacts.add(c);
                    imported++;
                }
            }
            System.out.println(imported + " contacts imported successfully from contacts.csv.");
        } catch (IOException e) {
            System.out.println("Error importing contacts: " + e.getMessage());
        }
    }
}
