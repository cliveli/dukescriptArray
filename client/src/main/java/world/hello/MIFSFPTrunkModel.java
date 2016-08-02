/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.hello;

import net.java.html.json.Model;
import net.java.html.json.Property;

@Model(className = "MIFSFPTrunkVM", targetId = "", properties = {
    @Property(name = "deviceName", type = String.class),
    @Property(name = "hiddenUuid", type = String.class),
    @Property(name = "hiddenChassisUuid", type = String.class),
    @Property(name = "tabText", type = String.class),
    @Property(name = "headerText", type = String.class),
    @Property(name = "txBandwidth", type = String.class),
    @Property(name = "rxBandwidth", type = String.class),
    @Property(name = "trunkGeneralEntryStatus", type = String.class)})
public class MIFSFPTrunkModel {

}