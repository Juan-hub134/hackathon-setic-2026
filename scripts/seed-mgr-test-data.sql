SELECT pg_advisory_xact_lock(hashtext('hackathon-setic-2026-mgr-test-data-v1'));

WITH seed (
    test_key,
    responsible_user_id,
    category_id,
    cause,
    event,
    impact_text,
    objective,
    probability,
    impact,
    horizon_months
) AS (
    VALUES
        ('R01', 15, 4,
         'TESTE_MGR_R01: acessos privilegiados sem revisão regular',
         'permanência de acesso além da necessidade',
         'exposição ou alteração indevida de dados',
         'proteger sistemas e informações institucionais',
         4, 4, 12),
        ('R02', 15, 3,
         'TESTE_MGR_R02: inventário de componentes incompleto',
         'exploração de vulnerabilidade em aplicação',
         'comprometimento ou indisponibilidade de serviço',
         'manter serviços digitais disponíveis e confiáveis',
         4, 5, 12),
        ('R03', 19, 1,
         'TESTE_MGR_R03: mudanças sem checklist uniforme',
         'implantação incompatível com o ambiente',
         'retrabalho e interrupção temporária',
         'entregar software com qualidade e rastreabilidade',
         3, 3, 9),
        ('R04', 12, 6,
         'TESTE_MGR_R04: recuperação de infraestrutura não exercitada',
         'falha simultânea de componentes críticos',
         'indisponibilidade prolongada de serviços',
         'assegurar continuidade dos serviços de TI',
         3, 5, 12),
        ('R05', 12, 4,
         'TESTE_MGR_R05: restaurações de backup sem evidência',
         'recuperação incompleta após incidente',
         'perda de registros e demora na retomada',
         'preservar dados e recuperar serviços',
         2, 4, 12),
        ('R06', 11, 2,
         'TESTE_MGR_R06: continuidade depende de pessoa-chave',
         'ausência em atividade crítica',
         'atraso na resposta operacional',
         'manter continuidade dos processos institucionais',
         2, 3, 12),
        ('R07', 11, 5,
         'TESTE_MGR_R07: vencimentos contratuais acompanhados manualmente',
         'renovação ocorrer após o prazo necessário',
         'atraso pontual em serviço de apoio',
         'planejar aquisições e manter serviços no prazo',
         2, 2, 6),
        ('R08', 15, 1,
         'TESTE_MGR_R08: conferências de dados são manuais',
         'divergência ser propagada para relatório',
         'correção de informação e retrabalho',
         'produzir informações consistentes para decisões',
         3, 2, 6)
)
INSERT INTO public.risco (
    id_usuario,
    id_categoria,
    causa,
    evento,
    impacto_txt,
    objetivo,
    descricao,
    id_probabilidade,
    id_impacto,
    nivel,
    horizonte_meses,
    criado_por,
    criado_em
)
SELECT
    u.id_usuario,
    s.category_id,
    s.cause,
    s.event,
    s.impact_text,
    s.objective,
    format('Devido a %s, poderá ocorrer %s, levando a %s, afetando %s.',
           s.cause, s.event, s.impact_text, s.objective),
    s.probability,
    s.impact,
    s.probability * s.impact,
    s.horizon_months,
    u.nome,
    CURRENT_TIMESTAMP
FROM seed s
JOIN public.usuario u ON u.id_usuario = s.responsible_user_id AND u.ativo
JOIN public.cargo cargo ON cargo.id_cargo = u.id_cargo
WHERE cargo.cargo = 'RESPONSAVEL_PRR'
  AND EXISTS (SELECT 1 FROM public.categoria c WHERE c.id_categoria = s.category_id)
  AND EXISTS (SELECT 1 FROM public.escala_probabilidade p WHERE p.nota = s.probability)
  AND EXISTS (SELECT 1 FROM public.escala_impacto i WHERE i.nota = s.impact)
  AND NOT EXISTS (SELECT 1 FROM public.risco existing WHERE existing.causa = s.cause);

INSERT INTO public.historico (
    id_usuario, id_registro, tabela, operacao, alterado_em,
    campos_alterados, antes, depois
)
SELECT
    r.id_usuario,
    r.id_risco,
    'risco',
    'REGISTRAR_RISCO',
    r.criado_em,
    'todos',
    '{}'::jsonb,
    jsonb_build_object(
        'id_risco', r.id_risco,
        'id_categoria', r.id_categoria,
        'causa', r.causa,
        'evento', r.evento,
        'impacto_txt', r.impacto_txt,
        'objetivo', r.objetivo,
        'descricao', r.descricao,
        'id_probabilidade', r.id_probabilidade,
        'id_impacto', r.id_impacto,
        'horizonte_meses', r.horizonte_meses
    )
FROM public.risco r
WHERE r.causa LIKE 'TESTE_MGR_R%'
  AND NOT EXISTS (
      SELECT 1 FROM public.historico h
      WHERE h.tabela = 'risco'
        AND h.id_registro = r.id_risco
        AND h.operacao = 'REGISTRAR_RISCO'
  );

WITH context_seed (test_key, scope) AS (
    VALUES
        ('R01', 'TESTE MGR R01 - escopo: revisão de contas privilegiadas e trilhas de acesso em ambiente controlado.'),
        ('R02', 'TESTE MGR R02 - escopo: inventário de dependências e priorização de correções em aplicações de teste.'),
        ('R03', 'TESTE MGR R03 - escopo: checklist de mudança, validação prévia e retorno de versão de teste.'),
        ('R04', 'TESTE MGR R04 - escopo: continuidade e recuperação de componentes de infraestrutura não produtivos.'),
        ('R06', 'TESTE MGR R06 - escopo: documentação de continuidade e distribuição de conhecimento da equipe.'),
        ('R07', 'TESTE MGR R07 - escopo: acompanhamento de prazos de contratos fictícios e serviços de apoio.')
)
INSERT INTO public.contexto (id_risco, escopo, criado_em)
SELECT r.id_risco, s.scope, CURRENT_TIMESTAMP
FROM context_seed s
JOIN public.risco r ON r.causa = 'TESTE_MGR_' || s.test_key || ': ' ||
    CASE s.test_key
    WHEN 'R01' THEN 'acessos privilegiados sem revisão regular'
    WHEN 'R02' THEN 'inventário de componentes incompleto'
    WHEN 'R03' THEN 'mudanças sem checklist uniforme'
    WHEN 'R04' THEN 'recuperação de infraestrutura não exercitada'
    WHEN 'R06' THEN 'continuidade depende de pessoa-chave'
    WHEN 'R07' THEN 'vencimentos contratuais acompanhados manualmente'
    END
WHERE NOT EXISTS (
    SELECT 1 FROM public.contexto existing WHERE existing.id_risco = r.id_risco
);

INSERT INTO public.historico (
    id_usuario, id_registro, tabela, operacao, alterado_em,
    campos_alterados, antes, depois
)
SELECT
    manager.id_usuario,
    c.id_contexto,
    'contexto',
    'CRIAR_CONTEXTO',
    c.criado_em,
    'todos',
    '{}'::jsonb,
    jsonb_build_object('id_contexto', c.id_contexto, 'id_risco', r.id_risco, 'escopo', c.escopo)
FROM public.contexto c
JOIN public.risco r ON r.id_risco = c.id_risco
JOIN public.usuario owner ON owner.id_usuario = r.id_usuario
JOIN public.usuario manager ON manager.id_setor = owner.id_setor AND manager.ativo
JOIN public.cargo manager_role ON manager_role.id_cargo = manager.id_cargo
WHERE r.causa LIKE 'TESTE_MGR_R%'
  AND c.escopo LIKE 'TESTE MGR %'
  AND manager_role.cargo = 'GESTOR_RISCO'
  AND NOT EXISTS (
      SELECT 1 FROM public.historico h
      WHERE h.tabela = 'contexto'
        AND h.id_registro = c.id_contexto
        AND h.operacao = 'CRIAR_CONTEXTO'
  );

WITH stage_seed (test_key, stage_key, stage_name, days_until_due, completed) AS (
    VALUES
        ('R01', 'S01', 'Levantar contas privilegiadas e responsáveis de referência', 10, true),
        ('R01', 'S02', 'Validar necessidade dos acessos com os gestores do processo', 24, false),
        ('R01', 'S03', 'Registrar evidências e programar revisão periódica', 40, false),
        ('R02', 'S01', 'Consolidar inventário de componentes das aplicações de teste', 12, false),
        ('R02', 'S02', 'Classificar vulnerabilidades por criticidade e exposição', 26, false),
        ('R02', 'S03', 'Planejar correções e validar resultado em ambiente controlado', 45, false),
        ('R03', 'S01', 'Definir checklist mínimo para mudança de software', 8, true),
        ('R03', 'S02', 'Testar validação e retorno de versão em ambiente não produtivo', 20, true),
        ('R03', 'S03', 'Revisar evidências do piloto e atualizar procedimento', 35, false),
        ('R04', 'S01', 'Listar componentes críticos e dependências de recuperação', 14, false),
        ('R04', 'S02', 'Executar exercício de recuperação em ambiente de teste', 30, false),
        ('R04', 'S03', 'Documentar tempos observados e oportunidades de melhoria', 45, false),
        ('R06', 'S01', 'Mapear atividades críticas e conhecimento concentrado', 9, true),
        ('R06', 'S02', 'Documentar procedimentos e contatos de escalonamento', 22, false),
        ('R06', 'S03', 'Realizar simulação de substituição e revisar documentação', 38, false),
        ('R07', 'S01', 'Consolidar calendário de vencimentos do cenário de teste', 11, false),
        ('R07', 'S02', 'Configurar lembretes e responsáveis de acompanhamento', 25, false),
        ('R07', 'S03', 'Conferir fluxo de renovação e registrar evidências', 42, false)
)
INSERT INTO public.contexto_etapa (
    id_contexto, etapa, prazo, concluido_em, status
)
SELECT
    c.id_contexto,
    format('TESTE MGR %s %s - %s', s.test_key, s.stage_key, s.stage_name),
    CURRENT_DATE + s.days_until_due,
    CASE WHEN s.completed THEN CURRENT_DATE - 2 ELSE NULL END,
    s.completed
FROM stage_seed s
JOIN public.risco r ON r.causa LIKE 'TESTE_MGR_' || s.test_key || ':%'
JOIN public.contexto c ON c.id_risco = r.id_risco
WHERE NOT EXISTS (
    SELECT 1
    FROM public.contexto_etapa existing
    WHERE existing.id_contexto = c.id_contexto
      AND existing.etapa LIKE format('TESTE MGR %s %s -%%', s.test_key, s.stage_key)
);

WITH prr_seed (
    test_key, treatment_id, parameter_id, responsible_text,
    existing_controls, proposed_action, metrics, status
) AS (
    VALUES
        ('R01', 3, 2,
         'TESTE MGR - equipe responsável do setor 4 (designação fictícia)',
         'Cenário de teste: autenticação individual e registro de acesso existentes; revisão periódica ainda não padronizada.',
         'Mitigar: revisar privilégios em intervalo definido, validar justificativas com gestor e remover acessos sem necessidade confirmada.',
         'Percentual de contas privilegiadas revisadas no ciclo; meta de teste: 100%.',
         'PENDENTE'),
        ('R02', 3, 1,
         'TESTE MGR - equipe responsável do setor 4 (designação fictícia)',
         'Cenário de teste: atualizações são realizadas sob demanda; inventário e evidências centralizadas estão incompletos.',
         'Mitigar: manter inventário de dependências, priorizar correções críticas e validar atualizações em ambiente não produtivo.',
         'Percentual de componentes inventariados e tempo de correção de achados críticos; meta de teste: inventário completo.',
         'PENDENTE'),
        ('R03', 3, 3,
         'TESTE MGR - equipe responsável do setor 4 (designação fictícia)',
         'Cenário de teste: revisão por pares utilizada em parte das mudanças; checklist formal foi testado em piloto.',
         'Mitigar: adotar checklist de implantação, validação funcional e plano de retorno para mudanças relevantes.',
         'Percentual de mudanças com checklist e evidência de validação; meta de teste: 100% no escopo piloto.',
         'APROVADO'),
        ('R06', 3, 2,
         'TESTE MGR - equipe responsável do setor 7 (designação fictícia)',
         'Cenário de teste: procedimentos existem de forma dispersa e dependem de orientação verbal.',
         'Mitigar: documentar procedimentos críticos, definir substitutos e testar o fluxo de escalonamento.',
         'Percentual de procedimentos críticos documentados e testados; meta de teste: 100%.',
         'REPROVADO')
)
INSERT INTO public.plano_resposta (
    id_contexto, id_tratamento, id_parametro, nome_responsaveis,
    contr_existentes, acao_proposta, metricas, status
)
SELECT
    c.id_contexto,
    s.treatment_id,
    s.parameter_id,
    s.responsible_text,
    s.existing_controls,
    s.proposed_action,
    s.metrics,
    s.status
FROM prr_seed s
JOIN public.risco r ON r.causa LIKE 'TESTE_MGR_' || s.test_key || ':%'
JOIN public.contexto c ON c.id_risco = r.id_risco
WHERE EXISTS (SELECT 1 FROM public.tratamento_riscos t WHERE t.id_tratamento = s.treatment_id)
  AND EXISTS (SELECT 1 FROM public.parametros p WHERE p.id_parametro = s.parameter_id)
  AND NOT EXISTS (
      SELECT 1 FROM public.plano_resposta existing WHERE existing.id_contexto = c.id_contexto
  );

INSERT INTO public.historico (
    id_usuario, id_registro, tabela, operacao, alterado_em,
    campos_alterados, antes, depois
)
SELECT
    manager.id_usuario,
    p.id_resposta,
    'plano_resposta',
    'CRIAR_PRR',
    CURRENT_TIMESTAMP,
    'todos',
    '{}'::jsonb,
    jsonb_build_object(
        'id_resposta', p.id_resposta,
        'id_contexto', p.id_contexto,
        'id_tratamento', p.id_tratamento,
        'nome_responsaveis', p.nome_responsaveis,
        'acao_proposta', p.acao_proposta
    )
FROM public.plano_resposta p
JOIN public.contexto c ON c.id_contexto = p.id_contexto
JOIN public.risco r ON r.id_risco = c.id_risco
JOIN public.usuario owner ON owner.id_usuario = r.id_usuario
JOIN public.usuario manager ON manager.id_setor = owner.id_setor AND manager.ativo
JOIN public.cargo manager_role ON manager_role.id_cargo = manager.id_cargo
WHERE r.causa LIKE 'TESTE_MGR_R%'
  AND p.nome_responsaveis LIKE 'TESTE MGR %'
  AND manager_role.cargo = 'GESTOR_RISCO'
  AND NOT EXISTS (
      SELECT 1 FROM public.historico h
      WHERE h.tabela = 'plano_resposta'
        AND h.id_registro = p.id_resposta
        AND h.operacao = 'CRIAR_PRR'
  );

WITH opinion_seed (test_key, evaluator_id, result_status, opinion_text) AS (
    VALUES
        ('R03', 3, 'APROVADO',
         'TESTE_MGR_PARECER_R03: parecer fictício favorável ao piloto; manter evidências de validação e revisar a métrica no ciclo seguinte.'),
        ('R06', 4, 'REPROVADO',
         'TESTE_MGR_PARECER_R06: parecer fictício solicita esclarecer responsáveis substitutos, prazo de teste e evidências de continuidade.')
)
INSERT INTO public.parecer_prr (
    id_resposta, id_usuario_avaliador, status_resultado, parecer_texto, avaliado_em
)
SELECT
    p.id_resposta,
    s.evaluator_id,
    s.result_status,
    s.opinion_text,
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM opinion_seed s
JOIN public.risco r ON r.causa LIKE 'TESTE_MGR_' || s.test_key || ':%'
JOIN public.contexto c ON c.id_risco = r.id_risco
JOIN public.plano_resposta p ON p.id_contexto = c.id_contexto
JOIN public.usuario evaluator ON evaluator.id_usuario = s.evaluator_id AND evaluator.ativo
JOIN public.cargo evaluator_role ON evaluator_role.id_cargo = evaluator.id_cargo
WHERE evaluator_role.cargo = 'CSGR'
  AND p.status = s.result_status
  AND NOT EXISTS (
      SELECT 1
      FROM public.parecer_prr existing
      WHERE existing.id_resposta = p.id_resposta
        AND existing.parecer_texto = s.opinion_text
  );
