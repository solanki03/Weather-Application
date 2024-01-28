package backend;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/* retrive weather data from API - this backend logic will fetch the latest weather
 * data from the external API and return it. The GUI will display this data to the user
 */
public class WeatherApp {
    // fetch weather data for given location
    // work: it'll get the perticular name of the location of the weather from API
    public static JSONObject getWeatherData(String locationName) {
        // get location cordinates using the Geocoding API
        JSONArray locationData = getLocationData(locationName);

        // so to use the weather forecast API, we need to get the latitude and longitude
        // of the location
        // which we get from the geolocation API
        // extract latitude and longitude data
        // the geolocation API returns up a list of different countries that have that
        // entered city
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with location cordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude
                + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Asia%2FTokyo";

        try {
            // call API and get response
            HttpURLConnection conn = fetchAPIResponse(urlString);

            // check the response status
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }

            // store result in json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                // read and store the data into the string builder
                resultJson.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();

            // close url connection
            conn.disconnect();

            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrive hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // we want to get the current hour's data
            // so we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            // will be used to find the weather description
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity data
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed data
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build the wether json data object that we are going to acess in our frontend
            // to store the data we will need to give the value and ID in a way
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // retrive geographic co-ordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace in location name to + to add to API request format
        // example: New York, it'll replace the space when we use the API link and
        // submit our data
        locationName = locationName.replaceAll(" ", "+");

        /*
         * build API url with location parameter
         * here we need to get our API and then replace the name values with ours name.
         * So when we call our API, we are going to be passing it our location
         */
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        try {
            // call the API and get response
            // to make a HTTP request like our API call, we need a HTTP client like
            // HTTPURLConnection class
            // we are going to make a separate method to instantiate it because we will be
            // doing this multiple times in this class
            HttpURLConnection con = fetchAPIResponse(urlString);

            // check response status
            // 200 means --> successful connection
            if (con.getResponseCode() != 200) {
                System.out.println("Error! Couldn't connect to API");
                return null;
            } else {
                // store the response of the API results
                StringBuilder resultsJson = new StringBuilder();

                // we'll use a scanner to read the JSOn data that is returned from our API call
                // we do this by using a while loop and using the hashNext()
                Scanner sc = new Scanner((con.getInputStream()));

                // read and store the resulting json data into our string builder
                // if there is json data to be read, then we store it into our resultJson String
                while (sc.hasNext()) {
                    resultsJson.append(sc.nextLine());
                }

                // close scanner
                sc.close();

                // close url connection
                con.disconnect();

                /// parse the JSON string into a JSON obj
                // the reason why we are parsing it so that we can access the data more properly
                JSONParser parser = new JSONParser();
                JSONObject resulJsonObj = (JSONObject) parser.parse(String.valueOf(resultsJson));

                // get the list of location data the API generated from the location name
                // when we are trying to get the "results" it'll return us [data]
                // which is why we store it in a JSONArray
                JSONArray locationData = (JSONArray) resulJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // couldn't find location
        return null;
    }

    private static HttpURLConnection fetchAPIResponse(String urlString) {
        try {
            // attempt to create a connection
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // set request method to get the response
            con.setRequestMethod("GET");

            // connection to our API
            con.connect();

            return con;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // could not make any connection
        return null;
    }

    private static int findIndexCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        // itereate through the time list and see which one match our current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                // return the index value
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2024-01-03T00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // print the curent date and time
        String formateDateTime = currentDateTime.format(formatter);

        return formateDateTime;
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            // clear
            weatherCondition = "Clear";
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            // cloudy
            weatherCondition = "Cloudy";
        } else if (weatherCode > 57L && weatherCode <= 67L) {
            // rainy
            weatherCondition = "Rainy";
        } else if (weatherCode > 71L && weatherCode <= 77L) {
            // snow
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

}
