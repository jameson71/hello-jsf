package utils;

//import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import java.io.Serializable;
//import javax.faces.SessionScoped;
import javax.enterprise.context.SessionScoped;

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
		System.out.println("Entered Username is:" + loginid + " and the password is: " + loginpwd);
		
		if (loginid.equalsIgnoreCase("james") || loginpwd.equalsIgnoreCase("james")) {
			navResult = "success";
		} else {
			navResult = "failure";
		}
		return navResult;
	}

}
