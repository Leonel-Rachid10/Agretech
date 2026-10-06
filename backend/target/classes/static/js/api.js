// === AgriTech Dondo — Cliente HTTP REST com JWT ===
const API_BASE = '/api';

const Api = {
    token: localStorage.getItem('agritech_token'),
    user: JSON.parse(localStorage.getItem('agritech_user') || 'null'),

    setAuth(loginResponse) {
        this.token = loginResponse.token;
        this.user = { id: loginResponse.id, nome: loginResponse.nome, telemovel: loginResponse.telemovel, perfil: loginResponse.perfil };
        localStorage.setItem('agritech_token', loginResponse.token);
        localStorage.setItem('agritech_user', JSON.stringify(this.user));
    },

    logout() {
        this.token = null;
        this.user = null;
        localStorage.removeItem('agritech_token');
        localStorage.removeItem('agritech_user');
        window.location.href = '/login.html';
    },

    isLoggedIn() {
        return !!this.token;
    },

    headers(json = true) {
        const h = {};
        if (json) h['Content-Type'] = 'application/json';
        if (this.token) h['Authorization'] = 'Bearer ' + this.token;
        return h;
    },

    async request(method, path, body = null) {
        const opts = { method, headers: this.headers() };
        if (body) opts.body = JSON.stringify(body);
        const res = await fetch(API_BASE + path, opts);
        if (res.status === 401) {
            // Só termina sessão se já existia um token (pedido autenticado que expirou).
            // Num login/registo falhado ainda não há sessão nenhuma — mostrar erro normal,
            // não redirecionar (isto evitava que a mensagem de erro aparecesse no login).
            if (this.token) { this.logout(); return null; }
            const err = await res.text();
            throw new Error(err || 'Erro na requisição');
        }
        if (res.status === 204) return null;
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Erro na requisição');
        }
        const ct = res.headers.get('Content-Type') || '';
        if (ct.includes('application/json')) return res.json();
        return res;
    },

    get(path) { return this.request('GET', path); },
    post(path, body) { return this.request('POST', path, body); },
    put(path, body) { return this.request('PUT', path, body); },
    patch(path, body) { return this.request('PATCH', path, body); },
    del(path) { return this.request('DELETE', path); },

    // Auth
    login(nome, senha) { return this.post('/auth/login', { nome, senha }); },
    registar(data) { return this.post('/auth/registo', data); },

    // Culturas
    getCulturas() { return this.get('/culturas'); },

    // Associações
    getAssociacoes() { return this.get('/associacoes'); },
    criarAssociacao(data) { return this.post('/associacoes', data); },

    // Produtores
    getProdutores() { return this.get('/produtores'); },
    getProdutoresPorAssociacao(id) { return this.get('/produtores/associacao/' + id); },
    criarProdutor(data) { return this.post('/produtores', data); },

    // Lotes de Produção / Catálogo
    getCatalogo(params = {}) {
        const qs = new URLSearchParams();
        if (params.culturaId) qs.set('culturaId', params.culturaId);
        if (params.localidade) qs.set('localidade', params.localidade);
        if (params.estado) qs.set('estado', params.estado);
        if (params.quantidadeMinima) qs.set('quantidadeMinima', params.quantidadeMinima);
        return this.get('/lotes/catalogo?' + qs.toString());
    },
    getLotes() { return this.get('/lotes'); },
    getLotesPorAssociacao(id) { return this.get('/lotes/associacao/' + id); },
    criarLote(data) { return this.post('/lotes', data); },
    atualizarLote(id, data) { return this.put('/lotes/' + id, data); },
    atualizarEstadoLote(id, estado) { return this.patch('/lotes/' + id + '/estado?novoEstado=' + estado); },
    removerLote(id) { return this.del('/lotes/' + id); },

    // Reservas
    registarContacto(data) { return this.post('/reservas', data); },

    // Sincronização Offline
    sincronizar(batch) { return this.post('/sync', batch); },

    // Relatório PDF
    async downloadRelatorioPdf() {
        const res = await fetch(API_BASE + '/relatorios/producao/pdf', { headers: this.headers(false) });
        if (!res.ok) throw new Error('Erro ao gerar relatório');
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'AgriTech_Dondo_Relatorio.pdf';
        a.click();
        URL.revokeObjectURL(url);
    }
};