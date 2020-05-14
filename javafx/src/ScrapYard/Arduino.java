package ScrapYard;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.Arrays;

public class Arduino {
    private static SerialPort sp;

    public boolean arduinoStart() throws InterruptedException {
        sp = SerialPort.getCommPort("COM3");
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Succesfully connected to Arduino");
            return true;

        } else {
            System.out.println("Couldn't connect to Arduino");
            return false;
        }
    }
    public void arduinoSensor() {
        String lezing = null;
        try {

            while (sp.getInputStream().available() > 0) {
                System.out.println("bytes: "+sp.bytesAvailable());
                byte[] bytes = sp.getInputStream().readNBytes(1);
                lezing = new String(bytes);
                System.out.println(lezing);
//                int getal = Integer.parseInt(lezing.trim());
//                int firstDigit = Integer.parseInt(Integer.toString(getal).substring(0, 1));
//                System.out.println("eerste getal: " + firstDigit);
            }
        } catch (NullPointerException | IOException NE) {
            lezing = null;

        }
    }

    public static void main(String[] args) throws InterruptedException {
        Arduino arduino = new Arduino();
        arduino.arduinoStart();
        while (true) {

                arduino.arduinoSensor();

        }



        }
    }

