package org.softelpsa;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.PDU;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SNMPManager {
    private Snmp snmp = null;
    private String address = null;

    /**
     * Constructor
     * @param address - Address of the SNMP Agent (e.g. "udp:127.0.0.1/161")
     */
    public SNMPManager(String address) {
        this.address = address;
    }

    /**
     * Start the Snmp session.
     */
    public void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    public void stop() throws IOException {
        if (snmp != null) {
            snmp.close();
        }
    }

    /**
     * Method to get the MIB data from the agent
     * @param oid - OID value
     * @return - MIB value
     */
    public String getAsString(OID oid) throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(oid));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        CommunityTarget target = getCommunityTarget();
        ResponseEvent event = snmp.get(pdu, target);

        if(event != null) {
            return event.getResponse().get(0).getVariable().toString();
        }
        throw new RuntimeException("GET timed out");
    }


    public Map<String, String> getBulk(OID[] oids) throws IOException {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GETBULK);
        pdu.setMaxRepetitions(10); // Adjust as needed

        CommunityTarget target = getCommunityTarget();
        ResponseEvent event = snmp.send(pdu, target);

        Map<String, String> resultMap = new HashMap<>();
        if (event != null && event.getResponse() != null) {
            Vector<? extends VariableBinding> variableBindings = event.getResponse().getVariableBindings();
            for (VariableBinding vb : variableBindings) {
                resultMap.put(vb.getOid().toString(), vb.getVariable().toString());
            }
        } else {
            throw new RuntimeException("GETBULK timed out or failed");
        }
        return resultMap;
    }


    /**
     * Perform an SNMP walk to retrieve all rows and columns of a given table.
     * @param baseOid The base OID of the table.
     * @return A list of VariableBindings representing the rows and columns.
     */
    public List<VariableBinding> walkTable(OID baseOid) throws IOException {
        List<VariableBinding> result = new ArrayList<>();
        PDU pdu = new PDU();
        OID targetOid = new OID(baseOid);
        pdu.add(new VariableBinding(targetOid));

        while (true) {
            pdu.setType(PDU.GETNEXT);
            ResponseEvent event = snmp.get(pdu, getCommunityTarget());
            if (event == null || event.getResponse() == null) {
                System.out.println("the response or response data is Null");
                break;
            }

            VariableBinding vb = event.getResponse().get(0);
            if (!vb.getOid().startsWith(baseOid)) {
                // We've reached the end of the table
                System.out.println("reached the End of the table");
                break;
            }

            result.add(vb);
            // Set up the next request
            pdu.setRequestID(new Integer32(0));
            pdu.set(0, vb);
        }
        return result;
    }



    /**
     * This method returns a CommunityTarget object
     */
    private CommunityTarget getCommunityTarget() {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("copa")); // default is public, change if different
        target.setAddress(GenericAddress.parse(address));
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c); // change if different version
        return target;
    }



}
