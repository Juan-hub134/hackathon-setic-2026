CREATE TABLE IF NOT EXISTS solicitacao_csgr (
    id_solicitacao BIGSERIAL PRIMARY KEY,
    id_resposta BIGINT NOT NULL REFERENCES plano_resposta (id_resposta),
    id_usuario_solicitante BIGINT NOT NULL REFERENCES usuario (id_usuario),
    id_usuario_avaliador BIGINT REFERENCES usuario (id_usuario),
    motivo TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    solicitado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atendido_em TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_solicitacao_csgr_prr_pendente
    ON solicitacao_csgr (id_resposta)
    WHERE status = 'PENDENTE';
