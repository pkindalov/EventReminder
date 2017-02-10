package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    public DatePicker datePick;
    public TextArea reminderMessage;
    public Button btnWriteOnFile;



    public void writeOnFile(ActionEvent actionEvent) {
        String date = getDateStr(datePick);
        String message = reminderMessage.getText();
        if(date.equals("") || date.equals(null)){
            reminderMessage.setText("First choose date from datepicker");
        }


        try{
            FileChooser ch = new FileChooser();
            File selectedFile = ch.showSaveDialog(((Button)actionEvent.getSource()).getScene().getWindow());
            String fileName = catcOnlyFileName(selectedFile.toString());
            write(selectedFile.toString(), message, date);

        }catch (Exception e){
            reminderMessage.setText(e.getMessage());
        }







    }

    private void write(String fileName, String textForSave, String date) {

        Map<String, Serializable> dateEvent = new HashMap<>();

        if(!dateEvent.containsKey(date)){
            dateEvent.put(date, textForSave);
        }else {
            dateEvent.put(date, textForSave);
        }


        try(FileOutputStream fos = new FileOutputStream(fileName, true);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(dateEvent);
            oos.close();
            fos.close();

        } catch (IOException e) {
            reminderMessage.setText(e.getMessage());
        }

//        for (String s : dateEvent.keySet()) {
//            reminderMessage.setText("Date: " + "\n" + s + "Reminder: " + "\n" + dateEvent.get(s));
//        }




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
            reminderMessage.setText(e.getMessage());
        }

        return dateStr;

    }

    public void getDate(ActionEvent actionEvent) {

        try{
            LocalDate localDate = datePick.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);
        }catch (Exception e){
            reminderMessage.setText(e.getMessage());
        }

        //        reminderMessage.setText(localDate + "\n" + instant + "\n" + date);
        //System.out.println(localDate + "\n" + instant + "\n" + date);
    }




    public void searchForEvent(ActionEvent actionEvent) {

        FileChooser ch = new FileChooser();
        File selectedFile = ch.showOpenDialog(((Button)actionEvent.getSource()).getScene().getWindow());
        String searchingDate = getDateStr(datePick);

        Date today = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = date_format.format(today);
        Map<String, Serializable> dateEvent = null;

        try(FileInputStream fis = new FileInputStream(selectedFile);
        ObjectInputStream ois = new ObjectInputStream(fis)) {

            dateEvent = (HashMap)ois.readObject();


        } catch (IOException e) {
            reminderMessage.setText(e.getMessage());
        } catch (ClassNotFoundException e) {
            reminderMessage.setText(e.getMessage());
        }

        searchInMapForEvent(searchingDate, todayDate, dateEvent);

        searchInMapForEvent(todayDate, todayDate, dateEvent);


    }

    private void searchInMapForEvent(String searchingDate, String todayDate, Map<String, Serializable> dateEvent) {
        if(dateEvent.containsKey(searchingDate)){
            reminderMessage.setText("You have event for this day: \n");
            reminderMessage.appendText(searchingDate + "\n");
            reminderMessage.appendText(todayDate);
            reminderMessage.appendText((String) dateEvent.get(searchingDate));
        }
    }
}
