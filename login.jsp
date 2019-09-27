<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="./css/style.css">
<link rel="stylesheet" href="./css/error.css">
<link rel="stylesheet" href="./css/form.css">
<link rel="stylesheet" href="./css/table.css">
<title>ログイン画面</title>
</head>
<body>
	<jsp:include page="header.jsp" />
	<div id="contents">
		<h1>ログイン画面</h1>
		<s:form action="LoginAction">

			<s:if test="errorMessage != null">
				<div class="error">
					<div class="error-message">
						<s:property value="errorMessage" />
						<br>
					</div>
				</div>
			</s:if>
			<s:elseif
				test="!userIdErrorMessageList.isEmpty()||!passwordErrorMessageList.isEmpty()">
				<s:if test="!userIdErrorMessageList.isEmpty()">
				<div class="error">
					<div class="error-message">
						<s:iterator value="userIdErrorMessageList"
							var="userIdErrorMessage">
							<s:property />
							<br>
						</s:iterator>
					</div>
				</div>
				</s:if>
				<s:if test="!passwordErrorMessageList.isEmpty()">
				<div class="error">
					<div class="error-message">
						<s:iterator value="passwordErrorMessageList"
							var="passwordErrorMessage">
							<s:property />
							<br>
						</s:iterator>
					</div>
				</div>
				</s:if>
			</s:elseif>
			<s:else>
			</s:else>
			<table class="vertical-list">
				<tr>
					<th><s:label value="ユーザーID" /></th>
					<s:if test="#session.keepId == true">
						<td><s:textfield name="userId" placeholder="ユーザーID"
								value="%{session.userId}" class="form" /></td>
					</s:if>
					<s:else>
						<td><s:textfield name="userId" placeholder="ユーザーID"
								class="form" /></td>
					</s:else>
				</tr>

				<tr>
					<th><s:label value="パスワード" /></th>
					<td><s:password name="password" placeholder="パスワード"
							class="form" /></td>
				</tr>
			</table>

			<div class="upside">
				<s:if test="#session.keepId == true">
					<s:checkbox name="keepId" checked="checked" class="check_box" />
					<s:label value="ユーザーIDを保存" />
				</s:if>
				<s:else>
					<s:checkbox name="keepId" class="check_box" />
					<s:label value="ユーザーIDを保存" />

				</s:else>
			</div>
			<div class="downside">
				<div class="submit_button">
					<s:submit class="button" value="ログイン" />
				</div>
			</div>

		</s:form>
		<div class="downside">
			<s:form action="CreateUserAction">
				<div class="submit_button">
					<s:submit class="button" value="新規ユーザー登録" />
				</div>
			</s:form>

			<s:form action="ResetPasswordAction">
				<div class="submit_button">
					<s:submit class="button" value="パスワード再設定" />
				</div>
			</s:form>
		</div>
	</div>
</body>
</html>