import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 *   SAVORIA RESTAURANT MANAGEMENT SYSTEM
 *   Object-Oriented Java Swing Desktop Application
 *   UPES — OOP Capstone Project (Academic Year 2025-2026)
 *
 *   COMPILE & RUN:
 *   Windows:  javac -cp ".;mysql-connector-j.jar" SavoriaApp.java
 *             java  -cp ".;mysql-connector-j.jar" SavoriaApp
 *   Mac/Linux: javac -cp ".:mysql-connector-j.jar" SavoriaApp.java
 *              java  -cp ".:mysql-connector-j.jar" SavoriaApp
 *
 *   OOP CONCEPTS USED:
 *   - Encapsulation  : All model classes with private fields + getters/setters
 *   - Inheritance    : JFrame, JPanel, MouseAdapter subclassing
 *   - Polymorphism   : Method overriding (paintComponent), overloaded helpers
 *   - Abstraction    : DatabaseHelper abstracts all JDBC operations
 *   - Inner Classes  : Anonymous event-handler classes
 *   - Interfaces     : Runnable used for action callbacks
 * ╚══════════════════════════════════════════════════════════════════╝
 */

// ═══════════════════════════════════════════════════════════════
//  MODEL CLASSES  (Encapsulation)
// ═══════════════════════════════════════════════════════════════

/** Represents a restaurant branch */
class Branch {
    private int    branchId;
    private String city, name, address, phone;
    private double rating;
    private boolean active;

    public Branch(int branchId, String city, String name, String address, double rating) {
        this.branchId = branchId; this.city = city; this.name = name;
        this.address = address;   this.rating = rating; this.active = true;
    }
    public int    getBranchId() { return branchId; }
    public String getCity()     { return city;     }
    public String getName()     { return name;     }
    public String getAddress()  { return address;  }
    public double getRating()   { return rating;   }
    public boolean isActive()   { return active;   }
    @Override public String toString() { return name; }
}

/** Represents a menu item */
class MenuItem {
    private int    itemId, branchId;
    private String name, category, description;
    private double price;
    private boolean veg, available;

    public MenuItem(int itemId, int branchId, String name, String category,
                    String description, double price, boolean veg, boolean available) {
        this.itemId = itemId; this.branchId = branchId; this.name = name;
        this.category = category; this.description = description;
        this.price = price; this.veg = veg; this.available = available;
    }
    public int    getItemId()      { return itemId;      }
    public int    getBranchId()    { return branchId;    }
    public String getName()        { return name;        }
    public String getCategory()    { return category;    }
    public String getDescription() { return description; }
    public double getPrice()       { return price;       }
    public boolean isVeg()         { return veg;         }
    public boolean isAvailable()   { return available;   }
    public void setAvailable(boolean a) { this.available = a; }
    public void setPrice(double p)      { this.price = p;     }
    @Override public String toString()  { return name + " (Rs." + (int)price + ")"; }
}

/** Represents a customer order */
class Order {
    private int    orderId;
    private String orderCode, customerName, customerPhone;
    private String deliveryAddress, paymentMethod, status;
    private String deliveryPerson, deliveryPhone;
    private double totalAmount;
    private int    branchId;

    public Order(String orderCode, int branchId, String customerName, String customerPhone,
                 String deliveryAddress, double totalAmount, String paymentMethod) {
        this.orderCode = orderCode; this.branchId = branchId;
        this.customerName = customerName; this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress; this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod; this.status = "CONFIRMED";
    }
    // Getters
    public int    getOrderId()        { return orderId;        }
    public String getOrderCode()      { return orderCode;      }
    public int    getBranchId()       { return branchId;       }
    public String getCustomerName()   { return customerName;   }
    public String getCustomerPhone()  { return customerPhone;  }
    public String getDeliveryAddress(){ return deliveryAddress;}
    public double getTotalAmount()    { return totalAmount;    }
    public String getPaymentMethod()  { return paymentMethod;  }
    public String getStatus()         { return status;         }
    public String getDeliveryPerson() { return deliveryPerson; }
    public String getDeliveryPhone()  { return deliveryPhone;  }
    // Setters
    public void setOrderId(int id)           { this.orderId = id;           }
    public void setStatus(String s)          { this.status = s;             }
    public void setDeliveryPerson(String dp) { this.deliveryPerson = dp;    }
    public void setDeliveryPhone(String dp)  { this.deliveryPhone = dp;     }
}

/** Represents a table booking */
class Booking {
    private int    bookingId, branchId, guests;
    private String bookingCode, customerName, customerPhone;
    private String bookingDate, slotId, slotLabel, specialRequest;
    private String paymentMethod, status;
    private double amountPaid;

    public Booking(String bookingCode, int branchId, String customerName, String customerPhone,
                   String bookingDate, String slotId, String slotLabel, int guests,
                   String specialRequest, double amountPaid, String paymentMethod) {
        this.bookingCode = bookingCode; this.branchId = branchId;
        this.customerName = customerName; this.customerPhone = customerPhone;
        this.bookingDate = bookingDate; this.slotId = slotId; this.slotLabel = slotLabel;
        this.guests = guests; this.specialRequest = specialRequest;
        this.amountPaid = amountPaid; this.paymentMethod = paymentMethod;
        this.status = "CONFIRMED";
    }
    public int    getBookingId()      { return bookingId;      }
    public String getBookingCode()    { return bookingCode;    }
    public int    getBranchId()       { return branchId;       }
    public String getCustomerName()   { return customerName;   }
    public String getCustomerPhone()  { return customerPhone;  }
    public String getBookingDate()    { return bookingDate;    }
    public String getSlotId()         { return slotId;         }
    public String getSlotLabel()      { return slotLabel;      }
    public int    getGuests()         { return guests;          }
    public String getSpecialRequest() { return specialRequest; }
    public double getAmountPaid()     { return amountPaid;     }
    public String getPaymentMethod()  { return paymentMethod;  }
    public String getStatus()         { return status;         }
    public void setBookingId(int id)  { this.bookingId = id;   }
    public void setStatus(String s)   { this.status = s;       }
}

// ═══════════════════════════════════════════════════════════════
//  DATABASE HELPER  (Abstraction — all JDBC in one place)
// ═══════════════════════════════════════════════════════════════
class DatabaseHelper {
    // SQLite: single file, no server needed, auto-created on first run
    static final String DB_FILE = "savoria_rms.db";
    static final String DB_URL  = "jdbc:sqlite:" + DB_FILE;

    static Connection getConnection() throws SQLException {
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException e) {
            System.err.println("[DB] SQLite JDBC Driver not found! Add sqlite-jdbc.jar to classpath.");
        }
        Connection con = DriverManager.getConnection(DB_URL);
        // Enable foreign keys and WAL mode for better performance
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
        }
        return con;
    }

    /** Called once at startup — creates DB + tables + seed data if not exists */
    static void testConnection() {
        try (Connection con = getConnection()) {
            System.out.println("[DB] SQLite connected: " + DB_FILE);
            initDatabase(con);
        } catch (SQLException e) {
            System.err.println("[DB] FAILED: " + e.getMessage());
        }
    }

    /** Creates all tables and inserts seed data on first run */
    static void initDatabase(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            // Check if already initialised
            ResultSet rs = st.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='branches'");
            if (rs.next()) { System.out.println("[DB] Database already initialised."); return; }

            System.out.println("[DB] First run — creating tables and seed data...");

            // admins
            st.execute("CREATE TABLE IF NOT EXISTS admins (" +
                "admin_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "full_name TEXT," +
                "role TEXT DEFAULT 'manager'," +
                "is_active INTEGER DEFAULT 1," +
                "created_at TEXT DEFAULT (datetime('now')))");

            // branches
            st.execute("CREATE TABLE IF NOT EXISTS branches (" +
                "branch_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "city TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "phone TEXT," +
                "rating REAL DEFAULT 4.5," +
                "is_active INTEGER DEFAULT 1)");

            // menu_items
            st.execute("CREATE TABLE IF NOT EXISTS menu_items (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "branch_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "description TEXT," +
                "price REAL NOT NULL," +
                "is_veg INTEGER DEFAULT 1," +
                "is_available INTEGER DEFAULT 1," +
                "FOREIGN KEY(branch_id) REFERENCES branches(branch_id))");

            // orders
            st.execute("CREATE TABLE IF NOT EXISTS orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_code TEXT UNIQUE NOT NULL," +
                "branch_id INTEGER NOT NULL," +
                "customer_name TEXT NOT NULL," +
                "customer_phone TEXT NOT NULL," +
                "delivery_address TEXT NOT NULL," +
                "total_amount REAL NOT NULL," +
                "payment_method TEXT DEFAULT 'UPI'," +
                "status TEXT DEFAULT 'CONFIRMED'," +
                "delivery_person TEXT," +
                "delivery_phone TEXT," +
                "created_at TEXT DEFAULT (datetime('now'))," +
                "FOREIGN KEY(branch_id) REFERENCES branches(branch_id))");

            // order_items
            st.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                "order_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER NOT NULL," +
                "item_id INTEGER NOT NULL," +
                "item_name TEXT NOT NULL," +
                "quantity INTEGER NOT NULL DEFAULT 1," +
                "unit_price REAL NOT NULL," +
                "FOREIGN KEY(order_id) REFERENCES orders(order_id)," +
                "FOREIGN KEY(item_id)  REFERENCES menu_items(item_id))");

            // bookings
            st.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_code TEXT UNIQUE NOT NULL," +
                "branch_id INTEGER NOT NULL," +
                "customer_name TEXT NOT NULL," +
                "customer_phone TEXT NOT NULL," +
                "booking_date TEXT NOT NULL," +
                "slot_id TEXT NOT NULL," +
                "slot_label TEXT NOT NULL," +
                "guests INTEGER DEFAULT 2," +
                "special_request TEXT," +
                "amount_paid REAL DEFAULT 590.0," +
                "payment_method TEXT DEFAULT 'UPI'," +
                "status TEXT DEFAULT 'CONFIRMED'," +
                "created_at TEXT DEFAULT (datetime('now'))," +
                "FOREIGN KEY(branch_id) REFERENCES branches(branch_id))");

            // slots
            st.execute("CREATE TABLE IF NOT EXISTS slots (" +
                "slot_entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "branch_id INTEGER NOT NULL," +
                "booking_date TEXT NOT NULL," +
                "slot_id TEXT NOT NULL," +
                "is_booked INTEGER DEFAULT 0," +
                "booking_ref TEXT," +
                "UNIQUE(branch_id, booking_date, slot_id)," +
                "FOREIGN KEY(branch_id) REFERENCES branches(branch_id))");

            // ── Seed data — one row per execute (SQLite JDBC compatibility) ──
            st.execute("INSERT INTO admins(username,password_hash,full_name,role) VALUES('admin','savoria@123','Super Admin','superadmin')");
            st.execute("INSERT INTO admins(username,password_hash,full_name,role) VALUES('manager','manager@456','Branch Manager','manager')");

            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Delhi','Savoria Delhi - Connaught Place','F-12, Connaught Circus, New Delhi','+91-11-23456789',4.8)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Mumbai','Savoria Mumbai - Bandra','Shop 7, Linking Road, Bandra West, Mumbai','+91-22-34567890',4.7)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Bangalore','Savoria Bangalore - Koramangala','80 Feet Road, Koramangala, Bengaluru','+91-80-45678901',4.6)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Pune','Savoria Pune - FC Road','Fergusson College Road, Deccan, Pune','+91-20-56789012',4.5)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Dehradun','Savoria Dehradun - Rajpur Road','14-A Rajpur Road, Dehradun, Uttarakhand','+91-135-6789012',4.7)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Hyderabad','Savoria Hyderabad - Banjara Hills','Rd No 12, Banjara Hills, Hyderabad','+91-40-67890123',4.6)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Chennai','Savoria Chennai - Anna Nagar','3rd Ave, Anna Nagar, Chennai','+91-44-78901234',4.5)");
            st.execute("INSERT INTO branches(city,name,address,phone,rating) VALUES('Kolkata','Savoria Kolkata - Park Street','22 Park Street, Kolkata','+91-33-89012345',4.4)");

            // Menu items for all 8 branches
            String[][] menuItems = {
                {"Paneer Tikka",        "Starters",     "Marinated cottage cheese, tandoor roasted",  "280",  "1"},
                {"Chicken Seekh Kebab", "Starters",     "Minced chicken with herbs on skewers",       "340",  "0"},
                {"Mushroom Bruschetta", "Starters",     "Toasted bread with wild mushroom blend",     "220",  "1"},
                {"Crispy Calamari",     "Starters",     "Lightly battered, served with lime aioli",   "380",  "0"},
                {"Butter Chicken",      "Main Course",  "Classic creamy tomato gravy with naan",      "420",  "0"},
                {"Dal Makhani",         "Main Course",  "Slow-cooked black lentils with cream",       "320",  "1"},
                {"Pesto Pasta",         "Main Course",  "Basil pesto, cherry tomatoes, parmesan",     "360",  "1"},
                {"Grilled Sea Bass",    "Main Course",  "Herb crust, lemon butter sauce",             "580",  "0"},
                {"Lamb Rogan Josh",     "Main Course",  "Kashmiri spiced braised lamb",               "520",  "0"},
                {"Garlic Naan",         "Breads & Rice","Butter garlic, tandoor baked",               "80",   "1"},
                {"Saffron Rice",        "Breads & Rice","Basmati, saffron, crispy fried onion",       "180",  "1"},
                {"Biryani Dum Pot",     "Breads & Rice","Fragrant layers, raita, salan",              "440",  "0"},
                {"Mango Lassi",         "Beverages",    "Fresh mango, yogurt, cardamom",              "120",  "1"},
                {"Cold Brew Coffee",    "Beverages",    "18-hour steeped, served over ice",           "160",  "1"},
                {"Virgin Mojito",       "Beverages",    "Mint, lime, soda, crushed ice",              "140",  "1"},
                {"Gulab Jamun",         "Desserts",     "Soft khoya balls in rose syrup",             "160",  "1"},
                {"Tiramisu",            "Desserts",     "Classic Italian, espresso soaked layers",    "280",  "1"},
                {"Kulfi Falooda",       "Desserts",     "Pistachio kulfi with rose falooda",          "200",  "1"}
            };
            String menuSql = "INSERT INTO menu_items(branch_id,name,category,description,price,is_veg) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement pm = con.prepareStatement(menuSql)) {
                for (int bid = 1; bid <= 8; bid++) {
                    for (String[] item : menuItems) {
                        pm.setInt(1, bid);
                        pm.setString(2, item[0]);
                        pm.setString(3, item[1]);
                        pm.setString(4, item[2]);
                        pm.setDouble(5, Double.parseDouble(item[3]));
                        pm.setInt(6, Integer.parseInt(item[4]));
                        pm.executeUpdate();
                    }
                }
            }

            // Sample orders
            st.execute("INSERT INTO orders(order_code,branch_id,customer_name,customer_phone,delivery_address,total_amount,payment_method,status,delivery_person,delivery_phone) VALUES('ORD-100001',1,'Rahul Sharma','+91-9876543210','12-A, Model Town, Delhi',755,'UPI','DELIVERED','Ramesh Kumar','+91-9811223344')");
            st.execute("INSERT INTO orders(order_code,branch_id,customer_name,customer_phone,delivery_address,total_amount,payment_method,status,delivery_person,delivery_phone) VALUES('ORD-100002',1,'Priya Mehta','+91-8765432109','B-4, Laxmi Nagar, Delhi',1050,'Card','ON_THE_WAY','Sunil Sharma','+91-9822334455')");
            st.execute("INSERT INTO orders(order_code,branch_id,customer_name,customer_phone,delivery_address,total_amount,payment_method,status,delivery_person,delivery_phone) VALUES('ORD-100003',1,'Aditya Singh','+91-7654321098','C-9, Dwarka Sector 6, Delhi',435,'Cash','PREPARING','Arjun Mehta','+91-9833445566')");

            // Sample bookings
            String today = java.time.LocalDate.now().toString();
            st.execute("INSERT INTO bookings(booking_code,branch_id,customer_name,customer_phone,booking_date,slot_id,slot_label,guests,special_request,payment_method) VALUES('BKG-200001',1,'Kavya Reddy','+91-9765432108','" + today + "','s2','12:00 PM - 02:00 PM',4,'Window seat please','UPI')");
            st.execute("INSERT INTO bookings(booking_code,branch_id,customer_name,customer_phone,booking_date,slot_id,slot_label,guests,special_request,payment_method) VALUES('BKG-200002',1,'Nikhil Joshi','+91-8654321097','" + today + "','s5','06:00 PM - 08:00 PM',2,'Anniversary dinner','Card')");

            st.execute("INSERT INTO slots(branch_id,booking_date,slot_id,is_booked,booking_ref) VALUES(1,'" + today + "','s2',1,'BKG-200001')");
            st.execute("INSERT INTO slots(branch_id,booking_date,slot_id,is_booked,booking_ref) VALUES(1,'" + today + "','s5',1,'BKG-200002')");

            System.out.println("[DB] Database initialised successfully!");
        }
    }

    /** READ — verify admin credentials */
    static boolean verifyAdmin(String username, String password) {
        String sql = "SELECT password_hash FROM admins WHERE username=? AND is_active=1";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("password_hash").equals(password);
        } catch (SQLException ignored) {}
        // Fallback if DB unavailable
        return username.equals("admin") && password.equals("savoria@123");
    }

    /** READ — find branch by city */
    static Branch findBranchByCity(String city) {
        String sql = "SELECT * FROM branches WHERE LOWER(city)=LOWER(?) AND is_active=1";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, city);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Branch(rs.getInt("branch_id"), rs.getString("city"),
                rs.getString("name"), rs.getString("address"), rs.getDouble("rating"));
        } catch (SQLException ignored) {}
        return null;
    }

    /** READ — get all branches */
    static List<Branch> getAllBranches() {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE is_active=1 ORDER BY city";
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(new Branch(rs.getInt("branch_id"), rs.getString("city"),
                rs.getString("name"), rs.getString("address"), rs.getDouble("rating")));
        } catch (SQLException ignored) {}
        return list;
    }

    /** READ — get menu items for a branch and category */
    static List<MenuItem> getMenuItems(int branchId, String category) {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE branch_id=? AND category=? AND is_available=1 ORDER BY name";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId); ps.setString(2, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new MenuItem(rs.getInt("item_id"), branchId,
                rs.getString("name"), rs.getString("category"), rs.getString("description"),
                rs.getDouble("price"), rs.getBoolean("is_veg"), rs.getBoolean("is_available")));
        } catch (SQLException ignored) {
            // Demo fallback data
            list.addAll(getDemoItems(branchId, category));
        }
        return list;
    }

    /** READ — all menu items for admin */
    static List<MenuItem> getAllMenuItems(int branchId) {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE branch_id=? ORDER BY category, name";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new MenuItem(rs.getInt("item_id"), branchId,
                rs.getString("name"), rs.getString("category"), rs.getString("description"),
                rs.getDouble("price"), rs.getBoolean("is_veg"), rs.getBoolean("is_available")));
        } catch (SQLException ignored) {}
        return list;
    }

    /** CREATE — insert a new order */
    static int saveOrder(Order order) {
        String sql = "INSERT INTO orders (order_code,branch_id,customer_name,customer_phone," +
                     "delivery_address,total_amount,payment_method,status,delivery_person,delivery_phone) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderCode()); ps.setInt(2, order.getBranchId());
            ps.setString(3, order.getCustomerName()); ps.setString(4, order.getCustomerPhone());
            ps.setString(5, order.getDeliveryAddress()); ps.setDouble(6, order.getTotalAmount());
            ps.setString(7, order.getPaymentMethod()); ps.setString(8, order.getStatus());
            ps.setString(9, order.getDeliveryPerson()); ps.setString(10, order.getDeliveryPhone());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException ignored) {}
        return -1;
    }

    /** CREATE — insert order items */
    static void saveOrderItems(int orderId, Map<Integer, Integer> cart,
                               Map<Integer, String> itemNames, Map<Integer, Double> itemPrices) {
        String sql = "INSERT INTO order_items (order_id,item_id,item_name,quantity,unit_price) VALUES (?,?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
                ps.setInt(1, orderId); ps.setInt(2, e.getKey());
                ps.setString(3, itemNames.getOrDefault(e.getKey(), "?"));
                ps.setInt(4, e.getValue()); ps.setDouble(5, itemPrices.getOrDefault(e.getKey(), 0.0));
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ignored) {}
    }

    /** UPDATE — update order status */
    static boolean updateOrderStatus(String orderCode, String newStatus) {
        String sql = "UPDATE orders SET status=? WHERE order_code=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus); ps.setString(2, orderCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /** DELETE — delete an order */
    static boolean deleteOrder(String orderCode) {
        String findSql = "SELECT order_id FROM orders WHERE order_code=?";
        String delItemsSql = "DELETE FROM order_items WHERE order_id=?";
        String delOrderSql = "DELETE FROM orders WHERE order_id=?";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            int orderId = -1;
            try (PreparedStatement find = con.prepareStatement(findSql)) {
                find.setString(1, orderCode);
                ResultSet rs = find.executeQuery();
                if (rs.next()) orderId = rs.getInt("order_id");
            }
            if (orderId == -1) {
                con.rollback();
                con.setAutoCommit(true);
                return false;
            }

            try (PreparedStatement delItems = con.prepareStatement(delItemsSql);
                 PreparedStatement delOrder = con.prepareStatement(delOrderSql)) {
                delItems.setInt(1, orderId);
                delItems.executeUpdate();

                delOrder.setInt(1, orderId);
                boolean deleted = delOrder.executeUpdate() > 0;

                if (deleted) con.commit();
                else con.rollback();

                con.setAutoCommit(true);
                return deleted;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /** READ — get all orders for a branch */
    static List<String[]> getAllOrders(int branchId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT order_code, customer_name, customer_phone, total_amount, " +
                     "payment_method, status, created_at FROM orders " +
                     (branchId > 0 ? "WHERE branch_id=? " : "") +
                     "ORDER BY created_at DESC LIMIT 100";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (branchId > 0) ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{
                rs.getString("order_code"), rs.getString("customer_name"),
                rs.getString("customer_phone"), "Rs." + (int)rs.getDouble("total_amount"),
                rs.getString("payment_method"), rs.getString("status"),
                rs.getString("created_at").substring(0, 16)
            });
        } catch (SQLException e) { System.err.println("getAllOrders error: " + e.getMessage()); }
        return list;
    }

    /** CREATE — insert a booking */
    static int saveBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_code,branch_id,customer_name,customer_phone," +
                     "booking_date,slot_id,slot_label,guests,special_request,amount_paid,payment_method,status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, booking.getBookingCode()); ps.setInt(2, booking.getBranchId());
            ps.setString(3, booking.getCustomerName()); ps.setString(4, booking.getCustomerPhone());
            ps.setString(5, booking.getBookingDate()); ps.setString(6, booking.getSlotId());
            ps.setString(7, booking.getSlotLabel()); ps.setInt(8, booking.getGuests());
            ps.setString(9, booking.getSpecialRequest()); ps.setDouble(10, booking.getAmountPaid());
            ps.setString(11, booking.getPaymentMethod()); ps.setString(12, booking.getStatus());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException ignored) {}
        return -1;
    }

    /** UPDATE — update booking status */
    static boolean updateBookingStatus(String bookingCode, String newStatus) {
        String sql = "UPDATE bookings SET status=? WHERE booking_code=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus); ps.setString(2, bookingCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /** DELETE — delete a booking AND free its slot */
    static boolean deleteBooking(String bookingCode) {
        // First, look up the branch_id, booking_date, and slot_id so we can free the slot
        String lookupSql = "SELECT branch_id, booking_date, slot_id FROM bookings WHERE booking_code=?";
        try (Connection con = getConnection()) {
            int branchId = -1; String bookingDate = null; String slotId = null;
            try (PreparedStatement ps = con.prepareStatement(lookupSql)) {
                ps.setString(1, bookingCode);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    branchId    = rs.getInt("branch_id");
                    bookingDate = rs.getString("booking_date");
                    slotId      = rs.getString("slot_id");
                }
            }
            // Delete the booking row
            boolean deleted;
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM bookings WHERE booking_code=?")) {
                ps.setString(1, bookingCode);
                deleted = ps.executeUpdate() > 0;
            }
            // Free the slot so it becomes available again
            if (deleted && branchId != -1 && bookingDate != null && slotId != null) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE slots SET is_booked=0, booking_ref=NULL " +
                        "WHERE branch_id=? AND booking_date=? AND slot_id=?")) {
                    ps.setInt(1, branchId);
                    ps.setString(2, bookingDate);
                    ps.setString(3, slotId);
                    ps.executeUpdate();
                }
            }
            return deleted;
        } catch (SQLException e) { System.err.println("deleteBooking error: " + e.getMessage()); return false; }
    }

    /** READ — get all bookings for a branch */
    static List<String[]> getAllBookings(int branchId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT booking_code, customer_name, customer_phone, booking_date, " +
                     "slot_label, guests, amount_paid, status FROM bookings " +
                     (branchId > 0 ? "WHERE branch_id=? " : "") +
                     "ORDER BY booking_date DESC, slot_id ASC LIMIT 100";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (branchId > 0) ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{
                rs.getString("booking_code"), rs.getString("customer_name"),
                rs.getString("customer_phone"), rs.getString("booking_date"),
                rs.getString("slot_label"), String.valueOf(rs.getInt("guests")),
                "Rs." + (int)rs.getDouble("amount_paid"), rs.getString("status")
            });
        } catch (SQLException e) { System.err.println("getAllBookings error: " + e.getMessage()); }
        return list;
    }

    /** UPDATE — update menu item price */
    static boolean updateMenuItemPrice(int itemId, double newPrice) {
        String sql = "UPDATE menu_items SET price=? WHERE item_id=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newPrice); ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /** UPDATE — toggle menu item availability */
    static boolean toggleMenuItemAvailability(int itemId, boolean available) {
        String sql = "UPDATE menu_items SET is_available=? WHERE item_id=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, available); ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /** READ — booked slots for a branch on a date.
     *  Reads from the bookings table (source of truth) so deleted/cancelled
     *  bookings are reflected immediately without relying on the slots cache. */
    static List<String> getBookedSlots(int branchId, String date) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT slot_id FROM bookings WHERE branch_id=? AND booking_date=? AND status != 'CANCELLED'";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId); ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { System.err.println("[DB] getBookedSlots error: " + e.getMessage()); }
        return list;
    }

    /** CREATE — mark a slot as booked */
    static void markSlotBooked(int branchId, String date, String slotId, String bookingRef) {
        String sql = "INSERT OR REPLACE INTO slots (branch_id,booking_date,slot_id,is_booked,booking_ref) " +
                     "VALUES (?,?,?,1,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId); ps.setString(2, date); ps.setString(3, slotId);
            ps.setString(4, bookingRef);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    /** Demo fallback items when DB is not connected */
    static List<MenuItem> getDemoItems(int bid, String cat) {
        List<MenuItem> list = new ArrayList<>();
        if (cat.equals("Starters")) {
            list.add(new MenuItem(1, bid, "Paneer Tikka", cat, "Marinated cottage cheese, tandoor roasted", 280, true, true));
            list.add(new MenuItem(2, bid, "Chicken Seekh Kebab", cat, "Minced chicken with herbs on skewers", 340, false, true));
            list.add(new MenuItem(3, bid, "Mushroom Bruschetta", cat, "Toasted bread, wild mushroom blend", 220, true, true));
        } else if (cat.equals("Main Course")) {
            list.add(new MenuItem(5, bid, "Butter Chicken", cat, "Classic creamy tomato gravy with naan", 420, false, true));
            list.add(new MenuItem(6, bid, "Dal Makhani", cat, "Slow-cooked black lentils with cream", 320, true, true));
            list.add(new MenuItem(7, bid, "Pesto Pasta", cat, "Basil pesto, cherry tomatoes, parmesan", 360, true, true));
        } else if (cat.equals("Beverages")) {
            list.add(new MenuItem(13, bid, "Mango Lassi", cat, "Fresh mango, yogurt, cardamom", 120, true, true));
            list.add(new MenuItem(14, bid, "Cold Brew Coffee", cat, "18-hour steeped, served over ice", 160, true, true));
        } else if (cat.equals("Desserts")) {
            list.add(new MenuItem(16, bid, "Gulab Jamun", cat, "Soft khoya balls in rose syrup", 160, true, true));
            list.add(new MenuItem(17, bid, "Tiramisu", cat, "Classic Italian, espresso soaked", 280, true, true));
        } else if (cat.equals("Breads & Rice")) {
            list.add(new MenuItem(10, bid, "Garlic Naan", cat, "Butter garlic, tandoor baked", 80, true, true));
            list.add(new MenuItem(11, bid, "Saffron Rice", cat, "Basmati, saffron, crispy fried onion", 180, true, true));
        }
        return list;
    }
}

// ═══════════════════════════════════════════════════════════════
//  MAIN APPLICATION CLASS  (Inheritance from JFrame)
// ═══════════════════════════════════════════════════════════════
public class SavoriaApp extends JFrame {

    // ── THEME COLORS ──────────────────────────────────────────
    static final Color C_BG     = new Color(247, 244, 238);
    static final Color C_NAV    = new Color(32, 44, 74);
    static final Color C_GOLD   = new Color(198, 150, 76);
    static final Color C_GOLD2  = new Color(226, 186, 108);
    static final Color C_WHITE  = new Color(255, 252, 247);
    static final Color C_TEXT   = new Color(44, 44, 52);
    static final Color C_DIM    = new Color(116, 109, 98);
    static final Color C_GREEN  = new Color(39, 174, 96);
    static final Color C_RED    = new Color(192, 57, 43);
    static final Color C_BORDER = new Color(225, 214, 197);
    static final Color C_ADMIN  = new Color(62, 74, 95);
    static final Color C_SHADOW = new Color(18, 22, 32, 28);

    // ── FONTS ─────────────────────────────────────────────────
    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 30);
    static final Font F_HEAD  = new Font("Segoe UI", Font.BOLD, 19);
    static final Font F_SUB   = new Font("Segoe UI", Font.PLAIN, 15);
    static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_BTN   = new Font("Segoe UI", Font.BOLD, 13);
    static final Font F_LABEL = new Font("Segoe UI", Font.BOLD, 12);

    // ── SLOT DATA ─────────────────────────────────────────────
    static final String[] SLOT_IDS    = {"s1","s2","s3","s4","s5","s6"};
    static final String[] SLOT_LABELS = {
        "10:00 AM - 12:00 PM","12:00 PM - 02:00 PM",
        "02:00 PM - 04:00 PM","04:00 PM - 06:00 PM",
        "06:00 PM - 08:00 PM","08:00 PM - 10:00 PM"
    };
    static final String[] RIDERS = {
        "Ramesh Kumar|+91-9811223344","Sunil Sharma|+91-9822334455",
        "Arjun Mehta|+91-9833445566","Vikram Singh|+91-9844556677"
    };

    // ── APP STATE ─────────────────────────────────────────────
    String  appFlow    = "";
    boolean isAdminLoggedIn = false;
    Branch  currentBranch = null;
    Map<Integer,Integer> cart       = new LinkedHashMap<>();
    Map<Integer,String>  itemNames  = new HashMap<>();
    Map<Integer,Double>  itemPrices = new HashMap<>();
    String  selectedSlot = "", selectedSlotLabel = "";
    String  bookingDate  = "";
    String  lastOrderId  = "", lastBookingId = "";
    String  deliveryName = "", deliveryPhone  = "";

    // ── NAVIGATION ────────────────────────────────────────────
    CardLayout cardLayout = new CardLayout();
    JPanel     cardPanel  = new JPanel(cardLayout);
    java.util.Deque<String> navHistory = new java.util.ArrayDeque<>();
    JButton    backNavBtn = null; // back arrow in header

    // ── FIELD REFERENCES ──────────────────────────────────────
    JTextField cityField, areaField, addrField;
    JPanel     branchResultPanel;
    JPanel     menuMainPanel;
    JLabel     cartTotalLabel, cartCountLabel;
    JPanel     paymentMainPanel;
    JTextField payPhoneField;
    ButtonGroup payMethodGroup;
    JPanel     orderConfirmMain;
    int        trackStep = 1;
    JPanel[]   trackDots;
    JLabel[]   trackLabels;
    JPanel     slotMainPanel, slotGridPanel;
    JTextField bookingDateField;
    JComboBox<String> guestsCombo;
    JTextField specialReqField;
    JPanel     bookingPayMain;
    JTextField bpayPhoneField;
    ButtonGroup bpayMethodGroup;
    JPanel     bookingConfirmMain;
    JPanel     adminMain;

    // ─────────────────────────────────────────────────────────
    public SavoriaApp() {
        DatabaseHelper.testConnection(); // prints DB status to console on startup
        setTitle("Savoria — Restaurant Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        applyGlobalUI();

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        // Register all screens
        cardPanel.add(buildHomePanel(),           "home");
        cardPanel.add(buildAddressPanel(),        "address");
        cardPanel.add(buildMenuPanel(),           "menu");
        cardPanel.add(buildPaymentPanel(),        "payment");
        cardPanel.add(buildOrderConfirmPanel(),   "orderConfirm");
        cardPanel.add(buildSlotPanel(),           "slots");
        cardPanel.add(buildBookingPayPanel(),     "bookingPay");
        cardPanel.add(buildBookingConfirmPanel(), "bookingConfirm");
        cardPanel.add(buildAdminPanel(),          "admin");

        showScreen("home");
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════════════════
    JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_NAV, getWidth(), getHeight(), new Color(19, 29, 52));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.setColor(C_GOLD);
                g2.fillRect(0, getHeight()-3, getWidth(), 3);
            }
        };
        h.setPreferredSize(new Dimension(0, 58));
        h.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("SAVORIA");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24)); logo.setForeground(C_GOLD2);
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { goHome(); }
        });

        JLabel tagline = new JLabel("Restaurant Management System");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tagline.setForeground(new Color(210, 212, 226));

        // ── Back arrow button ──────────────────────────────────
        backNavBtn = new JButton("<") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255,255,255,30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g);
            }
        };
        backNavBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        backNavBtn.setForeground(C_GOLD2);
        backNavBtn.setContentAreaFilled(false);
        backNavBtn.setBorderPainted(false);
        backNavBtn.setFocusPainted(false);
        backNavBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backNavBtn.setPreferredSize(new Dimension(38, 38));
        backNavBtn.setToolTipText("Go back");
        backNavBtn.setVisible(false); // hidden on home screen
        backNavBtn.addActionListener(e -> goBack());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(backNavBtn); left.add(logo); left.add(tagline);

        JButton adminBtn = adminButton("Admin Panel", C_GOLD);
        adminBtn.setForeground(new Color(20, 35, 60)); // override to navy text on gold
        adminBtn.addActionListener(e -> showAdminLogin());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false); right.add(adminBtn);

        h.add(left, BorderLayout.WEST); h.add(right, BorderLayout.EAST);
        return h;
    }

    // ══════════════════════════════════════════════════════════
    //  HOME PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(C_BG);

        JPanel hero = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(36, 57, 93), getWidth(), getHeight(), new Color(18, 31, 56));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(255,255,255,18));
                g2.fillOval(getWidth()-180, -70, 240, 240);
                g2.fillOval(-80, getHeight()-140, 220, 220);
            }
        };
        hero.setPreferredSize(new Dimension(0, 210));

        JPanel heroInner = new JPanel(); heroInner.setOpaque(false);
        heroInner.setLayout(new BoxLayout(heroInner, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Fine Dining, Delivered to You.");
        title.setFont(new Font("Georgia", Font.BOLD|Font.ITALIC, 36));
        title.setForeground(C_WHITE); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Choose your experience below");
        sub.setFont(F_SUB); sub.setForeground(new Color(218,220,231));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroInner.add(title); heroInner.add(Box.createVerticalStrut(8)); heroInner.add(sub);
        hero.add(heroInner);

        JPanel cards = new JPanel(new GridLayout(1, 2, 24, 0));
        cards.setOpaque(false); cards.setBorder(BorderFactory.createEmptyBorder(34, 80, 40, 80));

        cards.add(buildChoiceCard(">>", "Order",
            "Get food delivered to your doorstep.\nBrowse menu, pay online, track live.", () -> {
                appFlow = "order"; cart.clear(); showScreen("address");
            }));
        cards.add(buildChoiceCard("[T]", "Booking",
            "Reserve a table at your nearest\nSavoria branch for dining in.", () -> {
                appFlow = "booking"; showScreen("address");
            }));

        p.add(hero, BorderLayout.NORTH); p.add(cards, BorderLayout.CENTER);
        return p;
    }

    JPanel buildChoiceCard(String icon, String titleText, String desc, Runnable action) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_SHADOW);
                g2.fillRoundRect(4, 6, getWidth()-9, getHeight()-11, 20, 20);
                g2.setColor(C_WHITE);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.setColor(new Color(236, 227, 211));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.setColor(new Color(218, 189, 135, 82));
                g2.fillRoundRect(0, 0, getWidth()-1, 8, 20, 20);
            }
        };
        card.setOpaque(false); card.setBorder(BorderFactory.createEmptyBorder(28,28,28,28));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel inner = new JPanel(); inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel ico = new JLabel(icon); ico.setFont(new Font("Segoe UI Symbol",Font.PLAIN,42));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel ttl = new JLabel(titleText); ttl.setFont(F_TITLE); ttl.setForeground(C_NAV);
        ttl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel dsc = new JLabel("<html><div style='text-align:center;width:200px'>"+desc.replace("\n","<br>")+"</div></html>");
        dsc.setFont(F_BODY); dsc.setForeground(C_DIM); dsc.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton btn = goldButton(titleText.equals("Order") ? "Order Now" : "Book Table");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> action.run());

        inner.add(ico); inner.add(Box.createVerticalStrut(10)); inner.add(ttl);
        inner.add(Box.createVerticalStrut(8)); inner.add(dsc);
        inner.add(Box.createVerticalStrut(16)); inner.add(btn);
        card.add(inner, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,2,true),BorderFactory.createEmptyBorder(26,26,26,26))); }
            @Override public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(28,28,28,28)); }
        });
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  ADDRESS PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildAddressPanel() { return scrollWrap(buildAddressContent()); }

    JPanel buildAddressContent() {
        JPanel p = new JPanel(); p.setBackground(C_BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(32, 60, 32, 60));

        p.add(sectionTitle("Enter Your Address"));
        p.add(Box.createVerticalStrut(4));
        p.add(sectionSub("We'll check if Savoria is available in your city"));
        p.add(Box.createVerticalStrut(24));

        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(6,0,6,12);

        cityField = styledField("e.g. Delhi, Dehradun, Mumbai...");
        areaField = styledField("e.g. Rajpur Road, Connaught Place...");
        addrField = styledField("Street address, landmark...");

        c.gridx=0;c.gridy=0;c.weightx=0.5; form.add(labeled("City *", cityField), c);
        c.gridx=1;c.gridy=0;c.weightx=0.5; form.add(labeled("Area / Locality", areaField), c);
        c.gridx=0;c.gridy=1;c.gridwidth=2;  form.add(labeled("Full Address", addrField), c);
        p.add(form); p.add(Box.createVerticalStrut(16));

        JButton checkBtn = goldButton("Check Availability");
        checkBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBtn.addActionListener(e -> checkBranch());
        p.add(checkBtn); p.add(Box.createVerticalStrut(20));

        branchResultPanel = new JPanel(); branchResultPanel.setOpaque(false);
        branchResultPanel.setLayout(new BoxLayout(branchResultPanel, BoxLayout.Y_AXIS));
        p.add(branchResultPanel);
        return p;
    }

    void checkBranch() {
        String city = cityField.getText().trim();
        if (city.isEmpty() || city.equals("e.g. Delhi, Dehradun, Mumbai...")) {
            showMsg("Please enter your city."); return;
        }
        branchResultPanel.removeAll();
        Branch branch = DatabaseHelper.findBranchByCity(city);

        if (branch != null) {
            currentBranch = branch;
            showBranchFound(branch);
        } else {
            // Show not available message
            currentBranch = null;
            JPanel card = statusCard(false,
                "Sorry! We're not available in " + city + " yet.",
                "Savoria is currently available in:\nDelhi, Mumbai, Bangalore, Pune, Dehradun, Hyderabad, Chennai, Kolkata");
            branchResultPanel.add(card);
        }
        branchResultPanel.revalidate(); branchResultPanel.repaint();
    }

    void showBranchFound(Branch branch) {
        JPanel card = statusCard(true,
            "Great news! We're available in " + branch.getCity(),
            branch.getName() + "\n" + branch.getAddress() + "\nRating: " + branch.getRating() + " / 5.0");

        JButton nextBtn = goldButton(appFlow.equals("order") ? "View Menu and Order" : "Select Time Slot");
        nextBtn.addActionListener(e -> {
            if (appFlow.equals("order")) { rebuildMenuPanel(); showScreen("menu"); }
            else { rebuildSlotPanel(); showScreen("slots"); }
        });
        nextBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        branchResultPanel.add(card); branchResultPanel.add(Box.createVerticalStrut(12)); branchResultPanel.add(nextBtn);
    }

    // ══════════════════════════════════════════════════════════
    //  MENU PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildMenuPanel() {
        menuMainPanel = new JPanel(new BorderLayout()); menuMainPanel.setBackground(C_BG);
        return menuMainPanel;
    }

    void rebuildMenuPanel() {
        menuMainPanel.removeAll();
        cart.clear(); itemNames.clear(); itemPrices.clear();

        JPanel top = new JPanel(new BorderLayout()); top.setBackground(C_NAV);
        top.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        JLabel lbl = new JLabel("Menu - " + (currentBranch != null ? currentBranch.getName() : ""));
        lbl.setFont(F_HEAD); lbl.setForeground(C_WHITE);

        JPanel cartInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); cartInfo.setOpaque(false);
        cartCountLabel = new JLabel("0 items"); cartCountLabel.setFont(F_BTN); cartCountLabel.setForeground(C_GOLD2);
        cartTotalLabel = new JLabel("Rs.0"); cartTotalLabel.setFont(F_HEAD); cartTotalLabel.setForeground(C_GOLD2);
        JButton cartBtn = goldButton("Checkout");
        cartBtn.addActionListener(e -> {
            if (cart.isEmpty()) { showMsg("Add at least one item!"); return; }
            rebuildPaymentPanel(); showScreen("payment");
        });
        cartInfo.add(cartCountLabel);
        cartInfo.add(new JLabel(" | ") {{ setForeground(C_GOLD2); }});
        cartInfo.add(cartTotalLabel); cartInfo.add(Box.createHorizontalStrut(12)); cartInfo.add(cartBtn);
        top.add(lbl, BorderLayout.WEST); top.add(cartInfo, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(F_BTN); tabs.setBackground(C_BG);
        String[] categories = {"Starters","Main Course","Breads & Rice","Beverages","Desserts"};
        for (String cat : categories) {
            JPanel catPanel = new JPanel(); catPanel.setBackground(C_BG);
            catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
            catPanel.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
            List<MenuItem> items = DatabaseHelper.getMenuItems(
                currentBranch != null ? currentBranch.getBranchId() : 1, cat);
            if (items.isEmpty()) catPanel.add(new JLabel("No items available.") {{ setFont(F_BODY); setForeground(C_DIM); }});
            for (MenuItem item : items) { catPanel.add(buildMenuItemRow(item)); catPanel.add(Box.createVerticalStrut(8)); }
            tabs.addTab(cat, new JScrollPane(catPanel));
        }

        menuMainPanel.add(top, BorderLayout.NORTH);
        menuMainPanel.add(tabs, BorderLayout.CENTER);
        menuMainPanel.revalidate(); menuMainPanel.repaint();
    }

    JPanel buildMenuItemRow(MenuItem item) {
        itemNames.put(item.getItemId(), item.getName());
        itemPrices.put(item.getItemId(), item.getPrice());

        JPanel row = new JPanel(new BorderLayout(12, 0)); row.setBackground(C_WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDER, 1, true), BorderFactory.createEmptyBorder(12,16,12,16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel info = new JPanel(new GridLayout(2,1,0,2)); info.setOpaque(false);
        JLabel nameLbl = new JLabel((item.isVeg() ? "[V] " : "[N] ") + item.getName());
        nameLbl.setFont(F_LABEL); nameLbl.setForeground(C_TEXT);
        JLabel descLbl = new JLabel(item.getDescription()); descLbl.setFont(F_SMALL); descLbl.setForeground(C_DIM);
        info.add(nameLbl); info.add(descLbl);

        JLabel priceLbl = new JLabel("Rs." + (int)item.getPrice());
        priceLbl.setFont(new Font("SansSerif",Font.BOLD,15)); priceLbl.setForeground(C_GOLD);

        JPanel qty = new JPanel(new FlowLayout(FlowLayout.RIGHT,6,0)); qty.setOpaque(false);
        JLabel qtyLbl = new JLabel("0"); qtyLbl.setFont(F_BTN); qtyLbl.setPreferredSize(new Dimension(24,20));
        qtyLbl.setHorizontalAlignment(SwingConstants.CENTER);
        JButton minus = qtyBtn("-"); JButton plus = qtyBtn("+");
        int id = item.getItemId();
        minus.addActionListener(e -> {
            int q = cart.getOrDefault(id,0);
            if(q>0){q--;if(q==0)cart.remove(id);else cart.put(id,q);}
            qtyLbl.setText(String.valueOf(cart.getOrDefault(id,0))); updateCartTotal();
        });
        plus.addActionListener(e -> {
            cart.put(id, cart.getOrDefault(id,0)+1);
            qtyLbl.setText(String.valueOf(cart.get(id))); updateCartTotal();
        });
        qty.add(minus); qty.add(qtyLbl); qty.add(plus);

        JPanel right = new JPanel(new GridLayout(2,1)); right.setOpaque(false);
        right.add(priceLbl); right.add(qty);
        row.add(info, BorderLayout.CENTER); row.add(right, BorderLayout.EAST);
        return row;
    }

    void updateCartTotal() {
        double total = 0; int count = 0;
        for (Map.Entry<Integer,Integer> e : cart.entrySet()) {
            total += itemPrices.getOrDefault(e.getKey(),0.0) * e.getValue(); count += e.getValue();
        }
        if (cartTotalLabel!=null) cartTotalLabel.setText("Rs."+(int)total);
        if (cartCountLabel!=null) cartCountLabel.setText(count+" item"+(count==1?"":"s"));
    }

    // ══════════════════════════════════════════════════════════
    //  PAYMENT PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildPaymentPanel() {
        paymentMainPanel = new JPanel(new BorderLayout()); paymentMainPanel.setBackground(C_BG);
        return paymentMainPanel;
    }

    void rebuildPaymentPanel() {
        paymentMainPanel.removeAll();
        JPanel wrap = new JPanel(); wrap.setBackground(C_BG);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(28, 60, 28, 60));

        wrap.add(sectionTitle("Complete Payment"));
        wrap.add(sectionSub("Secure checkout - choose your preferred method"));
        wrap.add(Box.createVerticalStrut(20));

        JPanel twoCol = new JPanel(new GridLayout(1,2,28,0)); twoCol.setOpaque(false);

        // Payment form
        JPanel left = new JPanel(); left.setBackground(C_WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(20,20,20,20)));
        left.add(new JLabel("Payment Method") {{ setFont(F_LABEL); setForeground(C_NAV); }});
        left.add(Box.createVerticalStrut(10));
        payMethodGroup = new ButtonGroup();
        String[][] methods = {{"upi","UPI"},{"card","Card"},{"netbanking","Net Banking"},{"cash","Cash on Delivery"}};
        for (String[] m : methods) {
            JRadioButton rb = new JRadioButton(m[1]); rb.setActionCommand(m[0]); rb.setFont(F_BODY); rb.setOpaque(false);
            if(m[0].equals("upi")) rb.setSelected(true);
            payMethodGroup.add(rb); left.add(rb); left.add(Box.createVerticalStrut(4));
        }
        left.add(Box.createVerticalStrut(16));
        left.add(labeled("Contact Number *", payPhoneField = styledField("+91 98765 43210")));
        left.add(Box.createVerticalStrut(20));
        JButton payBtn = goldButton("Pay and Confirm Order");
        payBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        payBtn.addActionListener(e -> confirmOrder());
        left.add(payBtn);

        // Order summary
        JPanel right = new JPanel(); right.setBackground(C_WHITE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,1,true),BorderFactory.createEmptyBorder(20,20,20,20)));
        right.add(new JLabel("Order Summary") {{ setFont(F_HEAD); setForeground(C_NAV); }});
        right.add(Box.createVerticalStrut(12));

        double subtotal = 0;
        for (Map.Entry<Integer,Integer> e : cart.entrySet()) {
            double sub = itemPrices.getOrDefault(e.getKey(),0.0) * e.getValue(); subtotal += sub;
            right.add(summaryRow(itemNames.getOrDefault(e.getKey(),"?")+" x"+e.getValue(), "Rs."+(int)sub));
            right.add(Box.createVerticalStrut(4));
        }
        right.add(Box.createVerticalStrut(8)); addSeparator(right);
        right.add(summaryRow("Subtotal","Rs."+(int)subtotal));
        right.add(summaryRow("Delivery Fee","Rs.35"));
        int gst = (int)(subtotal*0.05);
        right.add(summaryRow("GST (5%)","Rs."+gst));
        addSeparator(right);
        JPanel tr = summaryRow("TOTAL","Rs."+(int)(subtotal+35+gst));
        ((JLabel)tr.getComponent(0)).setFont(F_BTN);
        ((JLabel)tr.getComponent(1)).setFont(new Font("SansSerif",Font.BOLD,16));
        ((JLabel)tr.getComponent(1)).setForeground(C_GOLD);
        right.add(tr);

        twoCol.add(left); twoCol.add(right); wrap.add(twoCol);
        JButton back = ghostButton("Back to Menu"); back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> showScreen("menu"));
        wrap.add(Box.createVerticalStrut(16)); wrap.add(back);

        paymentMainPanel.add(new JScrollPane(wrap){{ setBorder(null); getViewport().setBackground(C_BG); }}, BorderLayout.CENTER);
        paymentMainPanel.revalidate(); paymentMainPanel.repaint();
    }

    void confirmOrder() {
        String phone = payPhoneField.getText().trim();
        if (phone.isEmpty() || phone.startsWith("+91 98")) { showMsg("Please enter your contact number!"); return; }

        lastOrderId = "ORD-" + (100000 + new Random().nextInt(900000));
        String[] rider = RIDERS[new Random().nextInt(RIDERS.length)].split("\\|");
        deliveryName = rider[0]; deliveryPhone = rider[1];

        double subtotal = 0;
        for (Map.Entry<Integer,Integer> e : cart.entrySet())
            subtotal += itemPrices.getOrDefault(e.getKey(),0.0) * e.getValue();
        double total = subtotal + 35 + subtotal*0.05;
        String city = cityField != null ? cityField.getText().trim() : "";
        String area = areaField != null ? areaField.getText().trim() : "";
        String addr = addrField  != null ? addrField.getText().trim()  : "";
        String fullAddr = (area.isEmpty() ? "" : area+", ") + (addr.isEmpty() ? city : addr);
        String method = payMethodGroup.getSelection().getActionCommand();

        Order order = new Order(lastOrderId, currentBranch != null ? currentBranch.getBranchId() : 1,
            "Customer", phone, fullAddr, total, method);
        order.setDeliveryPerson(deliveryName); order.setDeliveryPhone(deliveryPhone);
        int oid = DatabaseHelper.saveOrder(order);
        if (oid > 0) DatabaseHelper.saveOrderItems(oid, cart, itemNames, itemPrices);

        rebuildOrderConfirm(); showScreen("orderConfirm");
    }

    // ══════════════════════════════════════════════════════════
    //  ORDER CONFIRMATION PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildOrderConfirmPanel() {
        orderConfirmMain = new JPanel(new BorderLayout()); orderConfirmMain.setBackground(C_BG);
        return orderConfirmMain;
    }

    void rebuildOrderConfirm() {
        orderConfirmMain.removeAll(); trackStep = 1;
        JPanel wrap = new JPanel(); wrap.setBackground(C_BG);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(28,100,28,100));

        // Success header
        JPanel header = new JPanel(); header.setBackground(new Color(39,174,96,25));
        header.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GREEN,1,true),BorderFactory.createEmptyBorder(20,24,20,24)));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel ico = new JLabel("Order Confirmed!"); ico.setFont(new Font("SansSerif",Font.BOLD,22));
        ico.setForeground(C_GREEN); ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Your order is being prepared at the kitchen");
        sub.setFont(F_BODY); sub.setForeground(C_DIM); sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); idPanel.setOpaque(false);
        JLabel idLbl = new JLabel(lastOrderId);
        idLbl.setFont(new Font("Monospaced",Font.BOLD,20)); idLbl.setForeground(C_GOLD);
        idLbl.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,1,true),BorderFactory.createEmptyBorder(6,16,6,16)));
        idPanel.add(idLbl);
        header.add(ico); header.add(Box.createVerticalStrut(6));
        header.add(sub); header.add(Box.createVerticalStrut(12)); header.add(idPanel);
        header.setAlignmentX(Component.CENTER_ALIGNMENT); header.setMaximumSize(new Dimension(700,200));
        wrap.add(header); wrap.add(Box.createVerticalStrut(24));

        // Tracking
        String[] stepNames = {"Confirmed","Preparing","Packed","On the Way","Delivered"};
        String[] stepIcons = {"OK","Prep","Pack","On Way","Done"};
        JPanel tracking = new JPanel(new GridLayout(1,stepNames.length,0,0));
        tracking.setOpaque(false); tracking.setMaximumSize(new Dimension(700,80)); tracking.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackDots = new JPanel[stepNames.length]; trackLabels = new JLabel[stepNames.length];
        for (int i=0; i<stepNames.length; i++) {
            JPanel step = new JPanel(); step.setOpaque(false);
            step.setLayout(new BoxLayout(step, BoxLayout.Y_AXIS));
            final int fi = i;
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    Color col = fi<trackStep ? C_GOLD : fi==trackStep ? new Color(201,168,76,80) : C_BORDER;
                    g2.setColor(col); g2.fillOval(2,2,getWidth()-5,getHeight()-5);
                    g2.setColor(fi<trackStep?C_NAV:C_DIM); g2.setFont(new Font("SansSerif",Font.BOLD,13));
                    FontMetrics fm=g2.getFontMetrics();
                    String txt = fi<trackStep?"OK":stepIcons[fi];
                    g2.drawString(txt,(getWidth()-fm.stringWidth(txt))/2,(getHeight()+fm.getAscent())/2-2);
                }
            };
            dot.setPreferredSize(new Dimension(40,40)); dot.setMaximumSize(new Dimension(40,40));
            dot.setOpaque(false); dot.setAlignmentX(Component.CENTER_ALIGNMENT); trackDots[i]=dot;
            JLabel lbl=new JLabel(stepNames[i]); lbl.setFont(F_SMALL);
            lbl.setForeground(i<trackStep?C_GOLD:i==trackStep?C_GOLD:C_DIM);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT); trackLabels[i]=lbl;
            step.add(dot); step.add(Box.createVerticalStrut(4)); step.add(lbl);
            tracking.add(step);
        }
        wrap.add(new JLabel("Live Order Tracking") {{ setFont(F_LABEL); setForeground(C_NAV); }});
        wrap.add(Box.createVerticalStrut(12)); wrap.add(tracking); wrap.add(Box.createVerticalStrut(8));

        JButton refreshBtn = ghostButton("Refresh Status");
        refreshBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshBtn.addActionListener(e -> {
            trackStep = Math.min(trackStep+1,stepNames.length-1);
            if (trackStep < stepNames.length) DatabaseHelper.updateOrderStatus(lastOrderId, getStatusForStep(trackStep));
            for (int i=0;i<trackDots.length;i++) { trackDots[i].repaint(); trackLabels[i].setForeground(i<trackStep?C_GOLD:i==trackStep?C_GOLD:C_DIM); }
            if (trackStep==stepNames.length-1) JOptionPane.showMessageDialog(this,"Order Delivered! Enjoy your meal!","Delivered",JOptionPane.INFORMATION_MESSAGE);
        });
        wrap.add(refreshBtn); wrap.add(Box.createVerticalStrut(20));

        // Rider card
        JPanel riderCard = new JPanel(new BorderLayout(16,0)); riderCard.setBackground(C_WHITE);
        riderCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(16,20,16,20)));
        riderCard.setMaximumSize(new Dimension(700,100)); riderCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel avatar = new JLabel("(Rider)"); avatar.setFont(new Font("SansSerif",Font.PLAIN,36));
        JPanel rInfo = new JPanel(new GridLayout(3,1)); rInfo.setOpaque(false);
        rInfo.add(new JLabel("Your Delivery Partner") {{ setFont(F_SMALL); setForeground(C_DIM); }});
        rInfo.add(new JLabel(deliveryName) {{ setFont(F_LABEL); setForeground(C_TEXT); }});
        rInfo.add(new JLabel(deliveryPhone) {{ setFont(F_BODY); setForeground(C_GOLD); }});
        JButton callBtn = goldButton("Call Rider");
        callBtn.addActionListener(e -> JOptionPane.showMessageDialog(null,"Calling "+deliveryName+"\n"+deliveryPhone,"Calling...",JOptionPane.INFORMATION_MESSAGE));
        riderCard.add(avatar,BorderLayout.WEST); riderCard.add(rInfo,BorderLayout.CENTER); riderCard.add(callBtn,BorderLayout.EAST);
        wrap.add(riderCard); wrap.add(Box.createVerticalStrut(20));

        JButton homeBtn = ghostButton("Back to Home"); homeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        homeBtn.addActionListener(e -> goHome()); wrap.add(homeBtn);

        orderConfirmMain.add(new JScrollPane(wrap){{ setBorder(null); getViewport().setBackground(C_BG); }},BorderLayout.CENTER);
        orderConfirmMain.revalidate(); orderConfirmMain.repaint();
    }

    String getStatusForStep(int step) {
        String[] statuses = {"CONFIRMED","PREPARING","PACKED","ON_THE_WAY","DELIVERED"};
        return step < statuses.length ? statuses[step] : "DELIVERED";
    }

    // ══════════════════════════════════════════════════════════
    //  SLOT PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildSlotPanel() {
        slotMainPanel = new JPanel(new BorderLayout()); slotMainPanel.setBackground(C_BG);
        return slotMainPanel;
    }

    void rebuildSlotPanel() {
        slotMainPanel.removeAll(); selectedSlot=""; selectedSlotLabel="";
        JPanel wrap = new JPanel(); wrap.setBackground(C_BG);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(28,60,28,60));
        wrap.add(sectionTitle("Select Your Time Slot"));
        wrap.add(sectionSub("Each slot is 2 hours · Restaurant hours: 10 AM - 10 PM"));
        wrap.add(Box.createVerticalStrut(20));

        JPanel twoCol = new JPanel(new GridLayout(1,2,28,0)); twoCol.setOpaque(false);

        // Left: date & guests
        JPanel left = new JPanel(); left.setBackground(C_WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(20,20,20,20)));
        bookingDateField = styledField(java.time.LocalDate.now().toString());
        bookingDateField.setText(java.time.LocalDate.now().toString());
        left.add(labeled("Date (YYYY-MM-DD) *", bookingDateField)); left.add(Box.createVerticalStrut(12));
        guestsCombo = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9","10+"});
        guestsCombo.setSelectedIndex(2); guestsCombo.setFont(F_BODY);
        guestsCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        left.add(labeled("Number of Guests", guestsCombo)); left.add(Box.createVerticalStrut(12));
        specialReqField = styledField("Window seat, birthday decor...");
        left.add(labeled("Special Requests", specialReqField)); left.add(Box.createVerticalStrut(20));
        JButton loadBtn = adminButton("Load Slots", C_NAV);
        loadBtn.setAlignmentX(Component.LEFT_ALIGNMENT); loadBtn.addActionListener(e -> loadSlots());
        left.add(loadBtn);

        // Right: slot grid
        JPanel right = new JPanel(); right.setBackground(C_WHITE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(20,20,20,20)));
        right.add(new JLabel("Available Time Slots") {{ setFont(F_LABEL); setForeground(C_NAV); }});
        right.add(Box.createVerticalStrut(12));
        slotGridPanel = new JPanel(new GridLayout(3,2,10,10)); slotGridPanel.setOpaque(false);
        slotGridPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,240));
        right.add(slotGridPanel); right.add(Box.createVerticalStrut(12));
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0)); legend.setOpaque(false);
        legend.add(colorDot(C_GREEN)); legend.add(new JLabel("Available"){{ setFont(F_SMALL); setForeground(C_DIM); }});
        legend.add(colorDot(C_RED));   legend.add(new JLabel("Booked")  {{ setFont(F_SMALL); setForeground(C_DIM); }});
        legend.add(colorDot(C_GOLD));  legend.add(new JLabel("Selected"){{ setFont(F_SMALL); setForeground(C_DIM); }});
        right.add(legend);

        twoCol.add(left); twoCol.add(right); wrap.add(twoCol); wrap.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0)); btnRow.setOpaque(false);
        JButton back = ghostButton("Go Back"); back.addActionListener(e -> showScreen("address"));
        JButton next = goldButton("Confirm Slot and Pay");
        next.addActionListener(e -> {
            if (selectedSlot.isEmpty()) { showMsg("Please select a time slot!"); return; }
            bookingDate = bookingDateField.getText().trim(); rebuildBookingPayPanel(); showScreen("bookingPay");
        });
        btnRow.add(back); btnRow.add(next); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(btnRow);

        slotMainPanel.add(new JScrollPane(wrap){{ setBorder(null); getViewport().setBackground(C_BG); }},BorderLayout.CENTER);
        slotMainPanel.revalidate(); slotMainPanel.repaint();
        loadSlots();
    }

    void loadSlots() {
        if (slotGridPanel==null) return;
        slotGridPanel.removeAll(); selectedSlot=""; selectedSlotLabel="";
        String date = bookingDateField.getText().trim();
        List<String> booked = DatabaseHelper.getBookedSlots(currentBranch!=null?currentBranch.getBranchId():1, date);
        ButtonGroup slotGroup = new ButtonGroup();
        for (int i=0; i<SLOT_IDS.length; i++) {
            final String sid=SLOT_IDS[i], slbl=SLOT_LABELS[i];
            boolean isBooked = booked.contains(sid);
            JToggleButton btn = new JToggleButton("<html><center>"+slbl+"<br><small>"+(isBooked?"Booked":"Available")+"</small></center></html>");
            btn.setFont(new Font("SansSerif",Font.PLAIN,12)); btn.setEnabled(!isBooked);
            btn.setBackground(isBooked?new Color(255,235,235):C_WHITE);
            btn.setForeground(isBooked?C_RED:C_TEXT);
            btn.setBorder(new LineBorder(isBooked?C_RED:C_BORDER,isBooked?2:1,true));
            btn.setFocusPainted(false);
            if (!isBooked) {
                btn.addActionListener(e -> {
                    selectedSlot=sid; selectedSlotLabel=slbl;
                    for (Component c : slotGridPanel.getComponents()) {
                        if (c instanceof JToggleButton && ((JToggleButton)c).isEnabled()) {
                            ((JToggleButton)c).setBackground(C_WHITE); ((JToggleButton)c).setBorder(new LineBorder(C_BORDER,1,true));
                        }
                    }
                    btn.setBackground(new Color(255,245,220)); btn.setBorder(new LineBorder(C_GOLD,2,true));
                });
                slotGroup.add(btn);
            }
            slotGridPanel.add(btn);
        }
        slotGridPanel.revalidate(); slotGridPanel.repaint();
    }

    // ══════════════════════════════════════════════════════════
    //  BOOKING PAYMENT PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildBookingPayPanel() {
        bookingPayMain = new JPanel(new BorderLayout()); bookingPayMain.setBackground(C_BG);
        return bookingPayMain;
    }

    void rebuildBookingPayPanel() {
        bookingPayMain.removeAll();
        JPanel wrap = new JPanel(); wrap.setBackground(C_BG);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(28,60,28,60));
        wrap.add(sectionTitle("Complete Booking"));
        wrap.add(sectionSub("Pay the reservation fee to secure your table"));
        wrap.add(Box.createVerticalStrut(20));

        JPanel twoCol = new JPanel(new GridLayout(1,2,28,0)); twoCol.setOpaque(false);

        JPanel left = new JPanel(); left.setBackground(C_WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(20,20,20,20)));
        left.add(new JLabel("Payment Method"){{ setFont(F_LABEL); setForeground(C_NAV); }});
        left.add(Box.createVerticalStrut(10));
        bpayMethodGroup = new ButtonGroup();
        String[][] methods={{"upi","UPI"},{"card","Card"},{"netbanking","Net Banking"},{"wallet","Wallet"}};
        for(String[] m:methods){JRadioButton rb=new JRadioButton(m[1]);rb.setActionCommand(m[0]);rb.setFont(F_BODY);rb.setOpaque(false);if(m[0].equals("upi"))rb.setSelected(true);bpayMethodGroup.add(rb);left.add(rb);left.add(Box.createVerticalStrut(4));}
        left.add(Box.createVerticalStrut(16));
        left.add(labeled("Contact Number *", bpayPhoneField=styledField("+91 98765 43210")));
        left.add(Box.createVerticalStrut(20));
        JButton payBtn = goldButton("Confirm Booking"); payBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        payBtn.addActionListener(e -> confirmBooking()); left.add(payBtn);

        JPanel right = new JPanel(); right.setBackground(C_WHITE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,1,true),BorderFactory.createEmptyBorder(20,20,20,20)));
        right.add(new JLabel("Booking Summary"){{ setFont(F_HEAD); setForeground(C_NAV); }});
        right.add(Box.createVerticalStrut(12));
        right.add(summaryRow("Branch", currentBranch!=null?currentBranch.getName():""));
        right.add(Box.createVerticalStrut(4));
        right.add(summaryRow("Date", bookingDateField!=null?bookingDateField.getText().trim():""));
        right.add(Box.createVerticalStrut(4));
        right.add(summaryRow("Slot", selectedSlotLabel));
        right.add(Box.createVerticalStrut(4));
        right.add(summaryRow("Guests", guestsCombo!=null?(String)guestsCombo.getSelectedItem():"2"));
        right.add(Box.createVerticalStrut(12)); addSeparator(right);
        right.add(summaryRow("Reservation Fee","Rs.500"));
        right.add(summaryRow("GST (18%)","Rs.90")); addSeparator(right);
        JPanel tr=summaryRow("TOTAL","Rs.590");
        ((JLabel)tr.getComponent(0)).setFont(F_BTN);
        ((JLabel)tr.getComponent(1)).setFont(new Font("SansSerif",Font.BOLD,16));
        ((JLabel)tr.getComponent(1)).setForeground(C_GOLD);
        right.add(tr);

        twoCol.add(left); twoCol.add(right); wrap.add(twoCol);
        JButton back=ghostButton("Back to Slots"); back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e->showScreen("slots")); wrap.add(Box.createVerticalStrut(16)); wrap.add(back);

        bookingPayMain.add(new JScrollPane(wrap){{ setBorder(null); getViewport().setBackground(C_BG); }},BorderLayout.CENTER);
        bookingPayMain.revalidate(); bookingPayMain.repaint();
    }

    void confirmBooking() {
        String phone = bpayPhoneField.getText().trim();
        if (phone.isEmpty() || phone.startsWith("+91 98")) { showMsg("Please enter your contact number!"); return; }
        lastBookingId = "BKG-" + (100000+new Random().nextInt(900000));
        int bid = currentBranch!=null?currentBranch.getBranchId():1;
        Booking booking = new Booking(lastBookingId, bid, "Customer", phone,
            bookingDate, selectedSlot, selectedSlotLabel,
            guestsCombo!=null?guestsCombo.getSelectedIndex()+1:2,
            specialReqField!=null?specialReqField.getText().trim():"",
            590, bpayMethodGroup.getSelection().getActionCommand());
        int id = DatabaseHelper.saveBooking(booking);
        if (id>0) DatabaseHelper.markSlotBooked(bid, bookingDate, selectedSlot, lastBookingId);
        rebuildBookingConfirm(phone); showScreen("bookingConfirm");
    }

    // ══════════════════════════════════════════════════════════
    //  BOOKING CONFIRMATION PANEL
    // ══════════════════════════════════════════════════════════
    JPanel buildBookingConfirmPanel() {
        bookingConfirmMain = new JPanel(new BorderLayout()); bookingConfirmMain.setBackground(C_BG);
        return bookingConfirmMain;
    }

    void rebuildBookingConfirm(String phone) {
        bookingConfirmMain.removeAll();
        JPanel wrap = new JPanel(); wrap.setBackground(C_BG);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(28,100,28,100));

        JPanel card = new JPanel(); card.setBackground(C_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,1,true),BorderFactory.createEmptyBorder(32,40,32,40)));
        card.setAlignmentX(Component.CENTER_ALIGNMENT); card.setMaximumSize(new Dimension(700,800));

        JLabel ico=new JLabel("Booking Confirmed!"); ico.setFont(new Font("SansSerif",Font.BOLD,22));
        ico.setForeground(C_NAV); ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub=new JLabel("Your table has been reserved. We look forward to hosting you!");
        sub.setFont(F_BODY); sub.setForeground(C_DIM); sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel idPanel=new JPanel(new FlowLayout(FlowLayout.CENTER)); idPanel.setOpaque(false);
        JLabel idLbl=new JLabel(lastBookingId);
        idLbl.setFont(new Font("Monospaced",Font.BOLD,22)); idLbl.setForeground(C_GOLD);
        idLbl.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,2,true),BorderFactory.createEmptyBorder(8,20,8,20)));
        idPanel.add(idLbl);
        card.add(ico); card.add(Box.createVerticalStrut(8)); card.add(sub);
        card.add(Box.createVerticalStrut(16)); card.add(idPanel); card.add(Box.createVerticalStrut(20));

        JPanel details = new JPanel(new GridLayout(0,2,12,8)); details.setOpaque(false);
        details.setMaximumSize(new Dimension(600,300));
        String bn = currentBranch!=null?currentBranch.getName():"";
        String ba = currentBranch!=null?currentBranch.getAddress():"";
        String[][] dets={{" Branch",bn},{"Address",ba},{"Date",bookingDate},{"Time Slot",selectedSlotLabel},
            {"Guests",guestsCombo!=null?(String)guestsCombo.getSelectedItem():"2"},{"Contact",phone},{"Amount Paid","Rs.590"},{"Status","CONFIRMED"}};
        for (String[] d : dets) {
            JPanel box=new JPanel(); box.setBackground(new Color(245,248,255));
            box.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(8,12,8,12)));
            box.setLayout(new BoxLayout(box,BoxLayout.Y_AXIS));
            box.add(new JLabel(d[0]){{ setFont(F_SMALL); setForeground(C_DIM); }});
            box.add(new JLabel(d[1]){{ setFont(F_LABEL); setForeground(C_TEXT); }});
            details.add(box);
        }
        card.add(details); card.add(Box.createVerticalStrut(20));

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0)); btns.setOpaque(false);
        JButton homeBtn=ghostButton("Back to Home"); homeBtn.addActionListener(e->goHome());
        JButton anotherBtn=goldButton("Book Another Table"); anotherBtn.addActionListener(e->{rebuildSlotPanel();showScreen("slots");});
        btns.add(homeBtn); btns.add(anotherBtn); btns.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btns); wrap.add(card);

        bookingConfirmMain.add(new JScrollPane(wrap){{ setBorder(null); getViewport().setBackground(C_BG); }},BorderLayout.CENTER);
        bookingConfirmMain.revalidate(); bookingConfirmMain.repaint();
    }

    // ══════════════════════════════════════════════════════════
    //  ADMIN LOGIN  (shown before Admin Panel is accessible)
    // ══════════════════════════════════════════════════════════
    void showAdminLogin() {
        // If already authenticated in this session, go straight in
        if (isAdminLoggedIn) { rebuildAdminPanel(); showScreen("admin"); return; }

        // ── Build a custom dialog ──────────────────────────────
        JDialog dlg = new JDialog(this, "Admin Login", true);
        dlg.setSize(420, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());

        // Top banner
        JPanel banner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(C_NAV); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_GOLD); g2.fillRect(0, getHeight()-3, getWidth(), 3);
            }
        };
        banner.setPreferredSize(new Dimension(0, 90));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        JLabel logoLbl = new JLabel("SAVORIA");
        logoLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        logoLbl.setForeground(C_GOLD2);
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Admin Portal");
        subLbl.setFont(new Font("SansSerif", Font.ITALIC, 13));
        subLbl.setForeground(new Color(180, 180, 200));
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(logoLbl); banner.add(Box.createVerticalStrut(4)); banner.add(subLbl);

        // Form area
        JPanel form = new JPanel();
        form.setBackground(C_WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        JLabel heading = new JLabel("Sign in to continue");
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(C_NAV);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(F_LABEL); userLabel.setForeground(C_NAV);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setFont(F_BODY);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        userField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(F_LABEL); passLabel.setForeground(C_NAV);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        passField.setFont(F_BODY);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        // Show/hide password checkbox
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(F_SMALL); showPass.setOpaque(false);
        showPass.setForeground(C_DIM); showPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPass.addActionListener(e ->
            passField.setEchoChar(showPass.isSelected() ? (char)0 : '*'));

        // Error label (hidden initially)
        JLabel errorLbl = new JLabel(" ");
        errorLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLbl.setForeground(C_RED);
        errorLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        JButton loginBtn = goldButton("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Hint label
        JLabel hintLbl = new JLabel("Default: admin / savoria@123");
        hintLbl.setFont(F_SMALL); hintLbl.setForeground(C_DIM);
        hintLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login action (shared by button and Enter key)
        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                errorLbl.setText("Please enter both username and password.");
                return;
            }
            errorLbl.setText("Verifying...");
            boolean ok = DatabaseHelper.verifyAdmin(username, password);
            if (ok) {
                isAdminLoggedIn = true;
                dlg.dispose();
                rebuildAdminPanel();
                showScreen("admin");
            } else {
                errorLbl.setText("Invalid username or password.");
                passField.setText("");
                passField.requestFocus();
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run()); // Enter key in password field
        userField.addActionListener(e -> passField.requestFocus()); // Enter key moves focus

        // Assemble form
        form.add(heading);
        form.add(Box.createVerticalStrut(22));
        form.add(userLabel); form.add(Box.createVerticalStrut(4)); form.add(userField);
        form.add(Box.createVerticalStrut(14));
        form.add(passLabel); form.add(Box.createVerticalStrut(4)); form.add(passField);
        form.add(Box.createVerticalStrut(6)); form.add(showPass);
        form.add(Box.createVerticalStrut(8)); form.add(errorLbl);
        form.add(Box.createVerticalStrut(14)); form.add(loginBtn);
        form.add(Box.createVerticalStrut(16)); form.add(hintLbl);

        root.add(banner, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        dlg.setContentPane(root);
        userField.requestFocusInWindow();
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════
    //  ADMIN PANEL  (Full CRUD: Create, Read, Update, Delete)
    // ══════════════════════════════════════════════════════════
    JPanel buildAdminPanel() {
        adminMain = new JPanel(new BorderLayout()); adminMain.setBackground(C_BG);
        return adminMain;
    }

    void rebuildAdminPanel() {
        adminMain.removeAll();

        // Header bar
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(C_ADMIN);
        header.setBorder(BorderFactory.createEmptyBorder(12,24,12,24));
        JLabel title = new JLabel("Admin Dashboard"); title.setFont(F_HEAD); title.setForeground(C_WHITE);
        JButton backBtn = adminButton("Back to App", C_GOLD);
        backBtn.setForeground(C_NAV);
        backBtn.addActionListener(e -> goHome());

        JButton logoutBtn = adminButton("Logout", C_RED);
        logoutBtn.addActionListener(e -> { isAdminLoggedIn = false; goHome(); });

        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerBtns.setOpaque(false); headerBtns.add(backBtn); headerBtns.add(logoutBtn);
        header.add(title, BorderLayout.WEST); header.add(headerBtns, BorderLayout.EAST);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(F_BTN);
        tabs.setBackground(Color.WHITE); tabs.setForeground(new Color(25,25,25));
        tabs.addTab("Orders", buildOrdersTab());
        tabs.addTab("Bookings", buildBookingsTab());
        tabs.addTab("Menu Items", buildMenuAdminTab());

        adminMain.add(header, BorderLayout.NORTH);
        adminMain.add(tabs, BorderLayout.CENTER);
        adminMain.revalidate(); adminMain.repaint();
    }

    /** ADMIN — Orders tab - Read, Update, Delete */
    JPanel buildOrdersTab() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        String[] cols = {"Order Code","Customer","Phone","Total","Payment","Status","Time"};
        List<String[]> rows = DatabaseHelper.getAllOrders(-1);

        // Demo fallback when DB is not connected
        if (rows.isEmpty()) {
            rows = new ArrayList<>();
            rows.add(new String[]{"ORD-100001","Rahul Sharma","+91-9876543210","Rs.755","UPI","DELIVERED","2025-01-01 12:00"});
            rows.add(new String[]{"ORD-100002","Priya Mehta","+91-8765432109","Rs.1050","Card","ON_THE_WAY","2025-01-01 13:00"});
            rows.add(new String[]{"ORD-100003","Aditya Singh","+91-7654321098","Rs.435","Cash","PREPARING","2025-01-01 14:00"});
        }

        Object[][] data = rows.toArray(new Object[0][]);

        JTable table = new JTable(new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(new Color(180,190,210),1));
        sp.getViewport().setBackground(Color.WHITE);
        sp.getViewport().setOpaque(true);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)); actions.setBackground(C_BG);

        JButton updateBtn = adminButton("Update Status", C_NAV);
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showMsg("Select an order first!"); return; }
            String code = (String)table.getValueAt(row, 0);
            String[] statuses = {"CONFIRMED","PREPARING","PACKED","ON_THE_WAY","DELIVERED"};
            String chosen = (String)JOptionPane.showInputDialog(this,"Select new status for "+code,"Update Status",
                JOptionPane.PLAIN_MESSAGE,null,statuses,"CONFIRMED");
            if (chosen!=null) {
                boolean ok = DatabaseHelper.updateOrderStatus(code, chosen);
                showMsg(ok ? "Status updated to: "+chosen : "Update failed");
                rebuildAdminPanel(); showScreen("admin");
            }
        });

        JButton deleteBtn = adminButton("Delete Order", C_RED);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showMsg("Select an order first!"); return; }
            String code = (String)table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,"Delete order "+code+"?","Confirm Delete",JOptionPane.YES_NO_OPTION);
            if (confirm==JOptionPane.YES_OPTION) {
                boolean ok = DatabaseHelper.deleteOrder(code);
                showMsg(ok ? "Order deleted!" : "Delete failed");
                rebuildAdminPanel(); showScreen("admin");
            }
        });

        JButton refreshBtn = adminButton("Refresh", new Color(39,174,96));
        refreshBtn.addActionListener(e -> { rebuildAdminPanel(); showScreen("admin"); });

        actions.add(updateBtn); actions.add(deleteBtn); actions.add(refreshBtn);

        String infoText = "Total orders: " + rows.size();
        JLabel info = new JLabel(infoText);
        info.setFont(F_BODY);
        info.setForeground(C_DIM);
        info.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

        p.add(info, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    /** ADMIN — Bookings tab - Read, Update, Delete */
    JPanel buildBookingsTab() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        String[] cols = {"Booking Code","Customer","Phone","Date","Slot","Guests","Amount","Status"};
        List<String[]> rows = DatabaseHelper.getAllBookings(-1);

        // Demo fallback so bookings are always visible even without DB
        if (rows.isEmpty()) {
            rows = new ArrayList<>();
            rows.add(new String[]{"BKG-200001","Kavya Reddy","+91-9765432108",
                java.time.LocalDate.now().toString(),"12:00 PM - 02:00 PM","4","590","CONFIRMED"});
            rows.add(new String[]{"BKG-200002","Nikhil Joshi","+91-8654321097",
                java.time.LocalDate.now().toString(),"06:00 PM - 08:00 PM","2","590","CONFIRMED"});
        }

        Object[][] data = rows.toArray(new Object[0][]);

        // Use DefaultTableModel (same as Orders tab) -- fixes blank/broken rendering
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(new Color(180,190,210),1));
        sp.getViewport().setBackground(Color.WHITE);
        sp.getViewport().setOpaque(true);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,10,8)); actions.setBackground(C_BG);

        JButton updateBtn = adminButton("Update Status", C_NAV);
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0){showMsg("Select a booking first!");return;}
            String code=(String)table.getValueAt(row,0);
            String[] statuses={"CONFIRMED","COMPLETED","CANCELLED"};
            String chosen=(String)JOptionPane.showInputDialog(this,"Select new status for "+code,"Update Status",
                JOptionPane.PLAIN_MESSAGE,null,statuses,"CONFIRMED");
            if(chosen!=null){boolean ok=DatabaseHelper.updateBookingStatus(code,chosen);showMsg(ok?"Status updated!":"Update failed");rebuildAdminPanel();showScreen("admin");}
        });

        JButton deleteBtn = adminButton("Delete Booking", C_RED);
        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0){showMsg("Select a booking first!");return;}
            String code=(String)table.getValueAt(row,0);
            int confirm=JOptionPane.showConfirmDialog(this,"Delete booking "+code+"?","Confirm Delete",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){boolean ok=DatabaseHelper.deleteBooking(code);showMsg(ok?"Booking deleted!":"Delete failed (demo mode)");rebuildAdminPanel();showScreen("admin");}
        });

        JButton refreshBtn = adminButton("Refresh", new Color(39,174,96));
        refreshBtn.addActionListener(e -> { rebuildAdminPanel(); showScreen("admin"); });

        actions.add(updateBtn); actions.add(deleteBtn); actions.add(refreshBtn);

        String infoText = "Total bookings: " + rows.size();
        JLabel info = new JLabel(infoText); info.setFont(F_BODY); info.setForeground(C_DIM);
        info.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

        p.add(info,BorderLayout.NORTH); p.add(sp,BorderLayout.CENTER); p.add(actions,BorderLayout.SOUTH);
        return p;
    }

    /** ADMIN — Menu Items tab (Read + Update price + Toggle availability) */
    JPanel buildMenuAdminTab() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // Branch selector
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT,12,4)); topBar.setBackground(C_BG);
        List<Branch> branches = DatabaseHelper.getAllBranches();
        if (branches.isEmpty()) branches.add(new Branch(1,"Delhi","Savoria Delhi","Connaught Place",4.8));
        JComboBox<Branch> branchCombo = new JComboBox<>(branches.toArray(new Branch[0]));
        branchCombo.setFont(F_BODY);
        topBar.add(new JLabel("Branch:"){{ setFont(F_LABEL); }});
        topBar.add(branchCombo);

        String[] cols = {"ID","Name","Category","Price","Veg","Available"};
        JTable table = new JTable(new Object[0][0], cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(table);
        JScrollPane sp = new JScrollPane(table); sp.setBorder(new LineBorder(new Color(180,190,210),1));
        sp.getViewport().setBackground(Color.WHITE);

        // Load items on branch change
        Runnable loadItems = () -> {
            Branch sel = (Branch)branchCombo.getSelectedItem();
            if(sel==null) return;
            List<MenuItem> items = DatabaseHelper.getAllMenuItems(sel.getBranchId());
            Object[][] data = new Object[items.size()][6];
            for(int i=0;i<items.size();i++){MenuItem it=items.get(i);
                data[i]=new Object[]{it.getItemId(),it.getName(),it.getCategory(),"Rs."+(int)it.getPrice(),it.isVeg()?"Veg":"Non-Veg",it.isAvailable()?"Yes":"No"};}
            table.setModel(new javax.swing.table.DefaultTableModel(data,cols){@Override public boolean isCellEditable(int r,int c){return false;}});
            styleTable(table);
        };
        branchCombo.addActionListener(e -> loadItems.run());
        loadItems.run();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,10,8)); actions.setBackground(C_BG);

        JButton priceBtn = adminButton("Update Price", C_NAV);
        priceBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0){showMsg("Select a menu item first!");return;}
            int itemId=(int)table.getValueAt(row,0); String name=(String)table.getValueAt(row,1);
            String input=JOptionPane.showInputDialog(this,"Enter new price for "+name+":","Update Price",JOptionPane.PLAIN_MESSAGE);
            if(input!=null && !input.isEmpty()) {
                try { double newPrice=Double.parseDouble(input.trim());
                    boolean ok=DatabaseHelper.updateMenuItemPrice(itemId,newPrice);
                    showMsg(ok?"Price updated to Rs."+newPrice:"Update failed");
                    loadItems.run();
                } catch(NumberFormatException ex){showMsg("Invalid price!");}
            }
        });

        JButton toggleBtn = adminButton("Toggle Availability", new Color(155,89,182));
        toggleBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0){showMsg("Select a menu item first!");return;}
            int itemId=(int)table.getValueAt(row,0);
            boolean currentlyAvail = "Yes".equals(table.getValueAt(row,5));
            boolean ok=DatabaseHelper.toggleMenuItemAvailability(itemId,!currentlyAvail);
            showMsg(ok?"Availability toggled!":"Update failed");
            loadItems.run();
        });

        actions.add(priceBtn); actions.add(toggleBtn);

        p.add(topBar,BorderLayout.NORTH); p.add(sp,BorderLayout.CENTER); p.add(actions,BorderLayout.SOUTH);
        return p;
    }

    void styleTable(JTable t) {
        t.setOpaque(true);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setForeground(new Color(25, 25, 25));
        t.setBackground(Color.WHITE);
        t.setRowHeight(34);
        t.setGridColor(new Color(200, 210, 230));
        t.setShowGrid(true);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(true);
        t.setIntercellSpacing(new Dimension(1, 1));
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(31, 56, 100));
        t.setSelectionForeground(Color.WHITE);
        // Header
        javax.swing.table.JTableHeader h = t.getTableHeader();
        h.setOpaque(true);
        h.setBackground(new Color(31, 56, 100));
        h.setForeground(Color.WHITE);
        h.setFont(new Font("SansSerif", Font.BOLD, 12));
        h.setPreferredSize(new Dimension(0, 38));
        h.setReorderingAllowed(false);
        h.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            { setHorizontalAlignment(JLabel.LEFT); setOpaque(true); }
            @Override public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                setText(val == null ? "" : val.toString());
                setBackground(new Color(31, 56, 100));
                setForeground(Color.WHITE);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(60, 90, 140)),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return this;
            }
        });
        // Row renderer
        t.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                setText(val == null ? "" : val.toString());
                setOpaque(true);
                setFont(new Font("SansSerif", Font.PLAIN, 13));
                if (sel) {
                    setBackground(new Color(31, 56, 100)); setForeground(Color.WHITE);
                } else if (row % 2 == 0) {
                    setBackground(Color.WHITE); setForeground(new Color(25, 25, 25));
                } else {
                    setBackground(new Color(238, 243, 252)); setForeground(new Color(25, 25, 25));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    // ══════════════════════════════════════════════════════════
    //  NAVIGATION
    // ══════════════════════════════════════════════════════════
    String currentScreen = "home";

    void showScreen(String name) {
        if (!name.equals(currentScreen)) {
            navHistory.push(currentScreen);
        }
        currentScreen = name;
        cardLayout.show(cardPanel, name);
        updateBackButton();
    }

    void goBack() {
        if (!navHistory.isEmpty()) {
            String prev = navHistory.pop();
            currentScreen = prev;
            cardLayout.show(cardPanel, prev);
            updateBackButton();
        }
    }

    void goHome() {
        cart.clear(); currentBranch = null;
        navHistory.clear();
        currentScreen = "home";
        cardLayout.show(cardPanel, "home");
        updateBackButton();
    }

    void updateBackButton() {
        if (backNavBtn != null) {
            // Hide on home and admin screens
            boolean show = !navHistory.isEmpty()
                && !currentScreen.equals("home")
                && !currentScreen.equals("admin");
            backNavBtn.setVisible(show);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  UI HELPERS  (Polymorphism via method overriding in anonymous classes)
    // ══════════════════════════════════════════════════════════
    JPanel scrollWrap(JPanel inner) {
        JPanel outer=new JPanel(new BorderLayout()); outer.setBackground(C_BG);
        JScrollPane sp=new JScrollPane(inner); sp.setBorder(null); sp.getViewport().setBackground(C_BG);
        outer.add(sp); return outer;
    }
    JLabel sectionTitle(String text) {
        JLabel l=new JLabel(text); l.setFont(new Font("Serif",Font.BOLD,28)); l.setForeground(C_NAV);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    JLabel sectionSub(String text) {
        JLabel l=new JLabel(text); l.setFont(F_BODY); l.setForeground(C_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    JTextField styledField(String placeholder) {
        JTextField f=new JTextField(); f.setFont(F_BODY);
        f.setPreferredSize(new Dimension(200,40)); f.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(6,11,6,11)));
        f.setForeground(C_DIM); f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if(f.getText().equals(placeholder)){f.setText("");f.setForeground(C_TEXT);}
                f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_GOLD,2,true),BorderFactory.createEmptyBorder(5,10,5,10)));
            }
            @Override public void focusLost(FocusEvent e) {
                if(f.getText().isEmpty()){f.setText(placeholder);f.setForeground(C_DIM);} 
                f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(C_BORDER,1,true),BorderFactory.createEmptyBorder(6,11,6,11)));
            }
        });
        return f;
    }
    JPanel labeled(String label, JComponent field) {
        JPanel p=new JPanel(); p.setOpaque(false); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JLabel l=new JLabel(label); l.setFont(F_LABEL); l.setForeground(C_NAV); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(l); p.add(Box.createVerticalStrut(4)); p.add(field);
        return p;
    }
    JButton goldButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isPressed() ? new Color(176, 133, 63)
                    : (getModel().isRollover() ? C_GOLD2 : C_GOLD);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(26, 38, 64));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(F_BTN);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(Math.max(144, text.length() * 8 + 44), 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    JButton ghostButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(240,240,245) : C_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(C_DIM);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(F_BODY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(Math.max(120, text.length() * 8 + 32), 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    JButton adminButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : (getModel().isRollover() ? bg.brighter() : bg));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(F_BTN);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(Math.max(126, text.length() * 8 + 34), 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void applyGlobalUI() {
        UIManager.put("TabbedPane.selected", C_WHITE);
        UIManager.put("TabbedPane.contentAreaColor", C_WHITE);
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));
        UIManager.put("TabbedPane.font", F_BTN);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("OptionPane.background", C_WHITE);
        UIManager.put("Panel.background", C_BG);
    }
    JButton qtyBtn(String text) {
        JButton b=new JButton(text); b.setFont(new Font("SansSerif",Font.BOLD,16));
        b.setPreferredSize(new Dimension(28,28)); b.setFocusPainted(false);
        b.setBackground(C_WHITE); b.setForeground(C_GOLD); b.setBorder(new LineBorder(C_GOLD,1,true));
        return b;
    }
    JPanel statusCard(boolean ok, String title, String desc) {
        JPanel card=new JPanel(); card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBackground(ok?new Color(39,174,96,20):new Color(192,57,43,20));
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ok?C_GREEN:C_RED,1,true),BorderFactory.createEmptyBorder(16,20,16,20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,150)); card.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t=new JLabel("<html>"+title+"</html>"); t.setFont(F_HEAD); t.setForeground(ok?C_GREEN:C_RED); t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel d=new JLabel("<html>"+desc.replace("\n","<br>")+"</html>"); d.setFont(F_BODY); d.setForeground(C_TEXT); d.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(t); card.add(Box.createVerticalStrut(6)); card.add(d);
        return card;
    }
    JPanel summaryRow(String key, String val) {
        JPanel row=new JPanel(new BorderLayout()); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
        JLabel k=new JLabel(key); k.setFont(F_BODY); k.setForeground(C_DIM);
        JLabel v=new JLabel(val); v.setFont(F_BODY); v.setForeground(C_TEXT);
        row.add(k,BorderLayout.WEST); row.add(v,BorderLayout.EAST); return row;
    }
    void addSeparator(JPanel p) {
        JSeparator sep=new JSeparator(); sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        p.add(Box.createVerticalStrut(6)); p.add(sep); p.add(Box.createVerticalStrut(6));
    }
    JPanel colorDot(Color c) {
        JPanel dot=new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c); g2.fillOval(0,0,12,12);
            }
        };
        dot.setPreferredSize(new Dimension(12,12)); dot.setOpaque(false); return dot;
    }
    void showMsg(String msg) { JOptionPane.showMessageDialog(this,msg,"Savoria",JOptionPane.INFORMATION_MESSAGE); }

    // ══════════════════════════════════════════════════════════
    //  MAIN ENTRY POINT
    // ══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SavoriaApp::new);
    }
}
