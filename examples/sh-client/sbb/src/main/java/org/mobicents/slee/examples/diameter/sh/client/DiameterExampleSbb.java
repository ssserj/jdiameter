package org.mobicents.slee.examples.diameter.sh.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientProvider;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.sh.client.ShClientSubscriptionActivityImpl;

/**
 * 
 * DiameterExampleSbb.java
 *
 * <br>Super project:  mobicents
 * <br>11:34:16 PM May 26, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 */
public abstract class DiameterExampleSbb implements javax.slee.Sbb {

  private static Logger logger = Logger.getLogger( DiameterExampleSbb.class );

  private SbbContext sbbContext = null; // This SBB's context

  private Context myEnv = null; // This SBB's environment

  private ShClientProvider provider = null;
  
  private ShClientMessageFactory messageFactory = null;
  private DiameterShAvpFactory avpFactory = null;
  private ShClientActivityContextInterfaceFactory acif=null;
  private TimerFacility timerFacility = null;
  
  private String originIP = "127.0.0.1";
  private String originPort = "1812";
  private String originRealm = "mobicents.org";

  private String destinationIP = "127.0.0.1";
  private String destinationPort = "3868";
  private String destinationRealm = "mobicents.org";

  
  public void setSbbContext( SbbContext context )
  {
    logger.info( "sbbRolledBack invoked." );

    this.sbbContext = context;

    try
    {
      myEnv = (Context) new InitialContext().lookup( "java:comp/env" );
      
      provider = (ShClientProvider) myEnv.lookup("slee/resources/diameter-sh-client-ra-interface");
      logger.info( "Got Provider:" + provider );
      
      messageFactory = provider.getClientMessageFactory();
      logger.info( "Got Message Factory:" + provider );
      
      avpFactory = provider.getClientAvpFactory();
      logger.info( "Got AVP Factory:" + provider );
      
      acif=(ShClientActivityContextInterfaceFactory) myEnv.lookup("slee/resources/JDiameterShClientResourceAdaptor/java.net/0.8.1/acif");
      
      // Get the timer facility
      timerFacility  = (TimerFacility) myEnv.lookup("slee/facilities/timer");
    }
    catch ( Exception e )
    {
      logger.error( "Unable to set sbb context.", e );
    }
  }

  public void unsetSbbContext()
  {
    logger.info( "unsetSbbContext invoked." );

    this.sbbContext = null;
  }

  public void sbbCreate() throws javax.slee.CreateException
  {
    logger.info( "sbbCreate invoked." );
  }

  public void sbbPostCreate() throws javax.slee.CreateException
  {
    logger.info( "sbbPostCreate invoked." );
  }

  public void sbbActivate()
  {
    logger.info( "sbbActivate invoked." );
  }

  public void sbbPassivate()
  {
    logger.info( "sbbPassivate invoked." );
  }

  public void sbbRemove()
  {
    logger.info( "sbbRemove invoked." );
  }

  public void sbbLoad()
  {
    logger.info( "sbbLoad invoked." );
  }

  public void sbbStore()
  {
    logger.info( "sbbStore invoked." );
  }

  public void sbbExceptionThrown( Exception exception, Object event, ActivityContextInterface activity )
  {
    logger.info( "sbbRolledBack invoked." );
  }

  public void sbbRolledBack( RolledBackContext context )
  {
    logger.info( "sbbRolledBack invoked." );
  }

  protected SbbContext getSbbContext()
  {
    logger.info( "getSbbContext invoked." );

    return sbbContext;
  }

  // ##########################################################################
  // ##                          EVENT HANDLERS                              ##
  // ##########################################################################

  public void onServiceStartedEvent( javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci )
  {
    logger.info( "onServiceStartedEvent invoked." );

    try
    {
      // check if it's my service that is starting
      ServiceActivity sa = ( (ServiceActivityFactory) myEnv.lookup( "slee/serviceactivity/factory" ) ).getActivity();
      if( sa.equals( aci.getActivity() ) )
      {
        logger.info( "################################################################################" );
        logger.info( "### D I A M E T E R   E X A M P L E   A P P L I C A T I O N  :: S T A R T E D ##" );
        logger.info( "################################################################################" );
        
        messageFactory = provider.getClientMessageFactory();
        avpFactory = provider.getClientAvpFactory();
        
        logger.info( "Performing sanity check..." );
        logger.info( "Provider [" + provider + "]" );
        logger.info( "Message Factory [" + messageFactory + "]" );
        logger.info( "AVP Factory [" + avpFactory + "]" );
        logger.info( "Check completed. Result: " + ((provider != null ? 1 : 0) + (messageFactory != null ? 1 : 0) + (avpFactory != null ? 1 : 0)) + "/3" );

        logger.info( "Connected to " + provider.getPeerCount() + " peers." );
        
        for(DiameterIdentity peer : provider.getConnectedPeers())
          logger.info( "Connected to Peer[" +  peer.toString() + "]" );

        TimerOptions options = new TimerOptions();
        
        timerFacility.setTimer(aci, null, System.currentTimeMillis() + 30000, options);
        
        /* Uncomment for basic message sending testing (DWR/DWA)

        try
        {
          DiameterAvp avp_DestHost = avpFactory.createAvp( Avp.DESTINATION_HOST, "127.0.0.1".getBytes() );
          DiameterAvp avp_DestRealm = avpFactory.createAvp( Avp.DESTINATION_REALM, "mobicents.org".getBytes() );

          DiameterAvp avp_HostIPAddress = avpFactory.createAvp( Avp.HOST_IP_ADDRESS, ("0x0001" + "7f000001").getBytes() );
          
          DiameterAvp avp_VendorId = avpFactory.createAvp( Avp.VENDOR_ID, "193".getBytes() );
          DiameterAvp avp_ProductName = avpFactory.createAvp( Avp.PRODUCT_NAME, "jDiameter".getBytes() );
         
          DiameterAvp[] avps = new DiameterAvp[]{avp_DestHost, avp_DestRealm, avp_HostIPAddress, avp_VendorId, avp_ProductName};

          logger.info( "Creating Custom Message..." );
          DiameterMessage ms = messageFactory.createDeviceWatchdogRequest(avps);
          logger.info( "Created Custom Message[" + ms + "]" );
         
          logger.info( "Sending Custom Message..." );
          provider.createActivity().sendMessage( ms );
          logger.info( "Sent Custom Message[" + ms + "]" );
        }
        catch (Exception e) {
          logger.error( "Not working...", e );
        }
        
        */
      }
    }
    catch ( Exception e )
    {
      logger.error( "Unable to handle service started event...", e );
    }
  }
  
  public void onTimerEvent(TimerEvent event, ActivityContextInterface aci)
  {
	  
	    Properties props = new Properties();
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("example.properties"));
			this.originIP = props.getProperty("origin.ip") == null ? this.originIP : props.getProperty("origin.ip");
			this.originPort = props.getProperty("origin.port") == null ? this.originPort : props.getProperty("origin.port");
			this.originRealm = props.getProperty("origin.realm") == null ? this.originRealm : props.getProperty("origin.realm");

			this.destinationIP = props.getProperty("destination.ip") == null ? this.destinationIP : props.getProperty("destination.ip");
			this.destinationPort = props.getProperty("destination.port") == null ? this.destinationPort : props.getProperty("destination.port");
			this.destinationRealm = props.getProperty("destination.realm") == null ? this.destinationRealm : props.getProperty("destination.realm");
			
			boolean b = Boolean.parseBoolean(props.getProperty("send.udr"));
			if(b)
			{
				
				doSimpleTestsSendUDR();
			}else
			{
				logger.info(" On TimerEvent: Skipping UDR send");
			}
			b = Boolean.parseBoolean(props.getProperty("send.snr"));
			if(b)
			{
				doSimpleTestSendSNR();
			}else
			{
				logger.info(" On TimerEvent: Skipping SNR send");
			}
			b = Boolean.parseBoolean(props.getProperty("send.pur"));
			if(b)
			{
				doSimpleTestSendPUR();
			}else
			{
				logger.info(" On TimerEvent: Skipping PUR send");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	 
  }
  
  private void doSimpleTestsSendUDR()
  {

	  logger.info(" On TimerEvent: ShClient activity and messages creation.");
	  try {
			ShClientActivity basicClientActivity=this.provider.createShClientActivity();
			
			logger.info(" On TimerEvent: activity created");
			
			ActivityContextInterface localACI=acif.getActivityContextInterface(basicClientActivity);
			logger.info(" On TimerEvent: ACI created for basicClientActivity");
			
			localACI.attach(getSbbContext().getSbbLocalObject());
			
			
			DiameterIdentity[] peers=provider.getConnectedPeers();
			
			for(DiameterIdentity peer: peers)
			{
				logger.info(" On TimerEvent: Connected Peer: "+peer.toString());
			}
			
			logger.info(" On TimerEvent: creating UDR");
			
			UserDataRequest udr=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createUserDataRequest();
			
			List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
		  
		      
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, ("aaa://" + destinationIP + ":"+destinationPort).getBytes() ));
		      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, destinationRealm.getBytes() ));
		      UserIdentityAvp ui=avpFactory.createUserIdentity();
		      ui.setPublicIdentity("sip:subscriber@mobicents.org");
		      
		      avps.add(ui);
		    
		      udr.setExtensionAvps(avps.toArray(new DiameterAvp[avps.size()]));
		      udr.setAuthSessionState(AuthSessionStateType.STATE_MAINTAINED);
		      udr.setDataReference(DataReferenceType.IMS_PUBLIC_IDENTITY);
		      logger.info(" On TimerEvent: Sending message:\n"+udr);
		      basicClientActivity.sendUserDataRequest(udr);
		      
			logger.info(" On TimerEvent: Message send");
			
			
			
		} catch (Exception e) {
		  logger.error( "Failure trying to create/sen UDR.", e );
		}
  }

  private void doSimpleTestSendSNR()
  {
	  try {
	  ShClientSubscriptionActivity shClientSubscriptionActivity=this.provider.createShClientSubscriptionActivity();
		logger.info(" On TimerEvent: Client Subscrition Activity created");
		
		shClientSubscriptionActivity.getDiameterAvpFactory();
		shClientSubscriptionActivity.getDiameterMessageFactory();
		
		logger.info(" On TimerEvent: Subscription activity methods tested");
		ActivityContextInterface localACI=this.acif.getActivityContextInterface(shClientSubscriptionActivity);
		localACI.attach(getSbbContext().getSbbLocalObject());
		logger.info(" On TimerEvent: Subscription activity acif created");
		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
		SubscribeNotificationsRequest snr=((ShClientMessageFactory)shClientSubscriptionActivity.getDiameterMessageFactory()).createSubscribeNotificationsRequest();
		//< Subscribe-Notifications-Request > ::=	< Diameter Header: 308, REQ, PXY, 16777217 >
		//				< Session-Id >
		//				{ Vendor-Specific-Application-Id }
	 	//				{ Auth-Session-State }
		snr.setAuthSessionState(AuthSessionStateType.STATE_MAINTAINED);
		//				{ Origin-Host }
		//				{ Origin-Realm }
		//				[ Destination-Host ]
	      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, ("aaa://" + destinationIP + ":"+destinationPort).getBytes() ));
		//				{ Destination-Realm }
	      avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, destinationRealm.getBytes() ));
		//				*[ Supported-Features ]
		//				{ User-Identity }
	      UserIdentityAvp ui=avpFactory.createUserIdentity();
	      ui.setPublicIdentity("sip:subscriber@mobicents.org");
	      avps.add(ui);
		//				[ Wildcarded-PSI ]
		//				[ Wildcarded-IMPU ]
		//				*[ Service-Indication ]
		//				[ Send-Data-Indication ]
		//				[ Server-Name ]
		//				{ Subs-Req-Type }
	      snr.setSubsReqType(SubsReqType.SUBSCRIBE);
		//				*{ Data-Reference }
	      //Its enumerated: 0 == Whole data
	      DiameterAvp avp=avpFactory.getBaseFactory().createAvp(MessageFactory._SH_VENDOR_ID,DiameterShAvpCodes.DATA_REFERENCE, 0);
	      avps.add(avp);
		//				[ Identity-Set ]
		//				[ Expiry-Time ]
	      //We can user setters, but this is faster :)
	      snr.setExtensionAvps(avps.toArray(avps.toArray(new DiameterAvp[avps.size()])));
	      
	      shClientSubscriptionActivity.sendSubscriptionNotificationRequest(snr);


	  } catch (Exception e) {
	    logger.error( "Failure creating/sending SNR.", e );
		}
  }
  private void doSimpleTestSendPUR()
  {
	  logger.info(" On TimerEvent: ShClient activity and messages creation.");
	  try {
			ShClientActivity basicClientActivity=this.provider.createShClientActivity();
			
			logger.info(" On TimerEvent: activity created");
			
			ActivityContextInterface localACI=acif.getActivityContextInterface(basicClientActivity);
			logger.info(" On TimerEvent: ACI created for basicClientActivity");
			
			localACI.attach(getSbbContext().getSbbLocalObject());
			
			
			DiameterIdentity[] peers=provider.getConnectedPeers();
			
			for(DiameterIdentity peer: peers)
			{
				logger.info(" On TimerEvent: Connected Peer: "+peer.toString());
			}
			
			logger.info(" On TimerEvent: creating PUR");
			
			ProfileUpdateRequest pur=((ShClientMessageFactory)basicClientActivity.getDiameterMessageFactory()).createProfileUpdateRequest();
			
			List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
		  
//			< Profile-Update-Request > ::=		< Diameter Header: 307, REQ, PXY, 16777217 >
//			< Session-Id >
//			{ Vendor-Specific-Application-Id }
//			{ Auth-Session-State }\
			pur.setAuthSessionState(AuthSessionStateType.STATE_MAINTAINED);
//			{ Origin-Host }
//			{ Origin-Realm }
//			[ Destination-Host ]
			avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_HOST, ("aaa://" + destinationIP + ":"+destinationPort).getBytes() ));
//			{ Destination-Realm }
			 avps.add(avpFactory.getBaseFactory().createAvp(Avp.DESTINATION_REALM, destinationRealm.getBytes() ));
//			*[ Supported-Features ]
//			{ User-Identity }
			UserIdentityAvp ui = this.avpFactory.createUserIdentity();
			ui.setPublicIdentity("sip:pomyslowy.romek@asn.media.com");
			pur.setUserIdentity(ui);
//			[ Wildcarded-PSI ]
//			[ Wildcarded-IMPU ]
//			{ Data-Reference }
			pur.setDataReference(DataReferenceType.IMS_PUBLIC_IDENTITY);
//			{ User-Data }
			pur.setUserData("<xml>  some data</xml>");
//			*[ AVP ]
//			*[ Proxy-Info ]
//			*[ Route-Record ]

		    
		      pur.setExtensionAvps(avps.toArray(new DiameterAvp[avps.size()]));
		    
		      logger.info(" On TimerEvent: Sending message:\n"+pur);
		      basicClientActivity.sendProfileUpdateRequest(pur);
		      
			logger.info(" On TimerEvent: Message send");
			
			
			
		} catch (Exception e) {
		  logger.error( "Failure trying to create/send PUR.", e );
		}
  }
  // ###################################
  // #  REQEUSTS - PNR, this is client #
  // ###################################
 
  										
  public void onPushNotificationRequest(net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest pnr, ActivityContextInterface aci)
  {
	  try
    {
	    logger.info( "Push-Notification-Request activity["+aci.getActivity()+"] received.\n"+pnr );
	    
	    // Let's be nice and say OK :)
	    ShClientSubscriptionActivityImpl shActivity = (ShClientSubscriptionActivityImpl) aci.getActivity();

	    shActivity.sendPushNotificationAnswer( 2001, false );
    }
    catch ( IOException e ) {
      logger.error( "Failure while creating/sending PNA.", e );
    }
  }
  
  
  // ###################################
  // # ASNWERS - PUA, SNA, UDA         #
  // ###################################
  
  public void onProfileUpdateAnswer(net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer pua, ActivityContextInterface aci)
  {
	  logger.info( "Profile-Update-Answer activity["+aci.getActivity()+"] received.\n"+pua );
  }
  
  public void onSubscriptionNotificationsAnswer(net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer sna, ActivityContextInterface aci)
  {
	  logger.info( "Subscription-Notifications-Answer activity["+aci.getActivity()+"] received.\n"+sna );
	  logger.info( "Subscription-Notifications-Answer Result-Code["+sna.getResultCode()+"].");
  }
  
  public void onUserDataRequestAnswer(net.java.slee.resource.diameter.sh.client.events.UserDataAnswer uda, ActivityContextInterface aci)
  {
	  logger.info( "User-Data-Answer activity["+aci.getActivity()+"] received.\n"+uda );
  }

  public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		logger.info( " Activity Ended["+aci.getActivity()+"]" );
	}

}
