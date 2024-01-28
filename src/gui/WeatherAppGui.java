package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import backend.WeatherApp;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        // setup our gui and add the title
        super("Weather App");
        // set the size of the gui
        setSize(450, 650);

        // load gui at the center of the screen
        setLocationRelativeTo(null);

        // configure the gui to end the program
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // prevent any resize of our gui
        setResizable(false);

        // disable the by default layout of swing
        setLayout(null);

        // components
        addGuiComponents();
    }

    private void addGuiComponents() {
        // search field
        JTextField searchTextField = new JTextField();
        // set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);
        // change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("Weather Application\\src\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);

        // temparature text
        JLabel temparatureText = new JLabel("10°C");
        temparatureText.setBounds(0, 350, 450, 54);
        temparatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temparatureText.setHorizontalAlignment(SwingConstants.CENTER);

        // weather condition text
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);

        // Humidity image
        JLabel humidityImage = new JLabel(loadImage("Weather Application\\src\\assets\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);

        // humidity condition text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));

        // Wind speed image
        JLabel windSpeedImage = new JLabel(loadImage("Weather Application\\src\\assets\\windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);

        // Wind speed text
        JLabel windSpeedText = new JLabel("<html><b>WindSpeed</b> 15Km/h</html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));

        // search button
        JButton searchButton = new JButton(loadImage("Weather Application\\src\\assets\\search.png"));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                // validate input - remove white space
                // example: New York
                if (userInput.replaceAll("\\s", " ").length() <= 0) {
                    return;
                }

                // retrive weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // -----------------update------------------

                // update weather Image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // depending on the condition, we will update the weather image with the
                // condition
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("Weather Application\\src\\assets\\snow.png"));
                        break;
                }

                //update temperature text
                double temperature = (double)weatherData.get("temperature");
                temparatureText.setText(temperature + "°C");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>WindSpeed</b> " + windspeed + "Km/h</html>");
            }

        }));

        // add the components ---
        add(searchTextField);
        add(searchButton);
        add(weatherConditionImage);
        add(temparatureText);
        add(weatherConditionDesc);
        add(humidityImage);
        add(humidityText);
        add(windSpeedImage);
        add(windSpeedText);
    }

    // used to create/load images in our gui components
    private ImageIcon loadImage(String resoursePath) {
        try {
            // read the image file from the given path
            BufferedImage image = ImageIO.read(new File(resoursePath));

            // return an image icon so that our component can render it
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Cannot find the resource!");
        return null;
    }
}
