-- Script SQL para adicionar aprovacao de PRR pela CSGR
-- Execute este script no pgAdmin

BEGIN;

-- Criar ENUM para status do PRR
CREATE TYPE status_prr AS ENUM ('PENDENTE', 'APROVADO', 'REPROVADO');

-- Adicionar coluna status na tabela plano_resposta
ALTER TABLE public.plano_resposta
ADD COLUMN status status_prr NOT NULL DEFAULT 'PENDENTE';

-- Criar tabela de pareceres
CREATE TABLE IF NOT EXISTS public.parecer_prr
(
    id_parecer bigserial NOT NULL,
    id_resposta bigint NOT NULL,
    id_usuario_avaliador bigint NOT NULL,
    status_resultado status_prr NOT NULL,
    parecer_texto text NOT NULL,
    avaliado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT parecer_prr_pkey PRIMARY KEY (id_parecer),
    CONSTRAINT fk_parecer_plano_resposta FOREIGN KEY (id_resposta)
        REFERENCES public.plano_resposta (id_resposta) ON DELETE CASCADE,
    CONSTRAINT fk_parecer_usuario FOREIGN KEY (id_usuario_avaliador)
        REFERENCES public.usuario (id_usuario) ON DELETE RESTRICT
);

-- Criar indices para performance
CREATE INDEX idx_plano_resposta_status ON public.plano_resposta(status);
CREATE INDEX idx_parecer_prr_resposta ON public.parecer_prr(id_resposta);
CREATE INDEX idx_parecer_prr_avaliador ON public.parecer_prr(id_usuario_avaliador);

COMMIT;
