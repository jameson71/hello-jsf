package utils;

//import javax.faces.bean.ManagedBean;
import javax.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;



import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;
//import javax.faces.SessionScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

//@ManagedBean 
@Named 
@SessionScoped
public class LoginBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String loginid;
	private String loginpwd;

	
	public String getLoginid() {
		return loginid;
	}
	
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	
	public String getLoginpwd() {
		return loginpwd;
	}
	
	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}
	
	public String validateUserLogin() {
		String navResult = "";
		String domainName = "elizabethjamesandnatalie.com";
		Hashtable props = new Hashtable();
		System.out.println("Entered Username is:" + loginid + " and the password is: " + loginpwd);
		
		/* if (loginid.equalsIgnoreCase("james") || loginpwd.equalsIgnoreCase("james")) {
			navResult = "success";
		} else {
			navResult = "failure";
		}
		return navResult; */
		
        // bind by using the specified username/password
		//most code came from here: https://stackoverflow.com/questions/390150/authenticating-against-active-directory-with-java-on-linux
		//with some updates from here: https://docs.oracle.com/javase/tutorial/jndi/ldap/authentication.html
        String principalName = loginid + "@" + domainName;
        System.out.println("Principlename is: " + principalName);
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        props.put(Context.SECURITY_CREDENTIALS, loginpwd);
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //props.put(Context.PROVIDER_URL, "ldap://ad.elizabethjamesandnatalie.com:389/DC=elizabethjamesandnatalie,DC=com");
        props.put(Context.PROVIDER_URL, "ldap://ad.elizabethjamesandnatalie.com:389/");
        

        try {
            // Create the initial context
            DirContext ctx = new InitialDirContext(props);
            System.out.println("Authentication succeeded!");
            navResult = "success";

            // locate this user's record
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> renum = ctx.search(toDC(domainName),
                    "(& (userPrincipalName=" + principalName + ")(objectClass=user))", controls);
            if (!renum.hasMore()) {
                System.out.println("Cannot locate user information for " + loginid);
                System.exit(1);
            }
            SearchResult result = renum.next();

            List<String> groups = new ArrayList<String>();
            Attribute memberOf = result.getAttributes().get("memberOf");
            if (memberOf != null) {// null if this user belongs to no group at all
                for (int i = 0; i < memberOf.size(); i++) {
                    Attributes atts = ctx.getAttributes(memberOf.get(i).toString(), new String[] { "CN" });
                    Attribute att = atts.get("CN");
                    groups.add(att.get().toString());
                }
            }

            ctx.close();

            System.out.println();
            System.out.println("User belongs to: ");
            Iterator ig = groups.iterator();
            while (ig.hasNext()) {
                System.out.println("   " + ig.next());
            }

        } catch (AuthenticationException a) {
            System.out.println("Authentication failed: " + a);
            navResult = "failure";
            //System.exit(1);
        } catch (NamingException e) {
            System.out.println("Failed to bind to LDAP / get account information: " + e);
            navResult = "failure";
            //System.exit(1);
        }
        return navResult;
    }
	
    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if (token.length() == 0)
                continue; // defensive check
            if (buf.length() > 0)
                buf.append(",");
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }
    
    public void logout() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        ec.redirect(ec.getRequestContextPath() + "/index.xhtml");
    }
    
	

}
