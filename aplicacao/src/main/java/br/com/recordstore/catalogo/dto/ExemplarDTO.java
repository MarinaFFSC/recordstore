package br.com.recordstore.catalogo.dto;
import br.com.recordstore.catalogo.StatusExemplar;
import br.com.recordstore.catalogo.CondicaoExemplar;
public record ExemplarDTO(Long id, Long midiaId, Integer numero, StatusExemplar status, CondicaoExemplar condicao) {}
