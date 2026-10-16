// Localizações disponíveis na plataforma. Chinde corrige a repetição de Inhassunge na lista recebida.
window.AgriLocations = {
    provinces: {
        'Sofala': ['Beira', 'Búzi', 'Caia', 'Chemba', 'Cheringoma', 'Chibabava', 'Dondo', 'Gorongosa', 'Machanga', 'Maringué', 'Marromeu', 'Muanza', 'Nhamatanda'],
        'Tete': ['Angónia', 'Cahora-Bassa', 'Changara', 'Chifunde', 'Chiúta', 'Dômuè', 'Dóa', 'Macanga', 'Magoé', 'Marávia', 'Moatize', 'Mutarara', 'Tete', 'Tsangano', 'Zumbo'],
        'Manica': ['Báruè', 'Chimoio', 'Gondola', 'Guro', 'Machaze', 'Macossa', 'Manica', 'Mossurize', 'Sussundenga', 'Tambara', 'Vanduzi', 'Macate'],
        'Zambézia': ['Alto Molócuè', 'Chinde', 'Derre', 'Gilé', 'Gurúè', 'Ile', 'Inhassunge', 'Lugela', 'Luabo', 'Maganja da Costa', 'Milange', 'Mocuba', 'Mocubela', 'Molumbo', 'Mopeia', 'Morrumbala', 'Mulevala', 'Namacurra', 'Namarroi', 'Nicoadala', 'Pebane', 'Quelimane']
    },
    provinceLabels: {
        'Sofala': 'Sofala (Beira)',
        'Tete': 'Tete (Cidade de Tete)',
        'Manica': 'Manica (Chimoio)',
        'Zambézia': 'Zambézia (Quelimane)'
    },
    zones: {
        'Dondo': ['Campo', 'Bairro Central', 'Consito', 'Mafarinha', 'Canhandula', 'Nhamaiabwe', 'Chinamacondo', 'Savana', 'Mafambisse', 'Mútua'],
        'Beira': ['Alto da Manga', 'Chaimite', 'Chingussura', 'Chipangara', 'Chota', 'Esturro', 'Inhamízua', 'Macurungo', 'Macuti', 'Mananga', 'Manga Mascarenhas', 'Maraza', 'Matacuane', 'Matadouro', 'Muave', 'Mungassa', 'Munhava-Central', 'Ndunda', 'Nhaconjo', 'Nhangau', 'Nhangoma', 'Pioneiros', 'Ponta-Gêa', 'Tchonja', 'Vaz', 'Vila Massane']
    }
};

// Mantém compatibilidade com os registos anteriores enquanto a interface passa a usar "zona".
window.AgriLocations.neighborhoods = window.AgriLocations.zones;

window.populateProvinces = function (selectId, emptyLabel) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = `<option value="">${emptyLabel}</option>` +
        Object.entries(window.AgriLocations.provinceLabels)
            .map(([value, label]) => `<option value="${value}">${label}</option>`).join('');
};

window.populateDistricts = function (provinceSelect, districtSelect, emptyLabel) {
    const province = document.getElementById(provinceSelect);
    const district = document.getElementById(districtSelect);
    if (!province || !district) return;
    const districts = window.AgriLocations.provinces[province.value] || [];
    district.innerHTML = `<option value="">${emptyLabel || 'Todos os Distritos'}</option>` +
        districts.map(name => `<option value="${name}">${name}</option>`).join('');
    district.disabled = districts.length === 0;
};

window.populateZones = function (districtSelect, zoneInput, zoneOptions, placeholder) {
    const district = document.getElementById(districtSelect);
    const input = document.getElementById(zoneInput);
    const options = document.getElementById(zoneOptions);
    if (!district || !input || !options) return;

    const zones = window.AgriLocations.zones[district.value] || [];
    input.value = '';
    input.disabled = !district.value;
    input.placeholder = district.value
        ? (placeholder || 'Ex.: Campo')
        : 'Selecione o distrito primeiro';
    options.innerHTML = zones.map(zone => `<option value="${zone}"></option>`).join('');
};
