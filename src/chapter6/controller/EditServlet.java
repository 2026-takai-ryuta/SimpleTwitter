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

import chapter6.beans.Message;
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

	  Message message = null;
      String messageStrId = request.getParameter("message_id");
  	  if (!StringUtils.isBlank(messageStrId) && messageStrId.matches("^[0-9]+$")) {
	      int messageId = Integer.parseInt(messageStrId);
  	      message = new MessageService().select(messageId);
  	  }

      if (message == null) {
    	  List<String> errorMessages = new ArrayList<String>();
    	  errorMessages.add("不正なパラメータが入力されました");
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

	  Message message = new Message();


	  List<String> errorMessages = new ArrayList<String>();

	  if (!(isValid(text, errorMessages))) {
		  int id = Integer.parseInt(messageId);
		  message = new MessageService().select(id);
    	  request.setAttribute("errorMessages", errorMessages);
    	  request.setAttribute("editMessage", message);
    	  request.getRequestDispatcher("edit.jsp").forward(request, response);
    	  return;
      }

	  message.setId(Integer.parseInt(messageId));
	  message.setText(text);
	  new MessageService().update(message);
	  response.sendRedirect("./");

    }

    private boolean isValid(String text, List<String> errorMessages)  {

    	if (StringUtils.isBlank(text)) {
    		errorMessages.add("入力してください");
    	} else if (140 < text.length()) {
    		errorMessages.add("140文字以下で入力してください");
    	}

    	return errorMessages.isEmpty();
    }
}
