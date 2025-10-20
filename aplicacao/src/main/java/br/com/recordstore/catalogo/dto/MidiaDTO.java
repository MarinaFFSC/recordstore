package br.com.recordstore.catalogo.dto;
import br.com.recordstore.catalogo.TipoMidia;
public record MidiaDTO(Long id, String titulo, String artista, String genero, int ano, TipoMidia tipo) {}
