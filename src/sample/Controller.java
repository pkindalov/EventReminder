package sample;

import com.google.common.collect.Multimap;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Controller {
    public DatePicker datePick;
    public TextArea reminderMessage;
    public Button btnWriteOnFile;
    public TextArea showMeEvents;
    public Button searchEvent;
    public Map<String, List<String>> events = new HashMap<>();
    public List<String> dates = new ArrayList<>();
    public List<List<String>> reminders = new ArrayList<>();
    public Button loadDatabase;
    public String searchingDate;
    public String todayDate;
    public Button moveTextLeft;
    public Button showAllNotes;
    public Label lblStatus;
    public Label lblStep2;
    public Label lblStep3;
    public Label lblStep4;
    public Label lblStep5;
    public Label lblStep1;
    Multimap<String, List<String>> database;
    List<String> eventForDay = new ArrayList<>();
    List<String> todayEvents = new ArrayList<>();
    String text = "";


    public void writeOnFile(ActionEvent actionEvent) {
        String date = getDateStr(datePick);
        String message = reminderMessage.getText().replace("\n", "").replace("\r", "");
        if(date.equals("") || date.equals(null)){
            reminderMessage.setText("First choose date from datepicker");
        }

        FileChooser ch = new FileChooser();
        File selectedFile = ch.showSaveDialog(((Button)actionEvent.getSource()).getScene().getWindow());

        try{

            String fileName = catcOnlyFileName(selectedFile.toString());
            write(selectedFile.toString(), message, date);
            lblStatus.setText("File Saved successfull");

        }catch (Exception e){
            lblStatus.setText(e.getMessage());
        }


        try{
            load(selectedFile);
            lblStatus.setText("Base loaded successfully");
        }catch (Exception ex){
            lblStatus.setText(ex.getMessage());
        }





    }

    private void write(String fileName, String textForSave, String date) throws IOException {


        try(FileOutputStream fos = new FileOutputStream(fileName, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"))){


            for (char c : date.toCharArray()) {
                bw.write(c);
            }
            bw.newLine();

            for (char c : textForSave.toCharArray()) {
                bw.write(c);
            }
            bw.newLine();

            bw.close();
            fos.close();

            clearLblSteps(lblStep4);
            writeCheckSymbol(lblStep4);


        }catch (Exception e){
            lblStatus.setText(e.getMessage());
        }

    }


    public void loadDatabase(ActionEvent actionEvent) throws IOException {


        FileChooser ch = new FileChooser();
        File selectedFile = ch.showOpenDialog(((Button)actionEvent.getSource()).getScene().getWindow());

        Date today = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        todayDate = date_format.format(today);
        searchingDate = getDateStr(datePick);


        load(selectedFile);
//
//
//        try(BufferedReader br = new BufferedReader(new FileReader(selectedFile))){
//
//            String line;
//            for (int i = 0; (line = br.readLine()) != null ; i++) {
//                if(i % 2 == 0){
////                    showMeEvents.appendText(line + "\n");
//                    if (line.equals(searchingDate)){
//                        line = br.readLine();
//                        eventForDay.add(line);
//                        i++;
//                    }
//
//                    if(line.equals(todayDate)){
//                        line = br.readLine();
//                        todayEvents.add(line);
//                        i++;
//                    }
//
//                }
//            }
//
//            if(eventForDay.size() > 0){
//                showMeEvents.setText("Event for " + searchingDate + "\n");
//                for (String s : eventForDay) {
//                    showMeEvents.appendText(s + "\n");
//                }
//            }else {
//                showMeEvents.setText("No events in this day. (" + searchingDate + ")");
//            }
//
//
//
//            if(todayEvents.size() > 0){
//                showMeEvents.appendText("\n" + "Today: \n");
//                for (String todayEvent : todayEvents) {
//                    showMeEvents.appendText(todayEvent + "\n");
//                }
//            }
//
//
//        }catch (Exception e){
//            reminderMessage.setText(e.getMessage());
//        }



//        clearTextFields();



    }

    private void writeCheckSymbol(Label label) {
        int checkSign = 10004;
        label.setText(Character.toString((char)checkSign) + " Done");

    }





    private void load(File selectedFile) {

        text = "";


        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), "UTF8"))){


            String line = br.readLine();
            while (line != null){
                if(!line.equals(" ")){
                    text += line;
                    text += '\n';
                }

                line = br.readLine();
            }

            br.close();
            lblStatus.setText("Database loaded successfully");

            clearLblSteps(lblStep1);
            writeCheckSymbol(lblStep1);

            Date today = new Date();
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            todayDate = date_format.format(today);
            searchingDate = getDateStr(datePick);
            showMeEvents.setText("Events for today(" + todayDate + ")\n\n");
            search(todayDate);

//            datePick.getEditor().setText(todayDate);
            //searchForEventToday();
//            for (int i = 0; (line = br.readLine()) != null ; i++) {

//                if(i % 2 == 0){
//                    showMeEvents.appendText(line + "\n");
//                    if (line.equals(searchingDate)){
//                        line = br.readLine();
//                        eventForDay.add(line);
//                        i++;
//                    }

//                    if(line.equals(todayDate)){
//                        line = br.readLine();
//                        todayEvents.add(line);
//                        i++;
//                    }

//                }
//            }





        }catch (Exception e){
            lblStatus.setText(e.getMessage());
        }

    }


    private String catcOnlyFileName(String str) {
        String regex = "[^\\/]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()){
            str = matcher.group();
        }

        return str;
    }

    private String getDateStr(DatePicker datePick) {
        String dateStr = "";

        try{
            LocalDate localDate = datePick.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            if(localDate.equals(null) || instant.equals(null)){
                return dateStr;
            }
            Date date = Date.from(instant);
            dateStr = localDate.toString();
            return dateStr;
        }catch (Exception e){
            lblStatus.setText(e.getMessage());
        }

        return dateStr;

    }

    public void getDate(ActionEvent actionEvent) {

        clearLblSteps(lblStep2);
        writeCheckSymbol(lblStep2);

        try{
            LocalDate localDate = datePick.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);

            Date today = new Date();
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            todayDate = date_format.format(today);
            searchingDate = getDateStr(datePick);
//
//            Date datepick = new Date(datePick.getValue().toEpochDay());
            showMeEvents.setText("Events for today(" + searchingDate + ")\n\n");
            search(searchingDate);
        }catch (Exception e){
            lblStatus.setText(e.getMessage());
        }



        //        reminderMessage.setText(localDate + "\n" + instant + "\n" + date);
        //System.out.println(localDate + "\n" + instant + "\n" + date);
    }




    public void searchForEvent(ActionEvent actionEvent) throws IOException {
        reminderMessage.setText("");

        Date today = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        todayDate = date_format.format(today);
        searchingDate = getDateStr(datePick);

        search(searchingDate);




//            if(eventForDay.size() > 0){
//            showMeEvents.setText("Event for " + searchingDate + "\n");
//            for (String s : eventForDay) {
//                showMeEvents.appendText(s + "\n");
//            }
//        }else {
//            showMeEvents.setText("No events in this day. (" + searchingDate + ")");
//        }
//
//
//
//        if(todayEvents.size() > 0){
//            showMeEvents.appendText("\n" + "Today: \n");
//            for (String todayEvent : todayEvents) {
//                showMeEvents.appendText(todayEvent + "\n");
//            }
//        }


//        if(i % 2 == 0){
//                    showMeEvents.appendText(line + "\n");
//                    if (line.equals(searchingDate)){
//                        line = br.readLine();
//                        eventForDay.add(line);
//                        i++;
//                    }

//                    if(line.equals(todayDate)){
//                        line = br.readLine();
//                        todayEvents.add(line);
//                        i++;
//                    }


//        if(eventForDay.size() > 0){
//            showMeEvents.setText("Event for " + searchingDate + "\n");
//            for (String s : eventForDay) {
//                showMeEvents.appendText(s + "\n");
//            }
//        }else {
//            showMeEvents.setText("No events in this day. (" + searchingDate + ")");
//        }
//
//
//
//        if(todayEvents.size() > 0){
//            showMeEvents.appendText("\n" + "Today: \n");
//            for (String todayEvent : todayEvents) {
//                showMeEvents.appendText(todayEvent + "\n");
//            }
//        }

//        FileChooser ch = new FileChooser();
//        File selectedFile = ch.showOpenDialog(((Button)actionEvent.getSource()).getScene().getWindow());
//
//        Date today = new Date();
//        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
//        todayDate = date_format.format(today);
//        searchingDate = getDateStr(datePick);


//        FileChooser ch = new FileChooser();
//        File selectedFile = ch.showOpenDialog(((Button)actionEvent.getSource()).getScene().getWindow());
//        String searchingDate = getDateStr(datePick);
//
//        Date today = new Date();
//        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
//        String todayDate = date_format.format(today);
//
//
//        Map<String, List<String>> events = new HashMap<>();
//        Properties properties = new Properties();
//        properties.load(new FileInputStream(selectedFile));
//
//        for (String key : properties.stringPropertyNames()) {
//            events.put(key, Arrays.asList(properties.get(key).toString()));
//        }

//        searchingDate = getDateStr(datePick);


//        if(events.containsKey(searchingDate)){
//            datePick.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
//            datePick.setStyle("-fx-background-color: red;");
//        }else {
//            datePick.setStyle("-fx-background-color: #6495ED");
//        }
//        searchInMapForEvent(searchingDate, events);
//        searchInMapForEventToday(todayDate, todayDate, events);



    }

    private void search(String todayDate) {

        List<String> datesAndEvents = new LinkedList<>(Arrays.asList(text.split("\n")));
//         datesAndEvents.removeIf(str -> str == null || "".equals(str) || " ".equals(str));


        for (int i = 0; i < datesAndEvents.size(); i++) {


            if(datesAndEvents.get(i).equals("") || datesAndEvents.get(i).equals(" ") || datesAndEvents.get(i).equals(null)){

                for (int j = 0; j < 2; j++) {
                    datesAndEvents.remove(i);
                }

            }
        }

//         datesAndEvents.removeIf(str -> str == null || "".equals(str) || " ".equals(str));

        try{
            for (int i = 0; i < datesAndEvents.size(); i++) {

                if(i % 2 == 0){
//                showMeEvents.appendText("On date " + searchingDate + "you have: \n");
//                showMeEvents.appendText("Events for " + searchingDate + "\n");
                    if(datesAndEvents.get(i).equals(todayDate)){
                        showMeEvents.appendText("-" + datesAndEvents.get(i + 1) + "\n\n");
//                    eventForDay.add(datesAndEvents.get(i + 1));
                    }

//                showMeEvents.appendText("\nToday you have following events: \n");
//                if(datesAndEvents.get(i).equals(todayDate)){
////                    todayEvents.add(datesAndEvents.get(i + 1));
//                    showMeEvents.appendText(datesAndEvents.get(i + 1) + "\n");
//                }
                }

            }


            if(!showMeEvents.getText().isEmpty()){
                lblStatus.setText("Events fonded");
            }else {
                lblStatus.setText("Nothing found");
            }

        }catch (Exception e){
            lblStatus.setText("You must first load base from button Load Database");
        }


        clearLblSteps(lblStep5);
        writeCheckSymbol(lblStep5);
    }





    ////////////////////////////////////////////////



//    private void searchForEventToday(){
//        Date today = new Date();
//        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
//        todayDate = date_format.format(today);
//        searchingDate = getDateStr(datePick);
//        List<String> datesAndEvents = new LinkedList<>(Arrays.asList(text.split("\n")));
//
//        for (int i = 0; i < datesAndEvents.size(); i++) {
//            if(datesAndEvents.get(i).equals("") || datesAndEvents.get(i).equals(" ") || datesAndEvents.get(i).equals(null)){
//                for (int j = 0; j < 2; j++) {
//                    datesAndEvents.remove(i);
//                }
//
//            }
//        }
//
//        try{
//            for (int i = 0; i < datesAndEvents.size(); i++) {
//
//                if(i % 2 == 0){
//                    if(datesAndEvents.get(i).equals(todayDate)){
//                        showMeEvents.appendText("-" + datesAndEvents.get(i + 1) + "\n\n");
//                    }
//
//                }
//
//            }
//
//
//            if(!showMeEvents.getText().isEmpty()){
//                lblStatus.setText("Events fonded");
//            }else {
//                lblStatus.setText("Nothing found");
//            }
//
//        }catch (Exception e){
//            lblStatus.setText("You must first load base from button Load Database");
//        }
//
//
//        clearLblSteps(lblStep5);
//        writeCheckSymbol(lblStep5);
//    }





    ///////////////////////////////////////////////







    private void searchInMapForEvent(String searchingDate, Map<String, List<String>> dateEvent) {



//        dateEvent.entrySet()
//                  .stream()
//                  .forEach(x -> {
//                      showMeEvents.setText("Key: " + x.getKey());
//                      showMeEvents.appendText("\n Value: " + x.getValue());
//                  });




//        dateEvent.entrySet().stream().
//                filter(x -> x.getKey().contains(searchingDate))
//                .forEach(s -> {
//                    showMeEvents.appendText("You have event for this day: \n" + s.getValue());
//                });

        datePick.setDayCellFactory(dayCellFactory);

        if(dateEvent.containsKey(searchingDate)){
            showMeEvents.appendText("You have event for this day: " + searchingDate + "\n");
//            showMeEvents.appendText("Today " +todayDate + " your event:\n");
            showMeEvents.appendText( removeBrackets(dateEvent.get(searchingDate).toString()) + "\n\n");
        }
    }


    private void searchInMapForEventToday(String today, String todayDate, Map<String, List<String>> dateEvent) {
        if(dateEvent.containsKey(today)){
            showMeEvents.appendText("Events for today: " + today + "\n");
//            showMeEvents.appendText("Today " +todayDate + " your event:\n");
            showMeEvents.appendText( removeBrackets(dateEvent.get(today).toString()) + "\n\n");
        }
    }


    public void clearTextFields(){
        showMeEvents.clear();
        reminderMessage.clear();
//        datePick.setStyle("-fx-background-color: #6495ED");
    }


    final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
        public DateCell call(final DatePicker datePicker) {
            return new DateCell() {
                @Override public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    for (String s : events.keySet()) {

                        if (MonthDay.from(item).equals(s)) {
                            setTooltip(new Tooltip("Happy Birthday!"));
                            setStyle("-fx-background-color: #ff4444;");
                        }
                    }



//                    if (item.equals(LocalDate.now().plusDays(1))) {
//                        // Tomorrow is too soon.
//                        setDisable(true);
//                    }
                }
            };
        }
    };


    public void moveTextLeft(ActionEvent actionEvent) {
        String[] message = showMeEvents.getText().split("\n");
        String messageForCopy = "";
        for (String s : message) {
            //reminderMessage.appendText(removeBrackets(s) + "\n");
            messageForCopy += s + "\n";
        }

        reminderMessage.setText(removeBrackets(messageForCopy));
//        message[1] = message[1].replace('[', ' ');
//        message[1] = message[1].replace(']', ' ');

    }


    public String removeBrackets(String str){
        str = str.replace('[', ' ')
                 .replace(']', ' ')
                 .replace("null", " ")
                 .trim();

        return  str;
    }

    public void showAllNotes(ActionEvent actionEvent) {

          List<String> allRecords = Arrays.asList(text.split("\n"));

        for (int i = 0; i < allRecords.size(); i++) {
            showMeEvents.appendText(allRecords.get(i)+ "\n");
            if(i % 2 != 0){
                showMeEvents.appendText("\n");
            }
        }


        clearLblSteps(lblStep5);
        writeCheckSymbol(lblStep5);

//        for (String allRecord : allRecords) {
//            showMeEvents.appendText(allRecord + "\n");
//        }

//        for (Map.Entry<String, List<String>> stringListEntry : events.entrySet()) {
//            //showMeEvents.appendText(stringListEntry.getKey() + "\n" + stringListEntry.getValue() + "\n");
//            for (List<String> strings : events.values()) {
//
//                for (String string : strings) {
//                    if (skipEmptyNotes(stringListEntry, strings, string)) continue;
//                    showMeEvents.appendText(stringListEntry.getKey() + "\n" + removeBrackets(strings.toString()) + "\n\n");
//                }
////
//            }
//        }


    }

    private boolean skipEmptyNotes(Map.Entry<String, List<String>> stringListEntry, List<String> strings, String string) {
        if(stringListEntry.getKey().isEmpty() || strings.isEmpty()){
            return true;
        }

        if(stringListEntry.getKey().equals("[null]") || string.equals("[null]")){
            return true;
        }

        if(stringListEntry.getKey().equals("") || string.equals("")){
            return true;
       }

        if (stringListEntry.getKey().equals("[]") || string.equals("[]")){
            return true;
        }
        return false;
    }

    public void statusClear(MouseEvent mouseEvent) {
        lblStatus.setText("");
    }

    private void clearLblSteps(Label label){
        label.setText("");
    }

//    public void checked(MouseEvent inputMethodEvent) {
//        clearLblSteps(lblStep3);
//        writeCheckSymbol(lblStep3);
//    }

    public void checkEventTextLength(MouseEvent mouseEvent) {
        String text = reminderMessage.getText();
        String date = getDateStr(datePick);

        if(date.length() > 0){
            clearLblSteps(lblStep2);
            writeCheckSymbol(lblStep2);
        }else {
            lblStep2.setText("2.Choose date for search or adding event");
        }

        if(text.length() > 10 && !text.equals("First choose date from datepicker")){
            clearLblSteps(lblStep3);
            writeCheckSymbol(lblStep3);
        }else {
            lblStep3.setText("3.Enter record in left window if you wish to add event or just skip this step if you search");
        }


    }
}










