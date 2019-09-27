package com.internousdev.olive.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.olive.dao.CartInfoDAO;
import com.internousdev.olive.dao.UserInfoDAO;
import com.internousdev.olive.dto.CartInfoDTO;
import com.internousdev.olive.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public  class LoginAction extends ActionSupport implements SessionAware{

	private String userId;
	private String password;
	private boolean keepId;
	private int logined;
	private List<String>userIdErrorMessageList;
	private List<String>passwordErrorMessageList;
	private String errorMessage;
	private List<CartInfoDTO> cartInfoDTOList;
	public Map<String, Object>session;

	UserInfoDAO userDao = new UserInfoDAO();
	CartInfoDAO cartDao = new CartInfoDAO();
	InputChecker inputChecker =new InputChecker();

	public String execute(){
		//事前準備、retをERRORで初期化
		String ret=ERROR;
		session.put("logined", 0);
		session.remove("keepId");

		if(!session.containsKey("tempUserId")){
			/*			セッションタイムアウト*/
			ret="sessionTimeout";
			return ret;
		}

		if(session.containsKey("createUserFlag")){
			//ユーザー作成からの自動遷移の場合の処理
			userId = session.get("userIdForCreateUser").toString();
			password = session.get("password").toString();
			session.remove("userIdForCreateUser");
			session.remove("password");
			session.remove("createUserFlag");

		} else if(session.containsKey("userIdForCreateUser")) {
			session.remove("userIdForCreateUser");
			session.remove("password");
			session.remove("familyName");
			session.remove("firstName");
			session.remove("familyNameKana");
			session.remove("firstNameKana");
			session.remove("sex");
			session.remove("sexList");
			session.remove("email");
		}

		/*入力項目の文字が正確に入力されているか判定*/
		userIdErrorMessageList = inputChecker.doCheck("ユーザーID", userId, 1, 8, true, false, false, true, false, false);
		passwordErrorMessageList = inputChecker.doCheck("パスワード", password, 1, 16, true, false, false, true, false, false);
		if(userIdErrorMessageList.size()>0||passwordErrorMessageList.size()>0){
			ret = ERROR;
			return ret;
		}

		if(userDao.checkExistsUser(userId,password)){
			/*ログイン認証成功・カート紐付け処理を実行するか判定*/
			userDao.login(userId, password);
			String tempUserId = session.get("tempUserId").toString();
			List<CartInfoDTO> tempCartDTO = cartDao.getCartInfoDTOList(tempUserId);

			if(tempCartDTO !=null){
				boolean changeResult = changeCart(tempCartDTO,tempUserId);
				if(!changeResult){
					ret="DBError";
					return ret;
				}
			}

		}else{
			/*ユーザーIDとパスワードがDBに存在しない*/
			ret = ERROR;
			errorMessage = "ユーザーＩＤまたはパスワードが異なります。";
			return ret;
		}

		//画面遷移先を選定
		if(session.containsKey("cartFlag")){
			session.remove("cartFlag");
			cartInfoDTOList = cartDao.getCartInfoDTOList(userId);
			int cartTotalPrice =0;
			for(CartInfoDTO dto : cartInfoDTOList){
				cartTotalPrice += dto.getTotalPrice();
			}
			session.put("cartTotalPrice", cartTotalPrice);
			ret = "cart";
		}else{
			ret = SUCCESS;
		}

		//最終設定
		session.remove("tempUserId");
		session.put("userId", userId);
		session.put("logined", 1);
		if(keepId){
			session.put("keepId", keepId);
		}

		return ret;
	}

	public boolean changeCart(List<CartInfoDTO> tempCartDTOList,String tempUserId){
		boolean ret = false;
		int accessCount = 0;

		for(CartInfoDTO tempCartDTO:tempCartDTOList){
			if(cartDao.checkExistsCart(userId, tempCartDTO.getProductId())){
				//ユーザーのカート情報に、一時ユーザーのカートと同一の商品が存在した。
				accessCount += cartDao.updateCartInfo(userId, tempCartDTO.getProductId(), tempCartDTO.getProductCount());
				cartDao.delete(tempUserId, String.valueOf(tempCartDTO.getProductId()));
			}else{
				//ユーザーのカート情報に、一時ユーザーのカートと同一の商品は存在しなかった。
				accessCount += cartDao.linkUserId(userId, tempUserId, tempCartDTO.getProductId());
			}
		}
		if(accessCount == tempCartDTOList.size()){
			//アクセス回数が正確かどうかを判定する。正確ならばtrueを返し、不正ならfalseが帰る。
			ret = true;
		}
		return ret;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isKeepId() {
		return keepId;
	}

	public void setKeepId(boolean keepId) {
		this.keepId = keepId;
	}

	public int getLogined() {
		return logined;
	}

	public void setLogined(int logined) {
		this.logined = logined;
	}

	public List<String> getUserIdErrorMessageList() {
		return userIdErrorMessageList;
	}

	public void setUserIdErrorMessageList(List<String> userIdErrorMessageList) {
		this.userIdErrorMessageList = userIdErrorMessageList;
	}

	public List<String> getPasswordErrorMessageList() {
		return passwordErrorMessageList;
	}

	public void setPasswordErrorMessageList(List<String> passwordErrorMessageList) {
		this.passwordErrorMessageList = passwordErrorMessageList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<CartInfoDTO> getCartInfoDTOList() {
		return cartInfoDTOList;
	}

	public void setCartInfoDTOList(List<CartInfoDTO> cartInfoDTOList) {
		this.cartInfoDTOList = cartInfoDTOList;
	}

}
