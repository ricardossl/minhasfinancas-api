package com.ricardo.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ricardo.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
}
