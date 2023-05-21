package com.example._9_laboras;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> firstName;

    @FXML
    private TableColumn<User, String> lastName;

    @FXML
    private TableColumn<User, String> email;

    @FXML
    private TableColumn<User, String> imageLink;

    @FXML
    private TableColumn<User, String> ip_address;

    @FXML
    private ChoiceBox<String> chooseFilter;

    @FXML
    private ChoiceBox<String> filteredLists;

    @FXML
    private ChoiceBox<String> columnChoices;

    @FXML
    private Label recordAmount;

    @FXML
    private TextField filterInput;

    private List<User> userData;

    private ObservableList<User> observableList;

    private ObservableList<String> choiceList;

    private ObservableList<String> filterList;

    private ObservableList<String> recordList;

    private int index = 0;

    private Map<String, List<User>> ipMap = new HashMap<>();

    private final static Path path = Paths.get("C:/Java/9_laboras/MOCK_DATA.csv");

    private final static HashMap<String, Integer> choiceMap = new HashMap<>();

    private final static HashMap<String, Integer> filterMap = new HashMap<>();

    private final static HashMap<String, Integer> recordMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        firstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        imageLink.setCellValueFactory(new PropertyValueFactory<User, String>("imagelink"));
        ip_address.setCellValueFactory(new PropertyValueFactory<User, String>("ip_address"));

        userData = new ArrayList<>();
        observableList = FXCollections.observableArrayList();

        userTable.setItems(observableList);
        loadInitialData();

        choiceMap.put("Names-A to Z", 1);
        choiceMap.put("Surnames-A to Z", 2);
        choiceMap.put("Emails-A to Z", 3);

        choiceList = FXCollections.observableArrayList(choiceMap.keySet());
        chooseFilter.setItems(choiceList);
        chooseFilter.setOnAction(this::onSortMethodChosen);

        filterMap.put("Names- Starts With", 1);
        filterMap.put("Surnames - Starts With", 2);
        filterMap.put("Emails - Starts With", 3);
        filterMap.put("ImageLink - Contains", 4);
        filterMap.put("IpAddress - Contains", 5);

        filterList = FXCollections.observableArrayList(filterMap.keySet());
        columnChoices.setItems(filterList);
        columnChoices.setOnAction(this::onFilterChosen);

        recordList = FXCollections.observableArrayList(recordMap.keySet());
        filteredLists.setItems(recordList);
    }

    private void onFilterChosen(ActionEvent actionEvent) {
        String selectedChoice = columnChoices.getValue();
        int selectedValue = filterMap.get(selectedChoice);
        String filterType = filterInput.getText();

        List<User> filterUsers = new ArrayList<>(userData);

        if (filterType != null && !filterType.isEmpty()) {
            switch (selectedValue) {
                case 1:
                    filterUsers = filterUsers.stream()
                            .filter(user -> user.getFirstName().startsWith(filterType))
                            .toList();
                    recordMap.put("Names", index++);
                    break;
                case 2:
                    filterUsers = filterUsers.stream()
                            .filter(user -> user.getLastName().startsWith(filterType))
                            .toList();
                    recordMap.put("Surnames", index++);
                    break;
                case 3:
                    filterUsers = filterUsers.stream()
                            .filter(user -> user.getEmail().startsWith(filterType))
                            .toList();
                    recordMap.put("Email", index++);
                    break;
                case 4:
                    filterUsers = filterUsers.stream()
                            .filter(user -> user.getImagelink().contains(filterType))
                            .toList();
                    recordMap.put("Imagelink", index++);
                    break;
                case 5:
                    filterUsers = filterUsers.stream()
                            .filter(user -> user.getIp_address().contains(filterType))
                            .toList();
                    recordMap.put("IpAddress", index++);
                    break;
            }
            recordList = FXCollections.observableArrayList(recordMap.keySet());
            filteredLists.setItems(recordList);
            recordAmount.setText(index + " ");
            observableList = FXCollections.observableArrayList(filterUsers);
            userTable.setItems(observableList);
        }
    }
    public void loadInitialData()
    {
        Thread loadData = new Thread(() ->
        {
            try{
                BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
                String line;
                boolean isFirstLine = true;
                while((line = reader.readLine()) != null)
                {
                    if(isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[]columns = line.split(";");

                    User user = new User();
                    user.setFirstName(columns[0]);
                    user.setLastName(columns[1]);
                    user.setEmail(columns[2]);
                    user.setImagelink(columns[3]);
                    user.setIp_address(columns[4]);
                    user.setId(userData.size());

                    synchronized (userData) {
                        userData.add(user);
                    }

                    Platform.runLater(() -> observableList.add(user));

                    Thread.sleep(5);
                }
            }catch(IOException | InterruptedException e)
            {
                e.printStackTrace();
            }

        });
        loadData.start();
    }

    @FXML
    public void onSortMethodChosen(ActionEvent event)
    {
        String selectedChoice = chooseFilter.getValue();
        int selectedValue = choiceMap.get(selectedChoice);

        List<User> filterUsers;

        switch(selectedValue)
        {
            case 1:
                filterUsers = userData.stream().
                        sorted(Comparator.comparing(User::getFirstName)).toList();
                observableList = FXCollections.observableArrayList(filterUsers);
                userTable.setItems(observableList);
                break;
            case 2:
                filterUsers = userData.stream().
                        sorted(Comparator.comparing(User::getLastName)).toList();
                observableList = FXCollections.observableArrayList(filterUsers);
                userTable.setItems(observableList);
                break;
            case 3:
                filterUsers = userData.stream().
                        sorted(Comparator.comparing(User::getEmail)).toList();
                observableList = FXCollections.observableArrayList(filterUsers);
                userTable.setItems(observableList);
                break;
        }

    }

    @FXML
    public void fromLowerToUpper()
    {
        List<User> toLowerUsers = userData.stream()
                .map(user -> new User(user.getFirstName().toUpperCase(), user.getLastName().toUpperCase(),
                        user.getEmail(), user.getImagelink(), user.getIp_address())).toList();

        observableList = FXCollections.observableArrayList(toLowerUsers);
        userTable.setItems(observableList);
    }

    @FXML
    public void fromUpperToLower()
    {
        List<User> toUpperUsers = userData.stream()
                .map(user -> new User(user.getFirstName().toLowerCase(), user.getLastName().toLowerCase(),
                        user.getEmail(), user.getImagelink(), user.getIp_address())).toList();

       observableList =  FXCollections.observableArrayList(toUpperUsers);
       userTable.setItems(observableList);
    }

    @FXML
    public void onMapButtonPressed()
    {
        int i = 0;
        for(User user: userData)
        {
            String ipKey = user.getIp_address().substring(0, user.getIp_address().indexOf('.'));
            ipMap.computeIfAbsent(ipKey, k -> new ArrayList<>()).add(user);
        }

        for (Map.Entry<String, List<User>> entry : ipMap.entrySet()) {
            String ipKey = entry.getKey();
            List<User> userList = entry.getValue();

            System.out.println("IP Key: " + ipKey);
            for (User user : userList) {
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                System.out.println("  First Name: " + firstName);
                System.out.println("  Last Name: " + lastName);
                System.out.println();
            }
            System.out.println();
        }
    }

    @FXML
    public void onResetButtonPressed()
    {
        observableList = FXCollections.observableArrayList(userData);
        userTable.setItems(observableList);
    }

}