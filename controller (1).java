package net.floodlightcontroller.cs446;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.action.OFActions;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructionApplyActions;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructions;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.util.FlowModUtils;
import net.floodlightcontroller.util.OFMessageDamper;
import net.floodlightcontroller.util.OFMessageUtils;

public class MyController implements IOFMessageListener, IFloodlightModule {

        protected IFloodlightProviderService floodlightProvider;

        protected static Logger logger;

        protected OFMessageDamper messageDamper;
    private int OFMESSAGE_DAMPER_CAPACITY = 10000;
    private int OFMESSAGE_DAMPER_TIMEOUT = 250; // ms
    public static final int FORWARDING_APP_ID = 446;
    static {
        AppCookie.registerApp(FORWARDING_APP_ID, "forwarding");
    }
    protected static final U64 cookie = U64.of(0xABCD <<
48);//AppCookie.makeCookie(FORWARDING_APP_ID, 0);
    @Override
        public String getName() {
                // TODO Auto-generated method stub
                return MyController.class.getSimpleName();
        }

        @Override
        public boolean isCallbackOrderingPrereq(OFType type, String name) {
                // TODO Auto-generated method stub
                return false;
        }

        @Override
        public boolean isCallbackOrderingPostreq(OFType type, String name) {
                // TODO Auto-generated method stub
                return false;
        }

        @Override
        public Collection<Class<? extends IFloodlightService>> getModuleServices() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Map<Class<? extends IFloodlightService>, IFloodlightService>
getServiceImpls() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Collection<Class<? extends IFloodlightService>>
getModuleDependencies() {
                // TODO Auto-generated method stub
                Collection<Class<? extends IFloodlightService>> l =
                        new ArrayList<Class<? extends IFloodlightService>>();
                    l.add(IFloodlightProviderService.class);
                    return l;
        }

        @Override
        public void init(FloodlightModuleContext context) throws
FloodlightModuleException {
                // TODO Auto-generated method stub
                floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
            logger = LoggerFactory.getLogger(MyController.class);

            messageDamper = new OFMessageDamper(OFMESSAGE_DAMPER_CAPACITY,
                EnumSet.of(OFType.FLOW_MOD),
                OFMESSAGE_DAMPER_TIMEOUT);

        }

        @Override
        public void startUp(FloodlightModuleContext context) throws
FloodlightModuleException {
                // TODO Auto-generated method stub
                floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

        }

        @Override
        public net.floodlightcontroller.core.IListener.Command
receive(IOFSwitch sw, OFMessage msg,
                        FloodlightContext cntx) {
                System.out.print("Switch  :" + sw.getId().toString());
                System.out.print("Port :" +
String.valueOf(((OFPacketIn)msg).getInPort().getPortNumber()));
                boolean flag =false;
                // TODO Auto-generated method stub
                switch(msg.getType()){
                case PACKET_IN:


                        Ethernet eth =
                IFloodlightProviderService.bcStore.get(cntx,

IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
                        MacAddress srcMac = eth.getSourceMACAddress();
                        MacAddress dstMac = eth.getDestinationMACAddress();

//                      if (flag == false)
//                              flag = true;
//                      else
//                              flag = false;           
                        if(sw.getId().toString().contains("01"))
                                addMyFlow1(sw, eth, ((OFPacketIn)msg).getInPort().getPortNumber(), flag);
                        if(sw.getId().toString().contains("02"))
                                addMyFlow2(sw, eth, ((OFPacketIn)msg).getInPort().getPortNumber(),flag);
                        if(sw.getId().toString().contains("03"))
                                addMyFlow3(sw, eth, ((OFPacketIn)msg).getInPort().getPortNumber(),flag);
                        if(sw.getId().toString().contains("04"))
                                addMyFlow4(sw, eth, ((OFPacketIn)msg).getInPort().getPortNumber(),flag);
                        if(eth.getEtherType()==EthType.IPv4){

                                logger.info("Packet in from switch {}", sw.getId().toString());


                                IPv4 pkt = (IPv4) eth.getPayload();
                                if(pkt.getProtocol()== IpProtocol.ICMP){
                                        logger.info("Src MAC:{}, Src IP:{}",
                                                srcMac.toString(),
                                                pkt.getSourceAddress().toString());
                                        logger.info("ip port:{}\n",
                                                Integer.toString( ((OFPacketIn)msg).getInPort().getPortNumber() ));


                                        //return Command.STOP;
                                }
                        }
                        break;
                default:
                        break;
        }
        return Command.CONTINUE;
        }

        void addMyFlow1(IOFSwitch sw, Ethernet eth, int port, boolean flag){
                System.out.print("Port in addFlow1:" + String.valueOf(port));
                if((port!=1)&&(port!=2)) return;
//              if(port == 3)
//                      port = 2;
//              if(flag==true && port==1)
//                      port=1;
//              else if(flag==false && port==1)
//                      port=0;
//              else if(flag == true && port==3)
//                      port=2;
//              else if(flag==false && port==2)
//                      port=2;

                OFFlowMod.Builder fmb;

                OFFactory myFactory=sw.getOFFactory();

                fmb=myFactory.buildFlowAdd();

                MacAddress srcMac = eth.getSourceMACAddress();
                MacAddress dstMac = eth.getDestinationMACAddress();

                Match myMatch = myFactory.buildMatch()
                            .setExact(MatchField.IN_PORT, OFPort.of(port))
                            //.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            //.setExact(MatchField.ETH_SRC, srcMac)
                            //.setExact(MatchField.ETH_DST, dstMac)
//                          .setExact(MatchField.IPV4_SRC, ((IPv4)
eth.getPayload()).getSourceAddress())
                            //.setExact(MatchFielAd.IPV4_DST, ((IPv4)
eth.getPayload()).getDestinationAddress())
                            //.setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setMasked(MatchField.IPV4_DST, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
                            //.setExact(MatchField.TCP_DST, TransportPort.of(80))
                            .build();


                ArrayList<OFAction> actionList = new ArrayList<OFAction>();
                OFActions actions = myFactory.actions();


                OFActionOutput output = actions.buildOutput()
                    .setMaxLen(0xFFffFFff)
                    .setPort(OFPort.of(3-port))
                    .build();
                actionList.add(output);

                fmb
                .setIdleTimeout(60)
        //.setHardTimeout(5)
        .setBufferId(OFBufferId.NO_BUFFER)
        .setCookie(cookie)
        .setPriority(2)
        .setMatch(myMatch);

                FlowModUtils.setActions(fmb, actionList, sw);




                try {
                        messageDamper.write(sw, fmb.build());
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }


        }

        void addMyFlow2(IOFSwitch sw, Ethernet eth, int port, boolean flag){
                System.out.print("Port in addFlow2:" + String.valueOf(port));
                if((port!=1)&&(port!=2)) return;

                OFFlowMod.Builder fmb;

                OFFactory myFactory=sw.getOFFactory();

                fmb=myFactory.buildFlowAdd();

                MacAddress srcMac = eth.getSourceMACAddress();
                MacAddress dstMac = eth.getDestinationMACAddress();

                Match myMatch = myFactory.buildMatch()
                            .setExact(MatchField.IN_PORT, OFPort.of(port))
                            //.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            //.setExact(MatchField.ETH_SRC, srcMac)
                            //.setExact(MatchField.ETH_DST, dstMac)
                            //.setExact(MatchField.IPV4_SRC, ((IPv4)
eth.getPayload()).getSourceAddress())
                            //.setExact(MatchField.IPV4_DST, ((IPv4)
eth.getPayload()).getDestinationAddress())
                            //.setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setMasked(MatchField.IPV4_DST, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
                            //.setExact(MatchField.TCP_DST, TransportPort.of(80))
                            .build();


                ArrayList<OFAction> actionList = new ArrayList<OFAction>();
                OFActions actions = myFactory.actions();


                OFActionOutput output = actions.buildOutput()
                    .setMaxLen(0xFFffFFff)
                    .setPort(OFPort.of(3-port))
                    .build();
                actionList.add(output);

                fmb
                .setIdleTimeout(60)
        //.setHardTimeout(5)
        .setBufferId(OFBufferId.NO_BUFFER)
        .setCookie(cookie)
        .setPriority(2)
        .setMatch(myMatch);

                FlowModUtils.setActions(fmb, actionList, sw);



                try {
                        messageDamper.write(sw, fmb.build());
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }


        }

        void addMyFlow3(IOFSwitch sw, Ethernet eth, int port, boolean flag){
                System.out.print("Port in addFlow3:" + String.valueOf(port));
                if((port!=1)&&(port!=2)) return;

                OFFlowMod.Builder fmb;

                OFFactory myFactory=sw.getOFFactory();

                fmb=myFactory.buildFlowAdd();

                MacAddress srcMac = eth.getSourceMACAddress();
                MacAddress dstMac = eth.getDestinationMACAddress();

                Match myMatch = myFactory.buildMatch()
                            .setExact(MatchField.IN_PORT, OFPort.of(port))
                            //.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            //.setExact(MatchField.ETH_SRC, srcMac)
                            //.setExact(MatchField.ETH_DST, dstMac)
                            //.setExact(MatchField.IPV4_SRC, ((IPv4)
eth.getPayload()).getSourceAddress())
                            //.setExact(MatchField.IPV4_DST, ((IPv4)
eth.getPayload()).getDestinationAddress())
                            //.setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setMasked(MatchField.IPV4_DST, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
                            //.setExact(MatchField.TCP_DST, TransportPort.of(80))
                            .build();


                ArrayList<OFAction> actionList = new ArrayList<OFAction>();
                OFActions actions = myFactory.actions();


                OFActionOutput output = actions.buildOutput()
                    .setMaxLen(0xFFffFFff)
                    .setPort(OFPort.of(3-port))
                    .build();
                actionList.add(output);

                fmb
                .setIdleTimeout(60)
        //.setHardTimeout(5)
        .setBufferId(OFBufferId.NO_BUFFER)
        .setCookie(cookie)
        .setPriority(2)
        .setMatch(myMatch);

                FlowModUtils.setActions(fmb, actionList, sw);



                try {
                        messageDamper.write(sw, fmb.build());
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }


        }

        void addMyFlow4(IOFSwitch sw, Ethernet eth, int port, boolean flag){
                System.out.print("Port in addFlow4:" + String.valueOf(port));
                if((port!=1)&&(port!=3)) return;
//              if(port ==3)
//                      port = 2;
//              if(flag==true && port==1)
//                      port=1;
//              else if(flag==true && port==3)
//                      port=2;
//              else if(flag == false && port==3)
//                      port=3;
//              else if(flag==false && port==2)
//                      port=1;

                OFFlowMod.Builder fmb;

                OFFactory myFactory=sw.getOFFactory();

                fmb=myFactory.buildFlowAdd();

                MacAddress srcMac = eth.getSourceMACAddress();
                MacAddress dstMac = eth.getDestinationMACAddress();

                Match myMatch = myFactory.buildMatch()
                            .setExact(MatchField.IN_PORT, OFPort.of(port))
                            //.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            //.setExact(MatchField.ETH_SRC, srcMac)
                            //.setExact(MatchField.ETH_DST, dstMac)
//                          .setExact(MatchField.IPV4_SRC, ((IPv4)
eth.getPayload()).getSourceAddress())
                            //.setExact(MatchField.IPV4_DST, ((IPv4)
eth.getPayload()).getDestinationAddress())
                            //.setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setMasked(MatchField.IPV4_DST, IPv4AddressWithMask.of("10.0.0.1/24"))
                            //.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
                            //.setExact(MatchField.TCP_DST, TransportPort.of(80))
                            .build();



                ArrayList<OFAction> actionList = new ArrayList<OFAction>();
                OFActions actions = myFactory.actions();


                OFActionOutput output = actions.buildOutput()
                    .setMaxLen(0xFFffFFff)
                    .setPort(OFPort.of(4-port))
                    .build();
                actionList.add(output);

                fmb
                .setIdleTimeout(60)
        //.setHardTimeout(5)
        .setBufferId(OFBufferId.NO_BUFFER)
        .setCookie(cookie)
        .setPriority(2)
        .setMatch(myMatch);

                FlowModUtils.setActions(fmb, actionList, sw);

                try {
                        messageDamper.write(sw, fmb.build());
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }


        }


}