package cz.qjetta.weatherstation.locale;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LocaleInterceptor
		implements HandlerInterceptor {

	@Resource(name = "localeHolder")
	LocaleHolder localeHolder;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws ServletException {
		LocaleResolver localeResolver = RequestContextUtils
				.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException(
					"No LocaleResolver found: not in a DispatcherServlet request?");
		}

		if (localeResolver instanceof AcceptHeaderLocaleResolver headerLocaleResolver) {
			localeHolder
					.setCurrentLocale(headerLocaleResolver
							.resolveLocale(request));
		} else {
			throw new IllegalStateException(
					"Resolver should be of AcceptHeaderLocaleResolver type");
		}

		return true;
	}
}