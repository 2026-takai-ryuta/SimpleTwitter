package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;

@WebFilter("/*")
public class EncodingFilter implements Filter {

	public static String INIT_PARAMETER_NAME_ENCODING = "encoding";

	public static String DEFAULT_ENCODING = "UTF-8";

	private String encoding;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(encoding);
		}

		HttpSession session = httpRequest.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        String servletPath = httpRequest.getServletPath();

        if (loginUser != null || servletPath.equals("/index.jsp") || servletPath.equals("/login") || servletPath.equals("/signup") || servletPath.startsWith("/css/")) {
        	chain.doFilter(request, response); // サーブレットを実行
        } else {
        	List<String> errorMessages = new ArrayList<>();
        	errorMessages.add("ログインをしてください");

        	session.setAttribute("errorMessages", errorMessages);

        	httpResponse.sendRedirect("login");
        	return;
        }
	}

	@Override
	public void init(FilterConfig config) {
		encoding = config.getInitParameter(INIT_PARAMETER_NAME_ENCODING);
		if (encoding == null) {
			System.out.println("EncodingFilter# デフォルトのエンコーディング(UTF-8)を利用します。");
			encoding = DEFAULT_ENCODING;
		} else {
			System.out.println("EncodingFilter# 設定されたエンコーディング(" + encoding
					+ ")を利用します。。");
		}
	}

	@Override
	public void destroy() {
	}

}
