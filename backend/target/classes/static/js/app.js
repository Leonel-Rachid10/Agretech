// === AgriTech Dondo — Lógica de Interface ===
const App = {
    culturas: [],
    associacoes: [],

    async init() {
        await AgriDB.open();
        SyncManager.init();
        await this.carregarDadosBase();
    },

    async carregarDadosBase() {
        try {
            this.culturas = await Api.getCulturas();
            if (this.culturas) await AgriDB.cacheList('culturas', this.culturas);
        } catch (e) {
            console.warn('Falha ao carregar culturas online, a usar cache offline:', e);
            this.culturas = await AgriDB.getCachedList('culturas');
        }
        try {
            this.associacoes = await Api.getAssociacoes();
            if (this.associacoes) await AgriDB.cacheList('associacoes', this.associacoes);
        } catch (e) {
            console.warn('Falha ao carregar associações online, a usar cache offline:', e);
            this.associacoes = await AgriDB.getCachedList('associacoes');
        }
    },

    refreshCurrentView() {
        if (document.getElementById('catalogo-lista')) this.carregarCatalogo();
        if (document.getElementById('lotes-lista')) this.carregarLotesDashboard();
    },

    // === CATÁLOGO (index.html) ===
    async carregarCatalogo() {
        const lista = document.getElementById('catalogo-lista');
        const kpiTotal = document.getElementById('kpi-total');
        const kpiVolume = document.getElementById('kpi-volume');
        const kpiProntos = document.getElementById('kpi-prontos');
        if (!lista) return;

        const filtros = {
            culturaId: document.getElementById('filtro-cultura')?.value || null,
            localidade: document.getElementById('filtro-localidade')?.value || null,
            estado: document.getElementById('filtro-estado')?.value || null,
            quantidadeMinima: document.getElementById('filtro-quantidade')?.value || null
        };

        let lotes;
        try {
            lotes = await Api.getCatalogo(filtros);
            if (lotes) await AgriDB.cacheList('lotes_cache', lotes);
        } catch (e) {
            console.warn('Falha ao carregar catálogo online, a usar cache offline:', e);
            lotes = await AgriDB.getCachedList('lotes_cache');
        }

        if (!lotes || lotes.length === 0) {
            lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Nenhum lote encontrado no catálogo.<br>Ajuste os filtros ou aguarde novas publicações.</p></div>';
            if (kpiTotal) kpiTotal.textContent = '0';
            if (kpiVolume) kpiVolume.textContent = '0 KG';
            if (kpiProntos) kpiProntos.textContent = '0';
            return;
        }

        // KPIs
        if (kpiTotal) kpiTotal.textContent = lotes.length;
        if (kpiVolume) {
            const vol = lotes.reduce((s, l) => s + Number.parseFloat(l.quantidadeEstimada || 0), 0);
            kpiVolume.textContent = vol.toLocaleString('pt-MZ') + ' KG';
        }
        if (kpiProntos) kpiProntos.textContent = lotes.filter(l => l.estado === 'PRONTO_PARA_COLHEITA').length;

        lista.innerHTML = lotes.map(l => this.renderLoteCard(l)).join('');
    },

    renderLoteCard(l) {
        const badgeClass = { EM_CRESCIMENTO: 'badge-crescimento', PRONTO_PARA_COLHEITA: 'badge-pronto', RESERVADO: 'badge-reservado', VENDIDO: 'badge-vendido' };
        const badgeLabel = { EM_CRESCIMENTO: 'Em Crescimento', PRONTO_PARA_COLHEITA: 'Pronto p/ Colheita', RESERVADO: 'Reservado', VENDIDO: 'Vendido' };
        
        // SonarLint S7781: replaceAll em vez de replace com regex
        const tel = (l.produtorTelemovel || '').replaceAll('+', '');
        const msgWa = encodeURIComponent('Olá, vi o lote de ' + l.culturaNome + ' (' + l.quantidadeEstimada + ' ' + (l.unidadeMedida||'KG') + ') na plataforma AgriTech Dondo e tenho interesse. Podemos negociar?');

        // SonarLint S7773: Number.parseFloat
        return `<div class="card">
            <div class="card-header">
                <h3> ${l.culturaNome}</h3>
                <span class="badge ${badgeClass[l.estado] || ''}">${badgeLabel[l.estado] || l.estado}</span>
            </div>
            <div class="card-body">
                <p class="destaque">${Number.parseFloat(l.quantidadeEstimada).toLocaleString('pt-MZ')} ${l.unidadeMedida || 'KG'}</p>
                ${l.precoPorUnidade ? '<p> Preço: <strong>' + Number.parseFloat(l.precoPorUnidade).toLocaleString('pt-MZ') + ' MT/' + (l.unidadeMedida||'KG') + '</strong></p>' : ''}
                <p> Produtor: <strong>${l.produtorNome || 'N/D'}</strong></p>
                <p> ${l.associacaoNome || ''} — ${l.associacaoLocalidade || ''}</p>
                <p> Colheita prevista: <strong>${l.dataColheitaPrevista || 'N/D'}</strong></p>
                ${l.observacoes ? '<p> ' + l.observacoes + '</p>' : ''}
            </div>
            <div class="botoes-contacto">
                ${tel ? '<a href="https://wa.me/' + tel + '?text=' + msgWa + '" target="_blank" class="btn btn-whatsapp"> WhatsApp</a>' : ''}
                ${l.produtorTelemovel ? '<a href="tel:' + l.produtorTelemovel + '" class="btn btn-chamada"> Ligar</a>' : ''}
            </div>
        </div>`;
    },

    preencherFiltros() {
        const selCultura = document.getElementById('filtro-cultura');
        if (selCultura && this.culturas) {
            selCultura.innerHTML = '<option value="">Todas as Culturas</option>' +
                this.culturas.map(c => '<option value="' + c.id + '">' + c.nome + '</option>').join('');
        }
    },

    // === DASHBOARD (dashboard.html) ===
    async carregarLotesDashboard() {
        const lista = document.getElementById('lotes-lista');
        if (!lista) return;

        let lotes;
        try {
            lotes = await Api.getLotes();
        } catch (e) {
            console.warn('Falha ao carregar lotes do dashboard:', e);
            lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Sem conexão. Os lotes offline serão mostrados após sincronização.</p></div>';
            return;
        }

        if (!lotes || lotes.length === 0) {
            lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Nenhum lote registado. Clique em "Novo Lote" para começar.</p></div>';
            return;
        }

        lista.innerHTML = '<div class="tabela-wrapper"><table><thead><tr><th>Cultura</th><th>Produtor</th><th>Quantidade</th><th>Colheita</th><th>Estado</th><th>Acções</th></tr></thead><tbody>' +
            lotes.map(l => {
                const badgeClass = { EM_CRESCIMENTO: 'badge-crescimento', PRONTO_PARA_COLHEITA: 'badge-pronto', RESERVADO: 'badge-reservado', VENDIDO: 'badge-vendido' };
                const badgeLabel = { EM_CRESCIMENTO: 'Em Crescimento', PRONTO_PARA_COLHEITA: 'Pronto', RESERVADO: 'Reservado', VENDIDO: 'Vendido' };
                return `<tr>
                    <td>${l.culturaNome}</td>
                    <td>${l.produtorNome}<br><small>${l.associacaoLocalidade || ''}</small></td>
                    <td>${Number.parseFloat(l.quantidadeEstimada).toLocaleString('pt-MZ')} ${l.unidadeMedida||'KG'}</td>
                    <td>${l.dataColheitaPrevista || '-'}</td>
                    <td><span class="badge ${badgeClass[l.estado]||''}">${badgeLabel[l.estado]||l.estado}</span></td>
                    <td>
                        <select class="estado-select" onchange="App.mudarEstadoLote(${l.id}, this.value)">
                            <option value="">Alterar...</option>
                            <option value="EM_CRESCIMENTO">Em Crescimento</option>
                            <option value="PRONTO_PARA_COLHEITA">Pronto</option>
                            <option value="RESERVADO">Reservado</option>
                            <option value="VENDIDO">Vendido</option>
                        </select>
                    </td>
                </tr>`;
            }).join('') +
            '</tbody></table></div>';
    },

    async mudarEstadoLote(id, estado) {
        if (!estado) return;
        try {
            await Api.atualizarEstadoLote(id, estado);
            this.carregarLotesDashboard();
        } catch (e) {
            alert('Erro ao atualizar: ' + e.message);
        }
    },

    async preencherFormLote() {
        const selCultura = document.getElementById('lote-cultura');
        const selProdutor = document.getElementById('lote-produtor');
        if (selCultura && this.culturas) {
            selCultura.innerHTML = '<option value="">Selecione a cultura</option>' +
                this.culturas.map(c => '<option value="' + c.id + '">' + c.nome + '</option>').join('');
        }
        if (selProdutor) {
            let produtores;
            try { 
                produtores = await Api.getProdutores(); 
            } catch (e) { 
                console.warn('Falha ao obter produtores:', e);
                produtores = []; 
            }
            selProdutor.innerHTML = '<option value="">Selecione o produtor</option>' +
                produtores.map(p => '<option value="' + p.id + '">' + p.nome + ' — ' + (p.associacaoLocalidade || '') + '</option>').join('');
        }
    },

    async submeterNovoLote(form) {
        // SonarLint S7773: Number.parseInt com base 10 e Number.parseFloat
        const data = {
            produtorId: Number.parseInt(form.produtorId.value, 10),
            culturaId: Number.parseInt(form.culturaId.value, 10),
            quantidadeEstimada: Number.parseFloat(form.quantidadeEstimada.value),
            dataColheitaPrevista: form.dataColheitaPrevista.value,
            dataSementeira: form.dataSementeira.value || null,
            precoPorUnidade: form.precoPorUnidade.value ? Number.parseFloat(form.precoPorUnidade.value) : null,
            observacoes: form.observacoes.value || null,
            estado: 'EM_CRESCIMENTO'
        };

        if (!navigator.onLine) {
            await AgriDB.addToSyncQueue('novo_lote', data);
            alert(' Lote guardado offline! Será sincronizado quando houver rede.');
            SyncManager.updateBar();
            fecharModal('modal-novo-lote');
            return;
        }

        try {
            await Api.criarLote(data);
            alert(' Lote registado com sucesso!');
            fecharModal('modal-novo-lote');
            this.carregarLotesDashboard();
        } catch (e) {
            alert('Erro: ' + e.message);
        }
    },

    async carregarAssociacoes() {
        const lista = document.getElementById('associacoes-lista');
        if (!lista) return;

        let assocs;
        try { 
            assocs = await Api.getAssociacoes(); 
        } catch(e) { 
            console.warn('Falha ao carregar associações online, a usar cache:', e);
            assocs = this.associacoes || []; 
        }

        if (!assocs || assocs.length === 0) {
            lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Nenhuma associação registada.</p></div>';
            return;
        }

        lista.innerHTML = assocs.map(a => `<div class="card">
            <div class="card-header"><h3> ${a.nome}</h3></div>
            <div class="card-body">
                <p> Localidade: <strong>${a.localidade}</strong>${a.povoado ? ' — ' + a.povoado : ''}</p>
                ${a.pontoFocalNome ? '<p> Ponto Focal: <strong>' + a.pontoFocalNome + '</strong> (' + (a.pontoFocalTelemovel||'') + ')</p>' : ''}
                ${a.contactoPrincipal ? '<p> Contacto: ' + a.contactoPrincipal + '</p>' : ''}
            </div>
        </div>`).join('');
    },

    async carregarProdutoresDashboard() {
        const lista = document.getElementById('produtores-lista');
        if (!lista) return;

        let produtores;
        try { 
            produtores = await Api.getProdutores(); 
        } catch(e) { 
            console.warn('Falha ao carregar produtores online:', e);
            produtores = []; 
        }

        if (!produtores || produtores.length === 0) {
            lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Nenhum produtor registado.</p></div>';
            return;
        }

        lista.innerHTML = produtores.map(p => `<div class="card">
            <div class="card-header"><h3> ${p.nome}</h3></div>
            <div class="card-body">
                <p> ${p.telemovel}</p>
                <p> ${p.associacaoNome || ''} — ${p.associacaoLocalidade || ''}</p>
                ${p.localizacaoDetalhada ? '<p> ' + p.localizacaoDetalhada + '</p>' : ''}
            </div>
        </div>`).join('');
    }
};

// === Utilitários de Modal ===
function abrirModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.add('active');
}
function fecharModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.remove('active');
}