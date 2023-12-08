package com.nimesa;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Weather {

    private static String weather_url = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";
    private Scanner scanner = new Scanner(System.in);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String userEnteredDate = null;
    private int userChosenOption = -1;

    public static void main(String[] args) {

        Weather weather = new Weather();
        weather.printMainMenu();

    }

    void printMainMenu() {

        userChosenOption = -1;

        System.out.println("\n-----------------------------------------\n");

        System.out.println("1. Get Weather");
        System.out.println("2. Get Wind Speed");
        System.out.println("3. Get Pressure");
        System.out.println("0. Exit\n");

        System.out.println("Type your option (0 - 3) and press Enter\n");

        readMenuInput();
    }

    void promptDate() {
        System.out.println("Enter Date (yyyy-MM-dd) from 2019-03-27 to 2019-03-31");
        readDateInput();
    }

    void readMenuInput(){
        int menuOption = scanner.nextInt();
        //System.out.println("You chose "+menuOption);
        validateMenuInput(menuOption);
    }

    void readDateInput()
    {
        String date = scanner.next();
        //System.out.println("You entered "+date);

        try {
            Date enteredDate = simpleDateFormat.parse(date);
            Date minDate = simpleDateFormat.parse("2019-03-27");
            Date maxDate = simpleDateFormat.parse("2019-03-31");

            if(enteredDate.after(maxDate) || enteredDate.before(minDate))
            {
                System.out.println("Please enter a date between 2019-03-27 to 2019-03-31");
                printMainMenu();
            }
            else {
                userEnteredDate = date;
                callWeatherAPI();
            }

        } catch (ParseException e) {
            System.out.println("Error: Invalid Date Format!");
            printMainMenu();
        }
    }

    void validateMenuInput(int n) {
        System.out.println("selected "+n);
        
        if (1 > n || 4 < n)
            System.out.println("Invalid Choice! Choose an option from 1 to 4");
        else if (n == 0) {
            System.out.println("Exit: Bye!");
        } else {
            userChosenOption = n;
            promptDate();
        }

        
    }

    void callWeatherAPI() {

        //System.out.println("Connecting to REST API ...\n");
        try {
            URL url = new URL(weather_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            //System.out.println("Sending get request : " + url);
            //System.out.println("Response code : " + responseCode);

            if(responseCode == 200)
            {
                // Reading response from input Stream
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String output;
                StringBuffer response = new StringBuffer();

                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                in.close();
                con.disconnect();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray dataArray = jsonObject.getJSONArray("list");
                for (int i = 0; i < dataArray.length(); i++) {

                    // store each object in JSONObject
                    JSONObject explrObject = dataArray.getJSONObject(i);
                    if(explrObject.get("dt_txt").toString().contains(userEnteredDate))
                    {
                        if(userChosenOption == 1)
                        {
                            JSONArray weather = (JSONArray) explrObject.get("weather");
                            JSONObject main = weather.getJSONObject(0);
                            System.out.println("The Weather on "+userEnteredDate+" is: "+main.get("main").toString());
                        }
                        else if(userChosenOption ==2)
                        {
                            JSONObject main = new JSONObject(explrObject.get("wind").toString());
                            System.out.println("The Wind speed on "+userEnteredDate+" is: "+main.get("speed").toString());
                        }
                        else
                        {
                            JSONObject main = new JSONObject(explrObject.get("main").toString());
                            System.out.println("The Pressure on "+userEnteredDate+" is: "+main.get("pressure").toString());
                        }

                        System.out.println("\n");
                        break;
                    }
                }
            }
            else
            {
                System.out.println("Unable to fetch data from API");
            }
        } catch (IOException ioe) {
            System.out.println("Unable to fetch data from API");
        }
        printMainMenu();
    }

}
