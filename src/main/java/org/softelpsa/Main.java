package org.softelpsa;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class Main {

    // this method will only manage on SBC
    public static void main(String[] args) {
        try {
            System.out.println("Starting SNMP manager ...");
            SNMPManager client = new SNMPManager("udp:172.30.1.19/161"); // Adjust IP and port
            client.start();

            System.out.println("Started SNMP manager ...");

            SNMPOperations snmpOps = new SNMPOperations(client);

            snmpOps.SbcPacketStats();

            // Keep the application running
            System.out.println("SNMP Manager is running. Press 'q' and Enter to quit.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));






//            while (true) {
//                String line = reader.readLine();
//                if ("q".equals(line.trim())) {
//                    System.out.println("SNMP Manager is stopping...");
//                    break;
//                }
//                // Additional commands or operations can be added here
//            }
//
//            client.stop(); // Properly stop the SNMP manager

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}