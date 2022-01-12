package com.ricardo.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.ricardo.minhasfinancas.model.entity.Lancamento;
import com.ricardo.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {
	Lancamento salvar(Lancamento lancamento);

	Lancamento atualizar(Lancamento lancamento);

	void deletar(Lancamento lancamento);

	List<Lancamento> buscar(Lancamento lancamento);

	void atualizarStatus(Lancamento lancamento, StatusLancamento status);

	void validar(Lancamento lancamento);

	Optional<Lancamento> findById(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
