package org.softelpsa;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNMPOperations {

    private SNMPManager snmpManager;

    public SNMPOperations(SNMPManager snmpManager) {
        this.snmpManager = snmpManager;
    }

    // method to get the network packet stats tree OID: 1.3.6.1.4.1.2879.2.10.4.2
    public void SbcPacketStats() throws IOException {

        System.out.println("fetching the packet statics form the SBC");

        OID oid = new OID("1.3.6.1.4.1.2879.2.10.4.2");
        List<VariableBinding> tableEntries = snmpManager.walkTable(oid);

//        List<VariableBinding> tableEntries = SNMPOperations.getTableRowsAndColumns(client, tableBaseOid);
        for (VariableBinding vb : tableEntries) {
            System.out.println(vb.getOid() + " = " + vb.getVariable());
        }

    }

}
