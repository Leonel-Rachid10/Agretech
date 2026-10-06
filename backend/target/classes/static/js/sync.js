// === AgriTech Dondo — Motor de Sincronização Automática (RF07) ===
const SyncManager = {
    isSyncing: false,

    init() {
        window.addEventListener('online', () => this.onOnline());
        window.addEventListener('offline', () => this.onOffline());
        this.updateBar();
        // Tentar sincronizar ao iniciar se online
        if (navigator.onLine) this.sync();
    },

    onOnline() {
        console.log('[Sync] Rede detectada — iniciando sincronização...');
        this.updateBar();
        this.sync();
    },

    onOffline() {
        console.log('[Sync] Sem rede — modo offline ativo');
        this.updateBar();
    },

    async updateBar() {
        const bar = document.getElementById('sync-bar');
        if (!bar) return;
        const count = await AgriDB.getSyncQueueCount();

        if (!navigator.onLine) {
            bar.className = 'sync-bar active';
            bar.textContent = ' Modo Offline — ' + (count > 0 ? count + ' registo(s) pendente(s) de sincronização' : 'Dados serão sincronizados quando houver rede');
        } else if (count > 0) {
            bar.className = 'sync-bar active online';
            bar.textContent = ' Sincronizando ' + count + ' registo(s) pendente(s)...';
        } else {
            bar.className = 'sync-bar';
            bar.textContent = '';
        }
    },

    async sync() {
        if (this.isSyncing || !navigator.onLine) return;
        this.isSyncing = true;

        try {
            const queue = await AgriDB.getSyncQueue();
            if (queue.length === 0) {
                this.isSyncing = false;
                this.updateBar();
                return;
            }

            console.log('[Sync] Processando ' + queue.length + ' itens da fila...');

            const batch = {
                clientTimestamp: new Date().toISOString(),
                pontoFocalTelemovel: Api.user ? Api.user.telemovel : 'desconhecido',
                produtoresNovos: [],
                lotesNovos: [],
                lotesAtualizados: []
            };

            for (const item of queue) {
                if (item.tipo === 'novo_produtor') {
                    batch.produtoresNovos.push(item.dados);
                } else if (item.tipo === 'novo_lote') {
                    item.dados.clientUuid = item.clientUuid;
                    batch.lotesNovos.push(item.dados);
                } else if (item.tipo === 'atualizar_lote') {
                    batch.lotesAtualizados.push(item.dados);
                }
            }

            const result = await Api.sincronizar(batch);

            if (result && result.sucesso) {
                await AgriDB.clearSyncQueue();
                console.log('[Sync] Sincronização concluída com sucesso!');
                // Recarregar dados na página se possível
                if (typeof App !== 'undefined' && App.refreshCurrentView) {
                    App.refreshCurrentView();
                }
            } else if (result) {
                console.warn('[Sync] Sincronização parcial:', result.erros);
                // Marcar os que foram sincronizados
                for (const item of queue) {
                    await AgriDB.markSynced(item.clientUuid);
                }
            }
        } catch (err) {
            console.error('[Sync] Erro na sincronização:', err);
        }

        this.isSyncing = false;
        this.updateBar();
    }
};
