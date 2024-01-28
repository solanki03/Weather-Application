import javax.swing.SwingUtilities;
import gui.WeatherAppGui;

public class AppLauncher {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                //display our weather gui
                new WeatherAppGui().setVisible(true);
                //testing: location geographical data from the API
                //System.out.println(WeatherApp.getLocationData("Tokyo"));
                //System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
