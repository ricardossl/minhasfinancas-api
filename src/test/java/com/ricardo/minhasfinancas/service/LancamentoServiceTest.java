package com.ricardo.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Lancamento;
import com.ricardo.minhasfinancas.model.entity.Usuario;
import com.ricardo.minhasfinancas.model.enums.StatusLancamento;
import com.ricardo.minhasfinancas.model.repository.LancamentoRepository;
import com.ricardo.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.ricardo.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {
	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// Execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);

		// Verificação
		assertEquals(lancamento.getId(), lancamentoSalvo.getId());
		assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		// Execução e verificação
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// Cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// Execução
		service.atualizar(lancamentoSalvo);

		// Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// Execução e verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveDeletarUmLancamento() {
		// Cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);

		// Execução
		service.deletar(lancamentoSalvo);

		// Verificação
		Mockito.verify(repository).delete(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// Execução e verificação
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarLancamentos() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);

		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// Excecução
		List<Lancamento> resultado = service.buscar(lancamento);

		// Verificação
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// Execução
		service.atualizarStatus(lancamento, novoStatus);

		// Verificação
		assertEquals(lancamento.getStatus(), novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		// Cenário
		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// Excecução
		Optional<Lancamento> resultado = service.findById(id);

		// Verfificação
		assertTrue(resultado.isPresent());
	}

	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExiste() {
		// Cenário
		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Excecução
		Optional<Lancamento> resultado = service.findById(id);

		// Verfificação
		assertFalse(resultado.isPresent());
	}

	@Test
	public void deveLancaErrosAoValidarLancamento() {
		Lancamento lancamento = new Lancamento();

		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma descrição válida!");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma descrição válida!");

		lancamento.setDescricao("Salário");

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um mês válido");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um ano válido");
		
		lancamento.setAno(2022);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um usuário");
		
		lancamento.setUsuario(Usuario.builder().id(1l).nome("Ricardo").build());
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um valor válido");
		
		lancamento.setValor(BigDecimal.valueOf(100));
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um tipo de lançamento");
	}
}
