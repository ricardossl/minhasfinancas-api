package com.ricardo.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Lancamento;
import com.ricardo.minhasfinancas.model.enums.StatusLancamento;
import com.ricardo.minhasfinancas.model.enums.TipoLancamento;
import com.ricardo.minhasfinancas.model.repository.LancamentoRepository;
import com.ricardo.minhasfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public List<Lancamento> buscar(Lancamento lancamento) {
		Example example = Example.of(lancamento,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

		return lancamentoRepository.findAll(example);
	}

	@Override
	@Transactional
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma descrição válida!");
		}

		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um mês válido");
		}

		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um ano válido");
		}

		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um usuário");
		}

		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor válido");
		}

		if (lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de lançamento");
		}
	}

	@Override
	public Optional<Lancamento> findById(Long id) {
		return lancamentoRepository.findById(id);
	}

	@Override
	@Transactional
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = lancamentoRepository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id,
				TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
		BigDecimal despesas = lancamentoRepository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id,
				TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);

		if (receitas == null)
			receitas = BigDecimal.ZERO;

		if (despesas == null)
			despesas = BigDecimal.ZERO;

		return receitas.subtract(despesas);
	}

}
