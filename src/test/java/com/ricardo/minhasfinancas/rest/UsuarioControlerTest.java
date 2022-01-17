package com.ricardo.minhasfinancas.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricardo.minhasfinancas.exception.ErroAutenticacao;
import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Usuario;
import com.ricardo.minhasfinancas.service.LancamentoService;
import com.ricardo.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioControler.class)
@AutoConfigureMockMvc
public class UsuarioControlerTest {
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService service;

	@MockBean
	LancamentoService lancamentoService;

	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		// Cenário
		String email = "usuario@mail.com";
		String senha = "123";
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();

		Usuario usuarioAutenticado = Usuario.builder().email(email).senha(senha).id(1l).build();

		Mockito.when(service.autenticar(email, senha)).thenReturn(usuarioAutenticado);

		String json = new ObjectMapper().writeValueAsString(usuario);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void deveRetornarBadRequestAoAutenticarUmUsuario() throws Exception {
		// Cenário
		String email = "usuadario@mail.com";
		String senha = "123";
		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

		String json = new ObjectMapper().writeValueAsString(usuario);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void deveSalvarUmUsuario() throws Exception {
		// Cenário
		String email = "usuario@mail.com";
		String senha = "123";
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();

		Usuario usuarioSalvo = Usuario.builder().email(email).senha(senha).id(1l).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuarioSalvo);

		String json = new ObjectMapper().writeValueAsString(usuario);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON)
				.content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioSalvo.getId()));
	}
	
	@Test
	public void deveRetornarBadRequestAoSalvarUmUsuarioInvalido() throws Exception {
		// Cenário
		String email = "usuario@mail.com";
		String senha = "123";
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();


		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

		String json = new ObjectMapper().writeValueAsString(usuario);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON)
				.content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
