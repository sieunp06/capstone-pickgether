package com.capstone.pick.controller;

import com.capstone.pick.config.TestSecurityConfig;
import com.capstone.pick.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@DisplayName("로그인 컨트롤러")
@Import(TestSecurityConfig.class)
@WebMvcTest(LoginController.class)
@ComponentScan(basePackages = "com.capstone.pick.config")
@MockBeans({
        @MockBean(JpaMetamodelMappingContext.class),
        @MockBean(UserService.class)
})
class LoginControllerTest {

    private final MockMvc mvc;

    public LoginControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[GET][/] 메인 페이지")
    @Test
    public void 로그인_뷰_엔드포인트_테스트() throws Exception {

        // When & Then
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("page/login"));
    }

    @DisplayName("[GET][/oauth2/authorization/] 소셜로그인 시도")
    @Test
    public void 소셜로그인_테스트() throws Exception {

        // When & Then
        mvc.perform(get("/oauth2/authorization/kakao"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://kauth.kakao.com/oauth/**")) // 카카오 로그인 페이지로 이동시키는지?
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(get("/oauth2/authorization/naver"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://nid.naver.com/oauth2.0/**")) // 네이버 로그인 페이지로 이동시키는지?
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://accounts.google.com/o/oauth2/v2/**")) // 구글 로그인 페이지로 이동시키는지?
                .andDo(MockMvcResultHandlers.print());
    }

//    @DisplayName("[GET][/addMoreInfo] 추가정보 입력페이지 요청")
//    @Test
//    public void 추가정보_입력_테스트() throws Exception {
//
//        // When & Then
//        mvc.perform(get("/addMoreInfo").with(oauth2Login()
//
//                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
//
//                .attributes(attributes -> {
//                    attributes.put("username", "testUserName");
//                    attributes.put("name", "test");
//                    attributes.put("email", "test@email");
//                })
//        ))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
//                .andExpect(view().name("page/addMoreInfo"))
//                .andExpect(model().attributeExists("addMoreInfo"));
//    }

}