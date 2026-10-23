// === AgreTech: Lógica de Interface ===
const App = {
    culturas: [],
    catalogoLotes: [],
    catalogoPagina: 1,
    itensPorPaginaCatalogo: 9,

    async init() {
        await this.carregarDadosBase();
    },

    async carregarDadosBase() {
        try {
            this.culturas = await Api.getCulturas();
        } catch (e) {
            console.warn('Falha ao carregar produtos agrícolas:', e);
            this.culturas = [];
        }
    },

    refreshCurrentView() {
        if (document.getElementById('catalogo-lista')) this.carregarCatalogo();
        if (document.getElementById('lotes-lista')) {
            if (typeof window.carregarLotes === 'function') window.carregarLotes();
            else this.carregarLotesDashboard();
        }
    },

    // === CATÁLOGO (index.html) ===
    _cacheCatalogoBruto: null,
    _ultimoParamFiltros: null,

    async carregarCatalogo(pagina = 1, apenasFiltroLocal = false) {
        const lista = document.getElementById('catalogo-lista');
        const kpiTotal = document.getElementById('kpi-total');
        const kpiQuantidade = document.getElementById('kpi-quantidade');
        const kpiProntos = document.getElementById('kpi-prontos');
        const containerPaginacao = document.getElementById('catalogo-paginacao');
        if (!lista) return;

        const filtroCultura = document.getElementById('filtro-cultura')?.value;
        const filtroProvincia = document.getElementById('filtro-provincia')?.value;
        const filtroLocalidade = document.getElementById('filtro-localidade')?.value;
        const filtroBairro = document.getElementById('filtro-bairro')?.value;
        const filtroEstado = document.getElementById('filtro-estado')?.value;
        const filtroQuantidade = document.getElementById('filtro-quantidade')?.value;
        const termoBusca = (document.getElementById('pesquisa-catalogo')?.value || '').trim().toLowerCase();

        const params = {};
        if (filtroCultura) params.culturaId = filtroCultura;
        if (filtroProvincia) params.provincia = filtroProvincia;
        if (filtroLocalidade) params.localidade = filtroLocalidade;
        if (filtroEstado) params.estado = filtroEstado;
        if (filtroQuantidade) params.quantidadeMinima = filtroQuantidade;

        const paramsStr = JSON.stringify(params);

        let lotes;
        if (apenasFiltroLocal && this._cacheCatalogoBruto && this._ultimoParamFiltros === paramsStr) {
            lotes = this._cacheCatalogoBruto;
        } else {
            try {
                lotes = await Api.getCatalogo(params);
            } catch (e) {
                console.warn('Falha ao carregar catálogo online:', e);
                lotes = [];
            }
            this._cacheCatalogoBruto = lotes || [];
            this._ultimoParamFiltros = paramsStr;
        }

        let listaFiltrada = Array.isArray(lotes) ? lotes : [];

        // Filtro adicional por bairro/zona
        if (filtroBairro) {
            const bairroBusca = filtroBairro.toLowerCase();
            listaFiltrada = listaFiltrada.filter(l => (l.bairro || '').toLowerCase().includes(bairroBusca));
        }

        // Filtro por texto livre da pesquisa (produto, agricultor, província, localidade/distrito, bairro)
        if (termoBusca) {
            listaFiltrada = listaFiltrada.filter(l => {
                const cultura = (l.culturaNome || '').toLowerCase();
                const produtor = (l.produtorNome || '').toLowerCase();
                const prov = (l.provincia || '').toLowerCase();
                const dist = (l.distrito || l.localidade || '').toLowerCase();
                const bairro = (l.bairro || '').toLowerCase();
                return cultura.includes(termoBusca) ||
                       produtor.includes(termoBusca) ||
                       prov.includes(termoBusca) ||
                       dist.includes(termoBusca) ||
                       bairro.includes(termoBusca);
            });
        }

        this.catalogoLotes = listaFiltrada;

        if (!this.catalogoLotes || this.catalogoLotes.length === 0) {
            const temFiltroAtivo = !!(filtroCultura || filtroProvincia || filtroLocalidade || filtroBairro || filtroEstado || filtroQuantidade || termoBusca);
            const mensagem = temFiltroAtivo
                ? 'Nenhum produto encontrado com os filtros selecionados.<br>Tente ajustar ou limpar os filtros para ver mais produtos.'
                : 'Nenhum produto disponível no catálogo de momento.<br>Aguarde novas publicações de agricultores.';
            lista.innerHTML = `<div class="empty-state"><div class="icone"></div><p>${mensagem}</p></div>`;
            if (kpiTotal) kpiTotal.textContent = '0';
            if (kpiQuantidade) kpiQuantidade.textContent = '0';
            if (kpiProntos) kpiProntos.textContent = '0';
            if (containerPaginacao) containerPaginacao.innerHTML = '';
            const res = document.getElementById('catalogo-resultados');
            if (res) res.textContent = temFiltroAtivo ? 'Nenhum resultado encontrado.' : '';
            return;
        }

        // KPIs calculados com base em todos os produtos filtrados do catálogo
        if (kpiTotal) kpiTotal.textContent = this.catalogoLotes.length;
        if (kpiQuantidade) {
            const quantidadesPorUnidade = this.catalogoLotes.reduce((totais, produto) => {
                const unidade = produto.unidadeMedida || 'un.';
                totais.set(unidade, (totais.get(unidade) || 0) + Number.parseFloat(produto.quantidadeEstimada || 0));
                return totais;
            }, new Map());
            const quantidadeTotal = [...quantidadesPorUnidade.entries()]
                .map(([unidade, total]) => `${total.toLocaleString('pt-MZ')} ${unidade}`)
                .join(' · ');
            kpiQuantidade.textContent = quantidadeTotal || '0';
            kpiQuantidade.title = quantidadeTotal;
        }
        if (kpiProntos) {
            kpiProntos.textContent = this.catalogoLotes.filter(l => l.estado === 'PRONTO_PARA_COLHEITA').length;
        }

        this.renderizarPaginaCatalogo(pagina);
    },

    preencherFiltros() {
        const select = document.getElementById('filtro-cultura');
        if (!select || !this.culturas || this.culturas.length === 0) return;
        const valorAtual = select.value;
        select.innerHTML = '<option value="">Todos</option>' +
            this.culturas.map(c => `<option value="${c.id}">${this.escapeHtml(c.nome)}</option>`).join('');
        if (valorAtual) select.value = valorAtual;
    },

    filtrarCatalogoTexto(termo) {
        clearTimeout(this._timerPesquisa);
        this._timerPesquisa = setTimeout(() => {
            this.carregarCatalogo(1, true);
        }, 150);
    },

    renderizarPaginaCatalogo(pagina) {
        const lista = document.getElementById('catalogo-lista');
        const containerPaginacao = document.getElementById('catalogo-paginacao');
        const res = document.getElementById('catalogo-resultados');
        if (!lista) return;

        const totalItens = this.catalogoLotes.length;
        const totalPaginas = Math.max(1, Math.ceil(totalItens / this.itensPorPaginaCatalogo));
        this.catalogoPagina = Math.max(1, Math.min(pagina, totalPaginas));

        const inicio = (this.catalogoPagina - 1) * this.itensPorPaginaCatalogo;
        const fim = Math.min(inicio + this.itensPorPaginaCatalogo, totalItens);
        const lotesPagina = this.catalogoLotes.slice(inicio, fim);

        if (res) {
            res.textContent = `A apresentar ${inicio + 1} a ${fim} de ${totalItens} produto(s)`;
        }

        lista.innerHTML = lotesPagina.map(l => this.renderLoteCard(l)).join('');

        if (containerPaginacao) {
            containerPaginacao.innerHTML = this.gerarHtmlPaginacao(totalPaginas, this.catalogoPagina, totalItens, 'App.irParaPaginaCatalogo');
        }
    },

    irParaPaginaCatalogo(pagina) {
        App.renderizarPaginaCatalogo(pagina);
        const topo = document.getElementById('catalogo-lista');
        if (topo) {
            topo.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    },

    gerarHtmlPaginacao(totalPaginas, paginaAtual, totalItens, nomeFuncao) {
        if (totalPaginas <= 1) {
            return `<div class="paginacao-info">${totalItens} produto(s) no total</div>`;
        }

        let botoesHtml = '';

        // Botão Anterior
        const desabilitarAnterior = paginaAtual === 1;
        botoesHtml += `<button type="button" class="btn-paginacao" ${desabilitarAnterior ? 'disabled' : ''} onclick="${nomeFuncao}(${paginaAtual - 1})" aria-label="Página anterior">← Anterior</button>`;

        // Números de Página
        const delta = 2;
        const alcance = [];
        for (let i = Math.max(2, paginaAtual - delta); i <= Math.min(totalPaginas - 1, paginaAtual + delta); i++) {
            alcance.push(i);
        }

        if (paginaAtual - delta > 2) {
            alcance.unshift('...');
        }
        if (paginaAtual + delta < totalPaginas - 1) {
            alcance.push('...');
        }

        alcance.unshift(1);
        if (totalPaginas > 1) {
            alcance.push(totalPaginas);
        }

        alcance.forEach(item => {
            if (item === '...') {
                botoesHtml += `<span class="paginacao-pontos" aria-hidden="true">…</span>`;
            } else {
                const ativa = item === paginaAtual;
                botoesHtml += `<button type="button" class="btn-paginacao ${ativa ? 'ativa' : ''}" ${ativa ? 'aria-current="page"' : ''} onclick="${nomeFuncao}(${item})">${item}</button>`;
            }
        });

        // Botão Seguinte
        const desabilitarSeguinte = paginaAtual === totalPaginas;
        botoesHtml += `<button type="button" class="btn-paginacao" ${desabilitarSeguinte ? 'disabled' : ''} onclick="${nomeFuncao}(${paginaAtual + 1})" aria-label="Página seguinte">Seguinte →</button>`;

        return `
            <div class="paginacao-info">Página ${paginaAtual} de ${totalPaginas} (${totalItens} produtos)</div>
            <div class="paginacao-botoes">${botoesHtml}</div>
        `;
    },

    escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    },

    renderLoteCard(l) {
        const badgeClass = { EM_CRESCIMENTO: 'badge-crescimento', PRONTO_PARA_COLHEITA: 'badge-pronto', RESERVADO: 'badge-reservado', VENDIDO: 'badge-vendido' };
        const badgeLabel = { EM_CRESCIMENTO: 'Em Crescimento', PRONTO_PARA_COLHEITA: 'Pronto p/ Colheita', RESERVADO: 'Reservado', VENDIDO: 'Vendido' };
        
        // SonarLint S7781: replaceAll em vez de replace com regex
        const tel = (l.produtorTelemovel || '').replaceAll('+', '');
        const rawLocalidade = [l.provincia, l.distrito, l.bairro].filter(Boolean).join(' · ') || l.associacaoLocalidade;
        const localidade = this.escapeHtml(rawLocalidade);
        const nomeCultura = this.escapeHtml(l.culturaNome || '');
        const nomeProdutor = this.escapeHtml(l.produtorNome || 'N/D');
        const observacoes = this.escapeHtml(l.observacoes || '');
        const msgWa = encodeURIComponent('Olá, vi o produto ' + (l.culturaNome || '') + ' (' + l.quantidadeEstimada + ' ' + (l.unidadeMedida||'KG') + ') na plataforma AgreTech e tenho interesse. Podemos negociar?');

        // SonarLint S7773: Number.parseFloat
        return `<div class="card produto-card">
            <div class="card-header">
                <h3> ${nomeCultura}</h3>
                <span class="badge ${badgeClass[l.estado] || ''}">${badgeLabel[l.estado] || l.estado}</span>
            </div>
            <div class="card-body">
                <p class="destaque">${Number.parseFloat(l.quantidadeEstimada).toLocaleString('pt-MZ')} ${this.escapeHtml(l.unidadeMedida || 'KG')}</p>
                ${l.precoPorUnidade ? '<p> Preço: <strong>' + Number.parseFloat(l.precoPorUnidade).toLocaleString('pt-MZ') + ' MT/' + this.escapeHtml(l.unidadeMedida||'KG') + '</strong></p>' : ''}
                <p> Agricultor: <strong>${nomeProdutor}</strong></p>
                ${localidade ? '<p> Localidade: <strong>' + localidade + '</strong></p>' : ''}
                <p> Colheita prevista: <strong>${this.escapeHtml(l.dataColheitaPrevista || 'N/D')}</strong></p>
                ${observacoes ? '<p> ' + observacoes + '</p>' : ''}
            </div>
            <div class="botoes-contacto">
                ${tel ? '<a href="https://wa.me/' + tel + '?text=' + msgWa + '" target="_blank" rel="noopener noreferrer" class="btn btn-whatsapp" onclick="App.registarIntencaoNegocio(' + l.id + ', \'WHATSAPP\')"> WhatsApp</a>' : ''}
                ${l.produtorTelemovel ? '<a href="tel:' + l.produtorTelemovel + '" class="btn btn-chamada" onclick="App.registarIntencaoNegocio(' + l.id + ', \'TELEFONE\')"> Ligar</a>' : ''}
            </div>
        </div>`;
    },

    async registarIntencaoNegocio(loteId, canal = 'WHATSAPP') {
        if (!Api.isLoggedIn()) return;
        try {
            await Api.registarContacto({
                loteId: loteId,
                canal: canal,
                notas: 'Intenção iniciada via catálogo (' + (canal === 'WHATSAPP' ? 'WhatsApp' : 'Chamada telefónica') + ')'
            });
        } catch (e) {
            console.debug('Registo de intenção de negócio:', e);
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
            console.warn('Falha em Api.getLotes(), tentando fallback para catálogo:', e);
            try {
                lotes = await Api.getCatalogo({});
            } catch (e2) {
                console.warn('Falha em Api.getCatalogo():', e2);
                lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Não foi possível carregar os produtos de momento.</p><button type="button" class="btn btn-sm btn-primario" onclick="App.carregarLotesDashboard()">Tentar novamente</button></div>';
                return;
            }
        }

        if (!lotes || lotes.length === 0) {
            lista.innerHTML = '<div class="empty-state"><div class="icone"></div><p>Nenhum produto registado. Clique em "Novo Produto" para começar.</p></div>';
            return;
        }

        lista.innerHTML = '<div class="tabela-wrapper"><table><thead><tr><th>Produto agrícola</th><th>Agricultor</th><th>Quantidade</th><th>Colheita</th><th>Estado</th><th>Ações</th></tr></thead><tbody>' +
            lotes.map(l => {
                const badgeClass = { EM_CRESCIMENTO: 'badge-crescimento', PRONTO_PARA_COLHEITA: 'badge-pronto', RESERVADO: 'badge-reservado', VENDIDO: 'badge-vendido' };
                const badgeLabel = { EM_CRESCIMENTO: 'Em Crescimento', PRONTO_PARA_COLHEITA: 'Pronto', RESERVADO: 'Reservado', VENDIDO: 'Vendido' };
                return `<tr>
                    <td>${l.culturaNome}</td>
                    <td>${l.produtorNome}<br><small>${[l.provincia, l.distrito, l.bairro].filter(Boolean).join(' · ') || l.associacaoLocalidade || ''}</small></td>
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
        const souAdmin = Api.user?.perfil === 'ADMIN';
        const campoProdutor = document.getElementById('campo-produtor-lote');
        const produtorProprio = document.getElementById('produtor-proprio-lote');
        if (selCultura && this.culturas) {
            selCultura.innerHTML = '<option value="">Selecione o produto agrícola</option>' +
                this.culturas.map(c => '<option value="' + c.id + '">' + c.nome + '</option>').join('');
        }
        if (campoProdutor) campoProdutor.hidden = !souAdmin;
        if (produtorProprio) produtorProprio.hidden = souAdmin;
        if (selProdutor) {
            selProdutor.required = souAdmin;
            selProdutor.disabled = !souAdmin;
        }
        if (!souAdmin) {
            const nomeProprio = document.getElementById('produtor-proprio-nome');
            if (nomeProprio) nomeProprio.textContent = Api.user?.nome || 'A sua conta';
            return;
        }
        if (selProdutor) {
            let produtores;
            try { 
                produtores = await Api.getProdutores(); 
            } catch (e) { 
                console.warn('Falha ao obter agricultores:', e);
                produtores = []; 
            }
            selProdutor.innerHTML = '<option value="">Selecione o agricultor</option>' +
                produtores.map(p => {
                    const loc = [p.provincia, p.distrito, p.bairro].filter(Boolean).join(' · ') || (p.associacaoLocalidade || '');
                    return '<option value="' + p.id + '">' + p.nome + (loc ? ' (' + loc + ')' : '') + '</option>';
                }).join('');
        }
    },

    async submeterNovoLote(form) {
        // SonarLint S7773: Number.parseInt com base 10 e Number.parseFloat
        const data = {
            produtorId: Api.user?.perfil === 'ADMIN'
                ? Number.parseInt(form.produtorId.value, 10)
                : (Api.user?.produtorId || null),
            culturaId: Number.parseInt(form.culturaId.value, 10),
            quantidadeEstimada: Number.parseFloat(form.quantidadeEstimada.value),
            dataColheitaPrevista: form.dataColheitaPrevista.value,
            dataSementeira: form.dataSementeira.value || null,
            precoPorUnidade: form.precoPorUnidade.value ? Number.parseFloat(form.precoPorUnidade.value) : null,
            observacoes: form.observacoes.value || null,
            estado: 'EM_CRESCIMENTO'
        };

        try {
            await Api.criarLote(data);
            if (typeof window.showToast === 'function') {
                window.showToast('Produto registado com sucesso no catálogo!', 'sucesso');
            } else {
                alert('Produto registado com sucesso!');
            }
            fecharModal('modal-novo-lote');
            if (typeof window.carregarLotes === 'function') {
                await window.carregarLotes();
                if (typeof window.atualizarKPIs === 'function') await window.atualizarKPIs();
            } else {
                this.carregarLotesDashboard();
            }
        } catch (e) {
            if (typeof window.showToast === 'function') {
                window.showToast('Erro: ' + e.message, 'erro');
            } else {
                alert('Erro: ' + e.message);
            }
        }
    },

    async carregarProdutoresDashboard() {
        const lista = document.getElementById('produtores-lista');
        const contador = document.getElementById('produtores-count-label');
        if (!lista) return;

        let produtores;
        try { 
            produtores = await Api.getProdutores(); 
        } catch(e) { 
            console.warn('Falha ao carregar agricultores online:', e);
            produtores = []; 
        }

        if (!produtores || produtores.length === 0) {
            lista.innerHTML = '<div class="col-12 empty-state"><i class="bi bi-people fs-1 text-muted"></i><p class="mt-2 mb-0">Nenhum agricultor registado.</p></div>';
            if (contador) contador.textContent = '0 agricultor(es)';
            return;
        }

        if (contador) contador.textContent = produtores.length + ' agricultor(es)';

        lista.innerHTML = produtores.map(p => {
            const loc = [p.provincia, p.distrito, p.bairro].filter(Boolean).join(' · ') || (p.associacaoLocalidade || '');
            const tel = (p.telemovel || '').replaceAll('+', '');
            const inicial = (p.nome || 'A').trim().charAt(0).toUpperCase();
            return `<div class="col-12 col-md-6 col-lg-4">
                <div class="card h-100 border shadow-sm rounded-3">
                    <div class="card-header bg-white py-3 d-flex align-items-center gap-2">
                        <div class="user-avatar" style="width:38px;height:38px;border-radius:50%;background:#55745D;color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:0.9rem;">
                            ${inicial}
                        </div>
                        <div class="overflow-hidden">
                            <h6 class="fw-bold mb-0 text-truncate">${p.nome}</h6>
                            <small class="text-muted"><i class="bi bi-geo-alt"></i> ${loc || 'Moçambique'}</small>
                        </div>
                    </div>
                    <div class="card-body">
                        <p class="mb-2 text-muted small"><i class="bi bi-telephone text-success me-1"></i> <strong>${p.telemovel}</strong></p>
                        ${p.localizacaoDetalhada ? '<p class="small text-muted mb-0"><i class="bi bi-pin-map text-muted me-1"></i>' + p.localizacaoDetalhada + '</p>' : ''}
                    </div>
                    <div class="card-footer bg-light border-0 d-flex gap-2">
                        ${tel ? `<a href="https://wa.me/${tel}" target="_blank" class="btn btn-outline-success btn-sm w-50 rounded-pill"><i class="bi bi-whatsapp"></i> WhatsApp</a>` : ''}
                        ${p.telemovel ? `<a href="tel:${p.telemovel}" class="btn btn-outline-secondary btn-sm w-50 rounded-pill"><i class="bi bi-telephone"></i> Ligar</a>` : ''}
                    </div>
                </div>
            </div>`;
        }).join('');
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
