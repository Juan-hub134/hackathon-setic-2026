(function () {
    const probabilityLabels = {
        1: 'Raro',
        2: 'Pouco provável',
        3: 'Provável',
        4: 'Muito provável',
        5: 'Praticamente certo'
    };
    const impactLabels = {
        1: 'Muito baixo',
        2: 'Baixo',
        3: 'Médio',
        4: 'Alto',
        5: 'Muito alto'
    };

    function renderRiskMatrix(container, risks, selectedRiskId) {
        container.replaceChildren();
        let invalidRisks = 0;
        const validRisks = (Array.isArray(risks) ? risks : []).filter(risk => {
            const probability = Number(risk.probabilidade?.nota);
            const impact = Number(risk.impacto?.nota);
            const valid = Number.isInteger(probability) && probability >= 1 && probability <= 5
                && Number.isInteger(impact) && impact >= 1 && impact <= 5;
            if (!valid) invalidRisks += 1;
            return valid;
        });

        const selected = validRisks.find(risk => String(risk.idRisco) === String(selectedRiskId));
        if (selected) {
            const probability = Number(selected.probabilidade.nota);
            const impact = Number(selected.impacto.nota);
            const summary = document.createElement('p');
            summary.className = 'matrix-selection';
            summary.setAttribute('aria-live', 'polite');
            summary.textContent = `Risco #${selected.idRisco}: probabilidade ${probability} × impacto ${impact} = nível ${probability * impact}.`;
            container.appendChild(summary);
        } else {
            const summary = document.createElement('p');
            summary.className = 'matrix-selection';
            summary.setAttribute('aria-live', 'polite');
            summary.textContent = 'Selecione um risco para destacar sua posição. A matriz também mostra os demais riscos listados para o seu perfil.';
            container.appendChild(summary);
        }

        const scroll = document.createElement('div');
        scroll.className = 'matrix-scroll';
        const table = document.createElement('table');
        table.className = 'risk-matrix';
        table.setAttribute('aria-label', 'Matriz de risco 5 por 5: impacto nas linhas e probabilidade nas colunas; cada célula apresenta probabilidade vezes impacto.');
        const head = document.createElement('thead');
        const header = document.createElement('tr');
        const corner = document.createElement('th');
        corner.scope = 'col';
        corner.className = 'matrix-axis';
        corner.textContent = 'Impacto ↓ · Probabilidade →';
        header.appendChild(corner);

        for (let probability = 1; probability <= 5; probability += 1) {
            const th = document.createElement('th');
            th.scope = 'col';
            th.className = 'matrix-column-heading';
            const note = document.createElement('span');
            note.className = 'matrix-scale-note';
            note.textContent = String(probability);
            const label = document.createElement('span');
            label.className = 'matrix-scale-label';
            label.textContent = probabilityLabels[probability];
            th.append(note, label);
            header.appendChild(th);
        }
        head.appendChild(header);
        table.appendChild(head);

        const body = document.createElement('tbody');
        for (let impact = 5; impact >= 1; impact -= 1) {
            const row = document.createElement('tr');
            const rowHeading = document.createElement('th');
            rowHeading.scope = 'row';
            rowHeading.className = 'matrix-row-heading';
            const note = document.createElement('span');
            note.className = 'matrix-scale-note';
            note.textContent = String(impact);
            const label = document.createElement('span');
            label.className = 'matrix-scale-label';
            label.textContent = impactLabels[impact];
            rowHeading.append(note, label);
            row.appendChild(rowHeading);

            for (let probability = 1; probability <= 5; probability += 1) {
                const cellRisks = validRisks.filter(risk =>
                    Number(risk.probabilidade.nota) === probability
                    && Number(risk.impacto.nota) === impact
                );
                const score = probability * impact;
                const cell = document.createElement('td');
                cell.className = 'matrix-cell';
                cell.style.setProperty('--risk-intensity', String((score - 1) / 24));
                const selectedRisks = cellRisks.filter(risk => String(risk.idRisco) === String(selectedRiskId));
                if (selectedRisks.length) cell.classList.add('is-selected');
                cell.setAttribute(
                    'aria-label',
                    `Impacto ${impact} (${impactLabels[impact]}), probabilidade ${probability} (${probabilityLabels[probability]}), nível ${score}${cellRisks.length ? `, riscos ${cellRisks.map(risk => `número ${risk.idRisco}`).join(', ')}` : ', nenhum risco listado'}`
                );
                const scoreLabel = document.createElement('span');
                scoreLabel.className = 'matrix-score';
                scoreLabel.textContent = String(score);
                cell.appendChild(scoreLabel);

                if (cellRisks.length) {
                    const markers = document.createElement('span');
                    markers.className = 'matrix-risk-list';
                    markers.setAttribute('aria-hidden', 'true');
                    markers.textContent = cellRisks.map(risk => `#${risk.idRisco}`).join(', ');
                    cell.appendChild(markers);
                    cell.title = cellRisks.map(risk => `Risco #${risk.idRisco}: ${risk.descricao}`).join('\n');
                }
                row.appendChild(cell);
            }
            body.appendChild(row);
        }
        table.appendChild(body);
        scroll.appendChild(table);
        container.appendChild(scroll);

        const legend = document.createElement('p');
        legend.className = 'matrix-legend';
        legend.textContent = `Nível = probabilidade × impacto (1 a 25); as cores vão do menor ao maior nível. ${validRisks.length} risco(s) representado(s) na matriz.`;
        container.appendChild(legend);

        if (invalidRisks) {
            const warning = document.createElement('p');
            warning.className = 'matrix-warning';
            warning.setAttribute('role', 'status');
            warning.textContent = `${invalidRisks} risco(s) não foram posicionados: a API retornou nota de probabilidade ou impacto fora da escala de 1 a 5.`;
            container.appendChild(warning);
        }
    }

    window.renderRiskMatrix = renderRiskMatrix;
})();
