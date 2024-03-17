package cz.qjetta.weatherstation.config.localization;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cz.qjetta.weatherstation.locale.LocaleInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LocaleInterceptor localeInterceptor;

	@Override
	public void addInterceptors(
			InterceptorRegistry interceptorRegistry) {
		interceptorRegistry
				.addInterceptor(localeInterceptor);
	}
}