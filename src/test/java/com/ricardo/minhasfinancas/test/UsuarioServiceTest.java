package com.ricardo.minhasfinancas.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ricardo.minhasfinancas.exception.ErroAutenticacao;
import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Usuario;
import com.ricardo.minhasfinancas.model.repository.UsuarioRepository;
import com.ricardo.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {
	@SpyBean
	private UsuarioService usuarioService;

	@MockBean
	private UsuarioRepository usuarioRepository;

	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
			Usuario usuario = Usuario.builder().nome("nome").email("email@email.com").senha("senha").id(1l).build();
			Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

			// Ação
			Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());

			// Verificação
			assertNotNull(usuarioSalvo);
			assertEquals(usuarioSalvo.getId(), 1l);
			assertEquals(usuarioSalvo.getNome(), "nome");
			assertEquals(usuarioSalvo.getEmail(), "email@email.com");
			assertEquals(usuarioSalvo.getSenha(), "senha");

		});

	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// Cenario
			String email = "email@email.com";

			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
			// Acao
			usuarioService.salvarUsuario(usuario);

			// Verificacao
			Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
		});
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// Cenário
			String email = "email@email.com";
			String senha = "senha";

			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();

			Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

			// Ação
			usuarioService.autenticar(email, senha);
		});
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		Exception exception = Assertions.assertThrows(ErroAutenticacao.class, () -> {
			// cenario
			Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

			// acao
			usuarioService.autenticar("email@email.com", "senha");
		});

		assertTrue(exception.getMessage().equals("Usuário não encontrado para o email informado"));
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		Exception exception = Assertions.assertThrows(ErroAutenticacao.class, () -> {
			// cenario
			String senha = "senha";
			Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
			Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

			// acao
			usuarioService.autenticar("email@email.com", "123");
		});

		assertTrue(exception.getMessage().equals("Senha inválida"));
	}

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			// cenario
			Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

			// acao
			usuarioService.validarEmail("ricardo@mail.com");
		});
	}

	@Test
	public void deveLancarErrorAoValidarEmailQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// cenario
			Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

			// acao
			usuarioService.validarEmail("email@email.com");
		});

	}

}
