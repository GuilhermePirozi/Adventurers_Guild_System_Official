package br.infnet.tp1_guilda.dto.elastic;

public record ResponseContagemPorCampo(
        String campo,
        String valor,
        long quantidade
) {
}
