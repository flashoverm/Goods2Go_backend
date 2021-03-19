package com.goods2go.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.goods2go.config.MailConfig;
import com.goods2go.config.MessageBrokerConfig;
import com.goods2go.controllers.errors.ConflictException;
import com.goods2go.controllers.errors.ForbiddenException;
import com.goods2go.controllers.errors.NotFoundException;
import com.goods2go.models.Address;
import com.goods2go.models.NotificationMessage;
import com.goods2go.models.Shipment;
import com.goods2go.models.User;
import com.goods2go.models.VerificationToken;
import com.goods2go.models.enums.NotificationMessageType;
import com.goods2go.models.enums.Role;
import com.goods2go.models.util.Mail;
import com.goods2go.models.util.ModelFilters;
import com.goods2go.repositories.AddressDao;
import com.goods2go.repositories.PaymentInformationDao;
import com.goods2go.repositories.UserDao;
import com.goods2go.repositories.VerificationTokenDao;
import com.goods2go.repositories.NotificationMessageDao;

@CrossOrigin //Allow cross-origin for development
@RestController
@RequestMapping("/user")
public class UserController {
	
    @Autowired
	SimpMessageSendingOperations template;
	
	@Autowired
	NotificationMessageDao notificationMessageDao;
    
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private PaymentInformationDao paymentInformationDao;
	
	@Autowired
	private VerificationTokenDao verificationTokenDao;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Value("${mail.config.server.address}")
	public void setServerAddress(String serverAddress) {
		MailConfig.SERVERADDRESS = serverAddress;
	}
	
	@Value("${mail.config.webserver.address}")
	public void setWebserverAddress(String webserverAddress) {
		MailConfig.WEBSERVERADDRESS = webserverAddress;
	}

	@Value("${mail.config.verification.path}")
	public void setVerificationPath(String verificationPath) {
		MailConfig.VERIFICATION_PATH = verificationPath;
	}
		
	@Value("${mail.config.verification.url}")
	public void setVerificationURL(String verificationURL) {
		MailConfig.VERIFICATION_URL = verificationURL;
	}
	
	@Value("${mail.config.verification.request.path}")
	public void setVerificationRequestPath(String verificationRequestPath) {
		MailConfig.VERIFICATION_REQUEST_PATH = verificationRequestPath;
	}
	
	@Value("${mail.config.verification.request.url}")
	public void setVerificationRequestURL(String verificationRequestURL) {
		MailConfig.VERIFICATION_REQUEST_URL = verificationRequestURL;
	}
	
	@Value("${mail.config.sender.address}")
	public void setSenderAddress(String senderAddress) {
		MailConfig.SENDERADDRESS = senderAddress;
	} 
	
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public MappingJacksonValue getUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(dbUser));
	}
	
	@RequestMapping(value="/addresshistory", method=RequestMethod.GET)
	public MappingJacksonValue getUserAddressHistory() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		return ModelFilters.filterButKeepAllFields(dbUser.getAddresshistory());
	}
	
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public MappingJacksonValue getAllUser() {
		return ModelFilters.filterButKeepAllFields(userDao.findAll());
	}
	
	// get all deliverer applicants
	@RequestMapping(value="/delivererApplicants", method=RequestMethod.GET)
	public MappingJacksonValue getDelivererApplicants() {
		return ModelFilters.filterButKeepAllFields(userDao.findByDelivererstatuspending(true));
	}
	
	// get only users with roles SENDER or DELIVERER
	@RequestMapping(value="/customers", method=RequestMethod.GET)
	public MappingJacksonValue getCustomers() {
		List<Role> roles = new ArrayList<Role>();
		roles.add(Role.DELIVERER);
		roles.add(Role.SENDER);
		return ModelFilters.filterButKeepAllFields(userDao.findByRoleIn(roles));
	}
	
	// get all admin users
	@RequestMapping(value="/admins", method=RequestMethod.GET)
	public MappingJacksonValue getAdmins() {
		return ModelFilters.filterButKeepAllFields(userDao.findByRole(Role.ADMIN));
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public MappingJacksonValue signup(@RequestBody User user) {
		
		User registered = userDao.findOneByEmail(user.getEmail());
		if(registered != null) {
	    	throw new ConflictException();
		}
		
		user.setActive(true);
		user.setMailvalidated(false);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRole(Role.SENDER);
	
    	if(user.getIdentno() != null && !user.getIdentno().equals("")) {
			user.setDelivererstatuspending(true);
		}

		User saved = userDao.save(user);
		
		VerificationToken token = verificationTokenDao.save(new VerificationToken(saved));
		Mail.sendCustomVerificationMail(token, saved);

		return ModelFilters.filterButKeepAllFields(prepareUserResponse(saved));
	}
	
	// Create new admin user from admin interface
	@RequestMapping(value="/createAdminUser", method=RequestMethod.POST)
	public MappingJacksonValue createAdminUser(@RequestBody User user) {
		
		User registered = userDao.findOneByEmail(user.getEmail());
		if(registered != null) {
	    	throw new ConflictException();
		}
		
		user.setActive(true);
		user.setMailvalidated(false);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRole(Role.ADMIN);
    	
		User saved = userDao.save(user);
		
		VerificationToken token = verificationTokenDao.save(new VerificationToken(saved));
		Mail.sendCustomVerificationMail(token, saved);

		return ModelFilters.filterButKeepAllFields(prepareUserResponse(saved));
	}
	
	@RequestMapping(value="${mail.config.verification.request.path}"+"{email:.+}", method=RequestMethod.GET)
	public ResponseEntity requestVerificationMail(@PathVariable(value="email") String email) {
		
		User user = userDao.findOneByEmail(email);
		if(user == null) {
			System.out.println("Mail: " + email);
			throw new NotFoundException();
		}
		if(user.isMailvalidated()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
			    			
		VerificationToken token = verificationTokenDao.save(new VerificationToken(user));
		Mail.sendCustomVerificationMail(token, user);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="${mail.config.verification.path}" + "{token}", method=RequestMethod.GET)
	public void verifyMail(@PathVariable(value="token") String token, HttpServletResponse  response) {
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	
		VerificationToken dbToken = verificationTokenDao.findOneByToken(token);
		if(dbToken != null) {
			if(dbToken.isValid()) {
				User user = userDao.findOne(dbToken.getUser().getId());
				user.setMailvalidated(true);
				userDao.save(user);
				verificationTokenDao.delete(dbToken.getId());
				
				response.addHeader("Location", MailConfig.WEBSERVERADDRESS+"/verification");
				return;
			}
			response.addHeader("Location", MailConfig.WEBSERVERADDRESS+"/verificationfailure");
			return;
		}
		response.addHeader("Location", MailConfig.WEBSERVERADDRESS+"/verificationfailure");
		return;
	}
	
	@RequestMapping(value="/password", method=RequestMethod.POST)
	public MappingJacksonValue changePassword(@RequestBody User user) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(userDao.save(dbUser)));
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public MappingJacksonValue update(@RequestBody User user) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		
		if(user.getDisplayname() != null) {
			dbUser.setDisplayname(user.getDisplayname());
		}
		if(user.getDefaultaddress() != null) {
			Address dbAddress = queryAddress(user.getDefaultaddress());
			dbUser.setDefaultaddress(dbAddress);
		}
		
		User returnUser;
		if(user.getPaymentInformation() != null) {
			long oldPaymentInfoId = 0;
			if(dbUser.getPaymentInformation() != null) {
				oldPaymentInfoId = dbUser.getPaymentInformation().getId();
			}
			
			dbUser.setPaymentInformation(user.getPaymentInformation());
			returnUser = userDao.save(dbUser);
			
			if(oldPaymentInfoId > 0) {
				paymentInformationDao.delete(oldPaymentInfoId);
			}
		} else {
			returnUser = userDao.save(dbUser);
		}
	
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(returnUser));
	}
	
	@RequestMapping(value="/displayname", method=RequestMethod.POST)
	public MappingJacksonValue changeDisplayName(@RequestBody User user) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		dbUser.setDisplayname(user.getDisplayname());

		return ModelFilters.filterButKeepAllFields(prepareUserResponse(userDao.save(dbUser)));
	}
	
	@RequestMapping(value="/defaultaddress", method=RequestMethod.POST)
	public MappingJacksonValue changeDefaultAddress(@RequestBody User user) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		Address dbAddress = queryAddress(user.getDefaultaddress());
		dbUser.setDefaultaddress(dbAddress);

		return ModelFilters.filterButKeepAllFields(prepareUserResponse(userDao.save(dbUser)));
	}
	
	@RequestMapping(value="/paymentinformation", method=RequestMethod.POST)
	public MappingJacksonValue changePaymentInformation(@RequestBody User user) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		
		long oldPaymentInfoId = 0;
		if(dbUser.getPaymentInformation() != null) {
			oldPaymentInfoId = dbUser.getPaymentInformation().getId();
		}
		
		dbUser.setPaymentInformation(user.getPaymentInformation());
		User returnUser = userDao.save(dbUser);
		
		if(oldPaymentInfoId > 0) {
			paymentInformationDao.delete(oldPaymentInfoId);
		}
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(returnUser));
	}
	
	@RequestMapping(value="/becomedeliverer", method=RequestMethod.POST)
	public MappingJacksonValue becomeDeliverer(@RequestBody User user) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User dbUser = userDao.findOneByEmail(auth.getName());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		
		if(dbUser.getRole().equals(Role.DELIVERER)) {
			return ModelFilters.filterButKeepAllFields(user);
		}
		if(user.getIdentno() != null && !user.getIdentno().equals("")) {
			dbUser.setIdentno(user.getIdentno());
			dbUser.setIdenttype(user.getIdenttype());
			dbUser.setPaymentInformation(user.getPaymentInformation());
			dbUser.setDelivererstatuspending(true);
		}
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(userDao.save(dbUser)));
	}
	
	
	@RequestMapping(value="/verifydeliverer", method=RequestMethod.POST)
	public MappingJacksonValue verifyDelivererRole(@RequestBody User user) {
		//TODO endpoint only for administration (otherwise throw new ForbiddenException();)
		User dbUser = userDao.findOneByEmail(user.getEmail());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		dbUser.setIdentconfirmed(true);
		dbUser.setRole(Role.DELIVERER);
		dbUser.setDelivererstatuspending(false);
		dbUser = userDao.save(dbUser);
		Mail.sendDelivererVerifiedMail(dbUser);
				
	    sendStatusNotification(
	    		dbUser.getEmail(), NotificationMessageType.DelivererVerified, 
	    		dbUser.getId(), "successfully verified");
		
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(dbUser));
	}
	
	@RequestMapping(value="/dontverifydeliverer", method=RequestMethod.POST)
	public MappingJacksonValue dontVerifyDelivererRole(@RequestBody User user) {
		//TODO endpoint only for administration (otherwise throw new ForbiddenException();)
		User dbUser = userDao.findOneByEmail(user.getEmail());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		dbUser.setIdentconfirmed(false);
		dbUser.setRole(Role.SENDER);
		dbUser.setDelivererstatuspending(false);
		dbUser = userDao.save(dbUser);
		Mail.sendDelivererNotVerifiedMail(dbUser);
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(dbUser));
	}
	
	@RequestMapping(value="/block", method=RequestMethod.POST)
	public MappingJacksonValue blockUser(@RequestBody User user) {
		//TODO endpoint only for administration (otherwise throw new ForbiddenException();)
		User dbUser = userDao.findOneByEmail(user.getEmail());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		dbUser.setActive(false);
		dbUser = userDao.save(dbUser);
		Mail.sendUserBlockedMail(dbUser);
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(dbUser));
	}
	
	@RequestMapping(value="/unblock", method=RequestMethod.POST)
	public MappingJacksonValue unblockUser(@RequestBody User user) {
		//TODO endpoint only for administration (otherwise throw new ForbiddenException();)
		User dbUser = userDao.findOneByEmail(user.getEmail());
		if(dbUser == null) {
			throw new NotFoundException();
		}
		dbUser.setActive(true);
		dbUser = userDao.save(dbUser);
		Mail.sendUserUnblockedMail(dbUser);
		return ModelFilters.filterButKeepAllFields(prepareUserResponse(dbUser));
	}
	
	//TODO redundant
	private Address queryAddress(Address address) {
		Address updatedAddress = addressDao.findOneByFirstnameAndLastnameAndStreetAndStreetnoAndPostcodeAndCityAndCountryAndCompanyname(
				address.getFirstname(), address.getLastname(), address.getStreet(), address.getStreetno(), 
				address.getPostcode(), address.getCity(), address.getCountry(), address.getCompanyname());
		if(updatedAddress == null) {
			updatedAddress = addressDao.save(address);
		}
		return updatedAddress;
	}
	
	private User prepareUserResponse(User user) {
		user.setPassword("");
		return user;
	}
	
	//TODO redundant: sendRequestNotification
	private void sendStatusNotification(String recipient, NotificationMessageType type, long subjectId, String subjectDescription) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage(recipient, type, subjectId, subjectDescription));
		
		template.convertAndSendToUser(recipient, MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + recipient);
	}
	
} 


