package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public EditServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	  User loginUser = (User) request.getSession().getAttribute("loginUser");
      if (loginUser == null) {
    	  response.sendRedirect("./");
    	  return;
      }

      String messageId = request.getParameter("message_id");
      UserMessage message = new MessageService().getMessage(messageId);

      List<String> errorMessages = new ArrayList<String>();

      if (isValid(message, loginUser, errorMessages) == false) {
    	  request.getSession().setAttribute("errorMessages", errorMessages);
    	  response.sendRedirect("./");
    	  return;
      }

      request.setAttribute("editMessage", message);

      request.getRequestDispatcher("/edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	  String messageId = request.getParameter("message_id");
	  String text = request.getParameter("text");

	  UserMessage message = new UserMessage();
	  message.setId(Integer.parseInt(messageId));
	  message.setText(text);

	  List<String> errorMessages = new ArrayList<String>();

	  if (isValid(message, errorMessages)) {
    	  new MessageService().update(message);
    	  response.sendRedirect("./");
    	  return;
      } else {
    	  request.setAttribute("errorMessages", errorMessages);
    	  request.setAttribute("editMessage", message);
    	  request.getRequestDispatcher("edit.jsp").forward(request, response);
      }

    }

    private boolean isValid(UserMessage message, User loginUser, List<String> errorMessages)  {
    	if (message == null) {
    		errorMessages.add("不正なパラメータが入力されました");
    		return false;
    	}

    	return true;
    }

    private boolean isValid(UserMessage message, List<String> errorMessages)  {
    	String text = message.getText();

    	if (StringUtils.isBlank(text)) {
    		errorMessages.add("入力してください");
    	} else if (140 < text.length()) {
    		errorMessages.add("140文字以下で入力してください");
    	}

    	return errorMessages.isEmpty();
    }
}
