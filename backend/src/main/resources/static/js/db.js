// === AgriTech Dondo — Camada IndexedDB para Operação Offline ===
const DB_NAME = 'agritech_dondo_offline';
const DB_VERSION = 1;

const AgriDB = {
    db: null,

    async open() {
        if (this.db) return this.db;
        return new Promise((resolve, reject) => {
            const req = indexedDB.open(DB_NAME, DB_VERSION);
            req.onupgradeneeded = (e) => {
                const db = e.target.result;
                // Fila de sincronização para dados criados offline
                if (!db.objectStoreNames.contains('sync_queue')) {
                    const store = db.createObjectStore('sync_queue', { keyPath: 'clientUuid' });
                    store.createIndex('tipo', 'tipo', { unique: false });
                    store.createIndex('timestamp', 'timestamp', { unique: false });
                }
                // Cache local de culturas
                if (!db.objectStoreNames.contains('culturas')) {
                    db.createObjectStore('culturas', { keyPath: 'id' });
                }
                // Cache local de associações
                if (!db.objectStoreNames.contains('associacoes')) {
                    db.createObjectStore('associacoes', { keyPath: 'id' });
                }
                // Cache local de lotes do catálogo
                if (!db.objectStoreNames.contains('lotes_cache')) {
                    db.createObjectStore('lotes_cache', { keyPath: 'id' });
                }
            };
            req.onsuccess = (e) => { this.db = e.target.result; resolve(this.db); };
            req.onerror = (e) => reject(e.target.error);
        });
    },

    generateUuid() {
        return 'offline_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    },

    async addToSyncQueue(tipo, dados) {
        const db = await this.open();
        const item = {
            clientUuid: this.generateUuid(),
            tipo,
            dados,
            timestamp: new Date().toISOString(),
            sincronizado: false
        };
        return new Promise((resolve, reject) => {
            const tx = db.transaction('sync_queue', 'readwrite');
            tx.objectStore('sync_queue').add(item);
            tx.oncomplete = () => resolve(item);
            tx.onerror = (e) => reject(e.target.error);
        });
    },

    async getSyncQueue() {
        const db = await this.open();
        return new Promise((resolve, reject) => {
            const tx = db.transaction('sync_queue', 'readonly');
            const req = tx.objectStore('sync_queue').getAll();
            req.onsuccess = () => resolve(req.result.filter(i => !i.sincronizado));
            req.onerror = (e) => reject(e.target.error);
        });
    },

    async clearSyncQueue() {
        const db = await this.open();
        return new Promise((resolve, reject) => {
            const tx = db.transaction('sync_queue', 'readwrite');
            tx.objectStore('sync_queue').clear();
            tx.oncomplete = () => resolve();
            tx.onerror = (e) => reject(e.target.error);
        });
    },

    async markSynced(clientUuid) {
        const db = await this.open();
        return new Promise((resolve, reject) => {
            const tx = db.transaction('sync_queue', 'readwrite');
            const store = tx.objectStore('sync_queue');
            const req = store.get(clientUuid);
            req.onsuccess = () => {
                const item = req.result;
                if (item) { item.sincronizado = true; store.put(item); }
            };
            tx.oncomplete = () => resolve();
            tx.onerror = (e) => reject(e.target.error);
        });
    },

    async getSyncQueueCount() {
        const items = await this.getSyncQueue();
        return items.length;
    },

    // Cache helpers
    async cacheList(storeName, items) {
        const db = await this.open();
        return new Promise((resolve, reject) => {
            const tx = db.transaction(storeName, 'readwrite');
            const store = tx.objectStore(storeName);
            store.clear();
            items.forEach(item => store.put(item));
            tx.oncomplete = () => resolve();
            tx.onerror = (e) => reject(e.target.error);
        });
    },

    async getCachedList(storeName) {
        const db = await this.open();
        return new Promise((resolve, reject) => {
            const tx = db.transaction(storeName, 'readonly');
            const req = tx.objectStore(storeName).getAll();
            req.onsuccess = () => resolve(req.result);
            req.onerror = (e) => reject(e.target.error);
        });
    }
};
