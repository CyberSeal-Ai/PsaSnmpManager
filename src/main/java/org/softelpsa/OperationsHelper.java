package org.softelpsa;

import org.snmp4j.smi.OID;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OperationsHelper {

    /**
     * Fetch multiple MIB values at once using GET.
     */
    public static Map<String, String> fetchMIBs(OID[] oids, SNMPManager snmpManager ) throws IOException {
        Map<String, String> mibValues = new HashMap<>();
        for (OID oid : oids) {
            String value = snmpManager.getAsString(oid);
            mibValues.put(oid.toString(), value);
        }
        return mibValues;
    }

    /**
     * Fetch multiple MIB values using GETBULK.
     */
    public static Map<String, String> bulkFetchMIBs(OID[] oids , SNMPManager snmpManager) throws IOException {
        return snmpManager.getBulk(oids); // Assuming getBulk is implemented in SNMPManager
    }

}
