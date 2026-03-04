package chapter6.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.beans.UserComment;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/index.jsp" })
public class TopServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public TopServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        boolean isShowMessageForm = false;
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user != null) {
            isShowMessageForm = true;
        }

        String startDate = request.getParameter("start");
        String endDate = request.getParameter("end");
        String userIdStr = request.getParameter("user_id");
        Integer userId = null;

        if (userIdStr != null && !StringUtils.isBlank(userIdStr)) {
            userId = Integer.parseInt(userIdStr);
        }

        List<UserMessage> messages = new MessageService().select(userId, startDate, endDate);
        List<UserComment> comments = new CommentService().select();

        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("comments", comments);
        request.setAttribute("messages", messages);
        request.setAttribute("isShowMessageForm", isShowMessageForm);
        request.getRequestDispatcher("/top.jsp").forward(request, response);
    }
}