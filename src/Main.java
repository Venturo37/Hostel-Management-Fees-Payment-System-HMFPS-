
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.table.DefaultTableModel;


abstract class User{
    public static final LocalDate date = LocalDate.now();
    
    private String tp, name, password, email, role;
    private boolean is_approved;

    User(String tp, String name, String password, String email, String role, boolean is_approved) {
        this.tp = tp;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.is_approved = is_approved;

        // Only save the user if both their email and tp number are unique.
        if (!checkUniqueEmail(email) && !checkUniqueTP(tp)){
            saveUserToFile();
        }
    }
    
    User(String tp, String name, String password, String email, String role, boolean is_approved, boolean fromFile) {
        this.tp = tp;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.is_approved = is_approved;
    }
    
    public static boolean checkUniqueEmail(String email){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        for (List<String> user : users) {
            if (user.get(3).trim().equals(email)) {
                return true; // Email is not unique
            }
        }
        return false; // Email is unique
    }
    public static boolean checkUniqueTP(String tp){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        for (List<String> user : users) {
            String existingTp = user.get(0).trim();
            if (existingTp.equals(tp)) {
                return true; // TP is not unique
            }
        }
        return false; // TP is unique
    }
    
    private void saveUserToFile(){
        File file = new File("user_data.txt");
        if (file.exists()){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
                bw.write(this.tp + "," + this.name + "," +  this.password + "," + this.email + "," + this.role + "," + this.is_approved + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void viewApprovedUsers(DefaultTableModel userlist){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        boolean foundUsers = false;
        
        // Cycle through each user within the file
        for(List<String> user : users){
            boolean approvedUser = Boolean.parseBoolean(user.get(5).trim());
            // We only add to table if user is approved
            if (approvedUser) {
                String approvedTp = user.get(0).trim();
                String approvedName = user.get(1).trim();
                String approvedEmail = user.get(3).trim();
                String approvedRole = user.get(4).trim();
                userlist.addRow(new Object[]{approvedTp, approvedName, approvedEmail, approvedRole});
                foundUsers = true;
            }
        }
    }
    public void viewUserData(DefaultTableModel userlist, String updateTp) {
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        
        // Cycle through each user within the file
        for(List<String> user : users){
            String Tp = user.get(0).trim();
            // If the tp mactches, only then add it to the table
            if (Tp.equals(updateTp)){
                String name = user.get(1).trim();
                String password = user.get(2).trim();
                String email = user.get(3).trim();
                userlist.addRow(new Object[]{name, password, email});  
            }   
        }
    }
    public void showPaymentRecords(DefaultTableModel paymentlist) {
        Main.readPaymentRecords();
        List<List<String>> payments = Main.getPaymentRecords();
        
        // Cycle through each payment within the file
        for (List<String> payment : payments) {
            String userTP = payment.get(0).trim();
            String purpose = payment.get(1).trim();
            String refNum = payment.get(2).trim();
            String amount = payment.get(3).trim();
            String date = payment.get(4).trim();
            String status = payment.get(5).trim();
            paymentlist.addRow(new Object[]{userTP, purpose, refNum, amount, date, status});
        }
    }
    
    public static String updatePersonalInfo(String TP, String Name, String Password, String Email){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        
        boolean userFound = false;
        // First check if the email is unique
        if (checkUniqueEmail(Email)) {
            return "email exist";
        }
        // Cycle through each user within the system until it finds the matching tp
        for (List<String> user : users) {
            if (user.get(0).trim().equals(TP)) {
                userFound = true;
                // Update the user's information if data given is not empty (name, password, email)
                if (!Name.isEmpty()) {
                    user.set(1, Name);
                }
                if (!Password.isEmpty()) {
                    user.set(2, Password);
                }
                if (!Email.isEmpty()) {
                    user.set(3, Email);
                }
            }
        }
        
        if (userFound) {
            Main.saveUserData();
            return "update success";
        } else {
            return "update error";
        }
    }
    
    public List<String> generateReceipt(String reference) {
        Main.readPaymentRecords();
        List<List<String>> paymentRecordsList = Main.getPaymentRecords();
        
        // Cycle through each payment record within the system until matching reference number is found
        for (List<String> entry : paymentRecordsList) {
            if (entry.get(2).equals(reference)) {
                List<String> receipt = new ArrayList<>();
                receipt.add(entry.get(0));
                receipt.add(entry.get(1));
                receipt.add(entry.get(2));
                receipt.add(entry.get(3));
                receipt.add(entry.get(4));
                receipt.add(entry.get(5));
                return receipt;
            }
        }
        return null;
    }
    public static String checkApprovedUser(String usertp){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        boolean userFound = false;
        boolean userApproved = false;
        
        // Cycle through each user within the system until tp matches
        for (List<String> user : users){
            String tp = user.get(0).trim();
            boolean isApproved = Boolean.parseBoolean(user.get(5).trim());
            
            // If found, set userFound flag to true and determine its approval status
            if (tp.equals(usertp)) {
                userFound = true;
                userApproved = isApproved;
                break;
            }
        }
        if (!userFound){
            return "user not found";
        } else if (!userApproved){
            return "user not approved";
        } else {
            return "user found and approved";
        }
    }    
    
    public String getTP() {
        return tp;
    }
    public String getName() {
        return name;
    }
}

class Manager extends User{
    Manager(String tp,String name, String password, String email) {
        super(tp, name, password, email, "manager", false);
    }
    Manager(String tp, String name, String password, String email, boolean is_approved) {
        super(tp, name, password, email, "manager", true, is_approved);
    }
    
    public void viewUser(DefaultTableModel userlist){
        Main.readUserData(); 
        List<List<String>> users = Main.getUserData();
        
        for(List<String> user : users){ 
            String tp = user.get(0).trim();
            String name = user.get(1).trim();
            String email = user.get(3).trim();
            String role = user.get(4).trim();
            boolean isApproved = Boolean.parseBoolean(user.get(5).trim());
            userlist.addRow(new Object[]{tp, name, email, role, isApproved ? "Approved" : "Unapproved"});
        }
    }
    public void viewUnapprovedUsers(DefaultTableModel userlist){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();

        for(List<String> user : users){
            boolean isApproved = Boolean.parseBoolean(user.get(5).trim());
            if (!isApproved) {
                String tp = user.get(0).trim();
                String name = user.get(1).trim();
                String email = user.get(3).trim();
                String role = user.get(4).trim();
                userlist.addRow(new Object[]{tp, name, email, role});
            }
        }
    }
    public boolean searchRoomOrUser(DefaultTableModel list, DefaultTableModel filteredModel, String searchItem){
        boolean foundStatus = false;
        
        for (int i = 0; i < list.getRowCount(); i++){
            String tp = list.getValueAt(i, 0).toString().trim();
            if (tp.equals(searchItem)){
                String col_1 = list.getValueAt(i, 0).toString();
                String col_2 = list.getValueAt(i, 1).toString();
                String col_3 = list.getValueAt(i, 2).toString();
                String col_4 = list.getValueAt(i, 3).toString();
                String col_5 = list.getValueAt(i, 4).toString();
                
                filteredModel.addRow(new Object[]{col_1, col_2, col_3, col_4, col_5});
                foundStatus = true;
                break;
            }
        }
        return foundStatus;
    }

    // Methods under USER MANAGEMENT
    public boolean approveUser(String userTP){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        boolean userFound = false;
        
        // Search for the user by their TP Number
        for(List<String> user : users){
            String tp = user.get(0).trim();
            if(tp.equals(userTP)){
                // Mark the user as approved
                user.set(5, "true");
                userFound = true;
            }
        }
        
        if (userFound){
            Main.saveUserData();
            return true;
        } else {
            return false; // Return false if user does not exist
        }
    }
    
    public String updateUser(String updateTP, String updateName,String updateEmail, String updateRole){
        Main.readUserData();
        List<List<String>> users = Main.getUserData();
        boolean userFound = false;
        if (checkUniqueEmail(updateEmail)) {
            return "email exist";
        }
        // Search for the user by their TP number and check if they are approved
        for (List<String> user : users) {
            String tp = user.get(0).trim();
            boolean approved = Boolean.parseBoolean(user.get(5).trim());
            if (tp.equals(updateTP) && approved){
                userFound = true;
                
                // Update the user's name if a new name is provided
                if (!updateName.isEmpty()) {
                    user.set(1, updateName);
                }
                
                // Update the user's email only if a valid and unique email is provided
                if (!updateEmail.isEmpty()) {
                    user.set(3, updateEmail);
                }
                
                // Similarly, update the user's role if a valid role is provided
                if (!updateRole.isEmpty()) {
                    String new_updateRole = updateRole.toLowerCase();
                    if (!new_updateRole.equals("resident") && !new_updateRole.equals("staff") && !new_updateRole.equals("manager")) {
                        return "invalid role";
                    }
                    user.set(4, new_updateRole);
                }
            }
        }
        if (userFound) {
            Main.saveUserData();
            return "update success";
        } else {
            return "no approved user found";
        }
    }
    public String deleteUser(String deleteTP){
        // Load user and room data
        Main.readUserData();
        Main.readRoomData();
        List<List<String>> users = Main.getUserData();
        List<List<String>> rooms = Main.getRoomData();

        boolean userInRoom = false;
        
        // Check if the user is currently occuping any room
        for (List<String> room : rooms){
            // Get the list of occupants in the room
            List<String> occupantsTp = room.get(4).equals("null") ? 
            new ArrayList<>() : new ArrayList<>(Arrays.asList(room.get(4).trim().split(";")));

            if (occupantsTp.contains(deleteTP)){
                userInRoom = true;
                break; // Exit the loop if user is found in the room
            }
        }
        
        // If the user is occupying a room, cancel deletion attempt
        if (userInRoom){
            return "user occupied room";
        }
        
        // Check if the user status is approved for deletion
        String approveResult = checkApprovedUser(deleteTP);

        if (approveResult.equals("user found and approved") || approveResult.equals("user not approved")){
            for (List<String> user : users){
                if (user.get(0).trim().equals(deleteTP)){
                    users.remove(user);
                    break; // Exit the loop after removing the user
                }
            }
            Main.saveUserData();
            return "user deleted";
//        } else if (approveResult.equals("user not approved")) {
//            // User exists but is not approved for deletion
//            return "user not approved";
        } else {
            return "user not found";
        }
    }

    // Methods under ROOM MANAGEMENT
    public String addNewRoom(String roomFeeTXT, String roomType){
        double roomFee;
        
        // First validate room fee input by parsing
        try {
            roomFee = Double.parseDouble(roomFeeTXT);
            if (roomFee <= 0){
                throw new NumberFormatException("Fee must be positive");
            }
        } catch (NumberFormatException e){
            return "invalid fee"; // Return if fee is not valid
        }
        
        // Validate room type input
        if (!roomType.equals("single") && !roomType.equals("double") && !roomType.equals("quadruple")) {
            return "invalid roomtype";
        }

        // Create a new room object based on the given specifications
        Room newRoom = switch(roomType){
            case "single" -> new SingleRoom(roomFee);
            case "double" -> new DoubleRoom(roomFee);
            case "quadruple" -> new QuadRoom(roomFee);
            default -> throw new IllegalArgumentException("Invalid room type");
        };
        return "room added";
    }
    public String updateFeeRate(String feeRateTXT, String updateRoomID){
        double feeRate;
        
        // First validate room fee input by parsing
        try {
            feeRate = Double.parseDouble(feeRateTXT);
            if (feeRate <= 0){
                throw new NumberFormatException("Fee must be positive");
            }
        } catch (NumberFormatException e){
            return "invalid fee";
        }
        
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        boolean roomFound = false;

        // Search for the room by its ID and update its fee rate
        for (List<String> room : rooms) {
            String roomID = room.get(0).trim();
            if (roomID.equals(updateRoomID)) {
                roomFound = true;
                room.set(2, String.valueOf(feeRate));
            }
        }
        
        // Save updated room data if room was found
        if (roomFound) {
            Main.saveRoomData();
            return "fee updated";
        } else {
            return "no room found"; // Otherwise, return if no matching room was found
        }
    }
    public String deleteRoom(String deleteRoomID){
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        boolean roomFound = false;
        boolean roomDelete = false;

        // Cycle through each room and check for occupants using the given ID
        for (List<String> room : rooms) {
            String roomID = room.get(0).trim();
            if (roomID.equals(deleteRoomID)) {
                roomFound = true;
                String occupantsTp = room.get(4).trim();
                
                // Check if the room has anyone inside
                if (!occupantsTp.equals("null") && !occupantsTp.isEmpty()){
                    return "occupant present"; // Cancel deletion if occupants are present
                }
                roomDelete = true;
                rooms.remove(room); // Remove the room since it is confirmed empty
                break;
            }
        }
        
        if (!roomFound) {
            return "no room found";
        } else if (!roomDelete) {
            return "cancel room delete";
        } else {
            Main.saveRoomData();
            return "room delete success";
        }
    }
    
    public void viewActivityLog(DefaultTableModel activityLogs) {
        Main.readActivityLog();
        List<List<String>> activityLog = Main.getActivityLog();
        
        for (List<String> user : activityLog) {
            activityLogs.addRow(new Object[]{user.get(0), user.get(1), user.get(2)});
        }
       
    }
}

class Staff extends User{
    Staff(String tp, String name, String password, String email) {
        super(tp, name, password, email, "staff", false);
    }
    Staff(String tp, String name, String password, String email, boolean is_approved) {
        super(tp, name, password, email, "staff", true, is_approved);
    }
    
    public void showPendingPaymentRecords(DefaultTableModel paymentlist) {
        Main.readPaymentRecords();
        List<List<String>> payments = Main.getPaymentRecords();
        
        for (List<String> payment : payments) {
            String status = payment.get(5).trim();
            if (status.equals("Pending")) {
                String userTP = payment.get(0).trim();
                String purpose = payment.get(1).trim();
                String refNum = payment.get(2).trim();
                String amount = payment.get(3).trim();
                String date = payment.get(4).trim();
                paymentlist.addRow(new Object[]{userTP, purpose, refNum, amount, date, status});
            }
        }
    }
    public String confirmPayment(String reference) {
        Main.readPaymentRecords();
        List<List<String>> paymentRecordsList = Main.getPaymentRecords();
        
        // Cycle through each entry within the list
        for (List<String> entry : paymentRecordsList) {
            // Proceed if reference code is found
            if (entry.get(2).equals(reference)) {
                if ("Confirmed".equals(entry.get(5))) {
                    return "payment already confirmed"; 
                } else {
                    entry.set(5, "Confirmed");
                    Main.savePaymentRecords();
                    return "payment confirmed";
                }
            } 
        }
        return "payment not found"; // Return if reference code/payment is not found
    }   
    
    public boolean checkReferenceinPayment(String reference) {
        Main.readPaymentRecords();
        List<List<String>> payments = Main.getPaymentRecords();

        
        for (List<String> entry : payments) {
            if (entry.get(2).trim().equals(reference)){
                
                return true;
            }
        }
        return false;
    }
}

class Resident extends User{
    Resident(String tp, String name, String password, String email) {
        super(tp, name, password, email, "resident", false);
    }
    Resident(String tp, String name, String password, String email, boolean is_approved) {
        super(tp, name, password, email, "resident", true, is_approved);
    }
    
    public void viewRoomRate(DefaultTableModel roomlist, String residentTP){
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        
        // Cycle through each room until it correctly matches the given tp
        for (List<String> room : rooms) {
            if (room.get(4).contains(residentTP)) {
                String roomID = room.get(0).trim();
                String roomType = room.get(1).trim();
                String roomFee = room.get(2).trim();
                boolean isFullyOccupied = Boolean.parseBoolean(room.get(3).trim());
                String occupantTP = room.get(4).trim();
                
                if (occupantTP.equals("null")){
                    occupantTP = "No occupants";
                }
                
                roomlist.addRow(new Object[]{
                    roomID, 
                    roomType, 
                    roomFee,
                    isFullyOccupied ? "Full" : "Not Full",
                    occupantTP
                });
            }
        }
    }
    public void showPaymentRecords(DefaultTableModel paymentlist, String residentTP) {
        Main.readPaymentRecords();
        List<List<String>> payments = Main.getPaymentRecords();
        
        // Cycle through each payment until it correctly matches the given tp
        for (List<String> payment : payments) {
            if (payment.get(0).equals(residentTP)) {
                String userTP = payment.get(0).trim();
                String purpose = payment.get(1).trim();
                String refNum = payment.get(2).trim();
                String amount = payment.get(3).trim();
                String date = payment.get(4).trim();
                String status = payment.get(5).trim();
                
                // Add payment record details to table model
                paymentlist.addRow(new Object[]{userTP, purpose, refNum, amount, date, status});
            }
        }
    }
    public boolean checkResidentTPinPayment(String reference, String userTP) {
        Main.readPaymentRecords();
        List<List<String>> payments = Main.getPaymentRecords();

        // Cycle through each entry within the payments list until reference matches
        for (List<String> entry : payments) {
            if (entry.get(2).trim().equals(reference)){
                // Returns true if TP matches, false otherwise
                return entry.get(0).equals(userTP);
            }
        }
        return false;
    }
    public void makePayment(String reference, double amount) {
        Main.readPaymentRecords();
        List<List<String>> paymentRecordsList = Main.getPaymentRecords();
        List<String> newList = Arrays.asList(this.getTP(), "rental fee", reference, String.valueOf(amount), date.toString(), "Pending");
        paymentRecordsList.add(newList);
        Main.savePaymentRecords();
    }
}

class Room{
    private final String roomID, roomType;
    private final double roomFee;
    private final boolean isFullyOccupied;
    private final int maxOccupants;
    private final List<String> occupantsTp;

    Room(String roomType, double roomFee, int maxOccupants){
        this.roomID = setID();
        this.roomType = roomType;
        this.roomFee = roomFee;
        this.isFullyOccupied = false;
        this.maxOccupants = maxOccupants;
        this.occupantsTp = new ArrayList<>();
        saveRoomToFile();
    }
    
    private String getLastID() {
        String lastID = "R000"; 
        try (BufferedReader br = new BufferedReader(new FileReader("room_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lastID = line.split(",")[0]; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastID;
    }
    
    private String setID() {
        String lastID = getLastID();
        int newID = Integer.parseInt(lastID.substring(1)) + 1; 
        return "R" + String.format("%03d", newID); 
    }
       
    public static void viewRoomDetails(DefaultTableModel roomlist){
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        
        for (List<String> room : rooms) {
            String room_ID = room.get(0).trim();
            String room_Type = room.get(1).trim();
            String room_Fee = room.get(2).trim();
            boolean is_FullyOccupied = Boolean.parseBoolean(room.get(3).trim());
            String occupant_Tp = room.get(4).trim();
            
            if (occupant_Tp.equals("null")){
                occupant_Tp = "No occupants";
            }
            roomlist.addRow(new Object[]{
                room_ID, 
                room_Type, 
                room_Fee, 
                is_FullyOccupied ? "Full" : "Not Full", 
                occupant_Tp
            });

        }
    }
    public static void viewAvailableRooms(DefaultTableModel roomlist){
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        
        for (List<String> room : rooms) {
            String roomID = room.get(0).trim();
            String roomType = room.get(1).trim();
            double roomFee = Double.parseDouble(room.get(2).trim());
            boolean isFullyOccupied = Boolean.parseBoolean(room.get(3).trim());
            List<String> occupantsTp = room.get(4).equals("null") ? 
                new ArrayList<>() : 
                new ArrayList<>(Arrays.asList(room.get(4).trim().split(";")));
        
            if (!isFullyOccupied){
                roomlist.addRow(new Object[]{roomID, roomType, roomFee,
                    (isFullyOccupied ? "Full" : "Not Full"),
                    (occupantsTp.isEmpty() ? "None" : String.join(";", occupantsTp))});
            }                
        }
    }
    public static void viewRoomsWithOccupants(DefaultTableModel roomlist){
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        
        for (List<String> room : rooms){
            String roomID = room.get(0).trim();
            String roomType = room.get(1).trim();
            double roomFee = Double.parseDouble(room.get(2).trim());
            List<String> occupantsTp = room.get(4).equals("null") ? new ArrayList<>() : new ArrayList<>(Arrays.asList(room.get(4).trim().split(";")));
            if (!occupantsTp.isEmpty()){
                roomlist.addRow(new Object[]{roomID, roomType, roomFee,
                    (occupantsTp.isEmpty() ? "None" : String.join(";", occupantsTp))});
            }
        }
    }

    public static String assignUsertoRoom(String assignTP, String assignroomID){
        Main.readUserData();
        Main.readRoomData();
        List<List<String>> users = Main.getUserData();
        List<List<String>> rooms = Main.getRoomData();
        boolean roomFound = false;

        // Cycle through each room to find the matching room
        for (List<String> room : rooms){
            String roomID = room.get(0).trim();
            String roomType = room.get(1).trim();
            boolean isFullyOccupied = Boolean.parseBoolean(room.get(3).trim());
            List<String> occupantsTp = room.get(4).
            equals("null") ? new ArrayList<>() : 
            new ArrayList<>(Arrays.asList(room.get(4).trim().split(";")));
            
            // Determine max number of occupants based on room type
            int maxOccupants = switch(roomType) {
                case "single" -> 1;
                case "double" -> 2;
                case "quadruple" -> 4;
                default -> 0;
            };
            
            // We add this to prevent managers and staff from occupying a room.
            for (List<String> user : users){
                String tp = user.get(0).trim();
                String role = user.get(4).trim();
                if (tp.equals(assignTP)){
                    if (role.equals("manager") || role.equals("staff")) {
                        return "not resident";
                    }
                }
            }                    
            
            // Proceed if room ID matches
            if (roomID.equals(assignroomID)){
                roomFound = true;
            
                if (isFullyOccupied){
                    return "room full";
                } else {
                    if (occupantsTp.size() < maxOccupants){
                        // Checks if the user is already in the room
                        if (occupantsTp.contains(assignTP)){
                            return "user in room";
                        } else {
                            // Verify whether the user is approved before assigning
                            String approveResult = User.checkApprovedUser(assignTP);

                            if (approveResult.equals("user found and approved")){
                                occupantsTp.add(assignTP);
                                if (occupantsTp.size() == maxOccupants){
                                    isFullyOccupied = true;
                                }
                                room.set(3, String.valueOf(isFullyOccupied));
                                room.set(4, String.join(";", occupantsTp));
                            } else if (approveResult.equals("user not approved")){
                                return "user not approve";
                            } else {
                                return "user not found";
                            }
                        }
                    }
                }
            }
        }
        if (!roomFound){
            return "room not found";
        } else {
            Main.saveRoomData();
            return "user assigned";
        }
    }

    public static String removeUserFromRoom(String removeUserTP, String removeUserRoomID){
        Main.readRoomData();
        List<List<String>> rooms = Main.getRoomData();
        boolean roomFound = false;
        
        // Cycle through each room until it matches
        for (List<String> room : rooms){
            String roomID = room.get(0).trim();
            String roomType = room.get(1).trim();
            boolean isFullyOccupied = Boolean.parseBoolean(room.get(3).trim());
            List<String> occupantsTp = room.get(4).
            equals("null") ? new ArrayList<>() : 
            new ArrayList<>(Arrays.asList(room.get(4).trim().split(";")));

            if (roomID.equals(removeUserRoomID)){
                roomFound = true;
                if (occupantsTp.isEmpty()){
                    return "no users";
                } else {
                    // Check if user is in the room and then removes them
                    if (occupantsTp.contains(removeUserTP)){
                        occupantsTp.remove(removeUserTP);
                        
                        // Check if room is fully occupied
                        isFullyOccupied = (occupantsTp.size() == (switch (roomType){
                            case "single" -> 1;
                            case "double" -> 2;
                            case "quadruple" -> 4;
                            default -> 0;
                        }));
                        room.set(3, String.valueOf(isFullyOccupied));
                        String occupants = occupantsTp.isEmpty() ? "null" : String.join(";", occupantsTp);
                        room.set(4, occupants);
                        
                    } else { 
                        return "user not found";
                    }
                }
            }
        }
        
        if (!roomFound){
            return "room not found";
        } else {
            Main.saveRoomData();
            return "user removed";
        }
    }

    public final void saveRoomToFile(){
        File file = new File("room_data.txt");
        if (file.exists()){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
                String occupants = this.occupantsTp.isEmpty() ? "null" : String.join(";", this.occupantsTp);
                bw.write(this.roomID + "," + this.roomType  + "," +  this.roomFee + "," + this.isFullyOccupied  + "," + occupants + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class SingleRoom extends Room{
    SingleRoom(double roomFee){
        super("single", roomFee, 1);
    }
}

class DoubleRoom extends Room{
    DoubleRoom(double roomFee){
        super("double", roomFee, 2);
    }
}

class QuadRoom extends Room{
    QuadRoom(double roomFee){
        super("quadruple", roomFee, 4);
    }
}

public class Main{
    private static final List<List<String>> userData = new ArrayList<>();
    private static final List<List<String>> activityLog = new ArrayList<>();
    private static final List<List<String>> paymentRecordsList = new ArrayList<>();
    private static final List<List<String>> roomDetails = new ArrayList<>();

    public static String signUp(String tp, String name, String password, String email, String role) {
        // First check whether the user already exists within the system
        if (User.checkUniqueTP(tp) || User.checkUniqueEmail(email)) {
            return "user exist";
        }

        User newUser;
        // Based on the role, create the corresponding user object
        switch (role.toLowerCase()) {
            case "resident" -> newUser = new Resident(tp, name, password, email);
            case "staff" -> newUser = new Staff(tp, name, password, email);
            case "manager" -> newUser = new Manager(tp, name, password, email);
            default -> {
                return "invalid role";
            }
        }
        return "register success";
    }     
    public static User logIn(String tp, String password) {
        File userFile = new File("user_data.txt");

        // Check if user file exists
        if (!userFile.exists()){
            return null;
        }
        
        // Cycle through each line within the file
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            boolean isLoggedIn = false;

            while ((line = br.readLine()) != null) {
                String[] userDetails = line.split(",");
                String inlineTp = userDetails[0].trim();
                String inlineName = userDetails[1].trim();
                String inlinePassword = userDetails[2].trim();
                String inlineRole = userDetails[4].trim().toLowerCase();
                boolean is_approved = Boolean.parseBoolean(userDetails[5].trim());
                
                // Check if the provided TP and password match the data
                if (inlineTp.equals(tp) && inlinePassword.equals(password)) {
                    if (!is_approved){
                        return null; // Return if user is not approved yet
                    }

                    isLoggedIn = true;
                    logActivity(inlineTp);
                    
                    // Based on the role, return the corresponding user object
                    return switch (inlineRole) {
                        case "resident" -> new Resident(inlineTp, inlineName, inlinePassword, userDetails[3], is_approved);
                        case "staff" -> new Staff(inlineTp, inlineName, inlinePassword, userDetails[3], is_approved);
                        case "manager" -> new Manager(inlineTp, inlineName, inlinePassword, userDetails[3], is_approved);
                        default -> throw new IllegalArgumentException("Invalid role found in user_data.txt");
                    };         
                    
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static void readActivityLog() {
        try (BufferedReader br = new BufferedReader(new FileReader("user_log.txt"))) {
            activityLog.clear();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> details = Arrays.asList(line.split(","));
                activityLog.add(new ArrayList<>(details));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void logActivity(String tp) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("user_log.txt", true))) {
            bw.write(LocalDate.now() + "," + tp + ",Logged In");
            bw.newLine();
        } catch (IOException e) { 
            e.printStackTrace();
        }
    }
    
    public static List<List<String>> getActivityLog() {
        return activityLog;
    }
    
    
    public static void readRoomData() {
        try (BufferedReader br = new BufferedReader(new FileReader("room_data.txt"))) {
            roomDetails.clear();
            String line;
            
            while ((line = br.readLine()) != null) {
                List<String> details = Arrays.asList(line.split(","));
                roomDetails.add(new ArrayList<>(details));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveRoomData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("room_data.txt"))) {
            for (List<String> room : roomDetails) {
                bw.write(room.get(0) + "," + room.get(1) + "," + room.get(2) + "," + room.get(3) + "," + room.get(4));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> getRoomData() {
        return roomDetails;
    } 
    
    public static void readUserData() {
        try (BufferedReader br = new BufferedReader(new FileReader("user_data.txt"))) {
            userData.clear();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> user = Arrays.asList(line.split(","));
                userData.add(new ArrayList<>(user));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveUserData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("user_data.txt"))) {
            for (List<String> user : userData) {
                bw.write(user.get(0) + "," + user.get(1) + "," + user.get(2) + "," + user.get(3) + "," + user.get(4) + "," + user.get(5));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<List<String>> getUserData() {
        return userData;
    }
    
    public static void readPaymentRecords() {
        try (BufferedReader br = new BufferedReader(new FileReader("payment_records.txt"))) {
            paymentRecordsList.clear();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> record = Arrays.asList(line.split(","));
                paymentRecordsList.add(new ArrayList<>(record));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void savePaymentRecords() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("payment_records.txt"))) {
            for (List<String> list : paymentRecordsList) {
                bw.write(list.get(0) + "," + list.get(1) + "," + list.get(2) + "," + list.get(3) + "," + list.get(4) + "," + list.get(5));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<List<String>> getPaymentRecords() {
        return paymentRecordsList;
    }
}
