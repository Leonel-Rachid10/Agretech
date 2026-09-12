# AgriTech Dondo

**Plataforma Digital para Conexão de Produtores Agrícolas e Compradores no Distrito do Dondo**

*Documento de Especificação e Planeamento do Projecto*

Autor(es): [a preencher]
Instituição / Organização: [a preencher]
Local: Maputo — Distrito do Dondo, Sofala, Moçambique
Setembro de 2026

---

## Índice

1. Resumo Executivo
2. Introdução e Visão Geral do Projecto
   - 2.1 Contexto e Justificativa
   - 2.2 Estratégia de Desenvolvimento Remoto (Maputo → Dondo)
   - 2.3 Objectivos do Sistema
3. Levantamento de Requisitos
   - 3.1 Requisitos Funcionais (RF)
   - 3.2 Requisitos Não-Funcionais (RNF)
4. Arquitetura Técnica e Tecnologias
   - 4.1 Pilha Tecnológica (Tech Stack)
   - 4.2 Padrão Arquitetural
5. Modelação e Esquema da Base de Dados (MySQL)
6. Equipa do Projecto e Responsabilidades
7. Plano de Execução
8. Orçamento e Recursos Necessários
9. Análise de Riscos e Mitigação
10. Indicadores de Sucesso (KPIs)
11. Conclusão
12. Referências
13. Anexos

---

## 1. Resumo Executivo

O Distrito do Dondo, na Província de Sofala, ocupa uma posição estratégica no Corredor da Beira, com um potencial produtivo considerável nos setores agrícola e hortícola. Ainda assim, o setor perde muito na fase pós-colheita: falta mercado e escoamento imediato para a produção, os produtores dependem fortemente de intermediários informais (os "mapezas") e as associações e cooperativas locais não têm forma de registar ou planear aquilo que produzem.

É este o problema que o AgriTech Dondo se propõe resolver. Trata-se de uma plataforma digital offline-first que liga diretamente os produtores e associações agrícolas do Dondo a compradores grossistas, hotéis e restaurantes da Beira — permitindo registar lotes de produção mesmo sem internet, manter um catálogo agrícola atualizado em tempo real para os compradores e digitalizar a gestão de membros e safras dentro de cada associação.

O desenvolvimento decorre a partir de Maputo, mas com validação constante no terreno: um ponto focal no Dondo acompanha o processo, os testes simulam redes 3G/2G/offline e as versões da aplicação vão sendo enviadas periodicamente para validação prática. O projecto está dividido em 5 fases ao longo de 12 semanas e termina com o lançamento de um MVP (Produto Mínimo Viável) centrado nas associações do Dondo e nos compradores da Beira.

## 2. Introdução e Visão Geral do Projecto

### 2.1 Contexto e Justificativa

O Distrito do Dondo, na Província de Sofala, ocupa uma posição geográfica e económica estratégica: funciona como um entroncamento logístico importante no Corredor da Beira e tem um potencial produtivo elevado no setor agrícola e hortícola, sobretudo em zonas como Mafambisse, Chinamacondo e na periferia da vila do Dondo. Apesar disso, o setor enfrenta vários gargalos estruturais:

- Perdas pós-colheita elevadas, por falta de mercado e de escoamento imediato da produção.
- Dependência forte de intermediários informais (os "mapezas"), que reduz a margem de lucro dos produtores locais.
- Ausência de histórico de produção e de planeamento nas associações e cooperativas agrícolas.
- Infraestrutura tecnológica limitada: internet instável, dados móveis caros e uma grande diversidade de dispositivos, muitos deles com capacidades reduzidas.

### 2.2 Estratégia de Desenvolvimento Remoto (Maputo → Dondo)

O desenvolvimento do software acontece em Maputo, mas o foco operacional e de mercado mantém-se inteiramente no Distrito do Dondo e no Corredor Dondo-Beira. Isto exige alguns cuidados adicionais:

- **Ponto focal no terreno** — uma parceria com líderes de associações agrícolas locais e técnicos no Dondo, para validação e testes práticos.
- **Testes com simulação de rede** — perfis de rede restrita (3G/2G/offline) usados em Maputo, para garantir que a aplicação aguenta as condições reais das zonas rurais do Dondo.
- **Validação incremental** — testes de usabilidade e recolha de requisitos feitos à distância, através de reuniões agendadas, mensagens e envio periódico de compilações PWA de teste.

### 2.3 Objectivos do Sistema

**Objectivo geral:** desenvolver uma plataforma digital robusta e adaptada ao contexto do Dondo, que ligue diretamente a oferta de produtos agrícolas à procura dos compradores e que dê às associações locais ferramentas de gestão.

**Objectivos específicos:**

1. Permitir que líderes de associações e produtores registem lotes de produção agrícola mesmo sem internet (offline-first).
2. Disponibilizar aos compradores grossistas, hotéis e restaurantes da Beira e do Dondo um catálogo agrícola atualizado em tempo real.
3. Digitalizar a gestão de membros, safras e insumos dentro das associações locais.
4. Facilitar o contacto direto entre produtor e comprador, por chamada telefónica ou mensagem de WhatsApp.

## 3. Levantamento de Requisitos

### 3.1 Requisitos Funcionais (RF)

| ID | Descrição |
|---|---|
| RF01 | **Gestão de Utilizadores e Autenticação** — regista e autentica utilizadores por número de telemóvel e senha (ADMIN, GESTOR_ASSOCIACAO, PRODUTOR, COMPRADOR). |
| RF02 | **Gestão de Associações** — cria e gere perfis de associações do Dondo, associando membros, localizações geográficas e pontos focais. |
| RF03 | **Gestão de Lotes de Produção (Oferta)** — regista culturas, quantidades estimadas (kg/toneladas), data de colheita e estado (EM_CRESCIMENTO, PRONTO_PARA_COLHEITA, RESERVADO, VENDIDO). |
| RF04 | **Catálogo Agrícola e Pesquisa (Procura)** — filtra por cultura, quantidade disponível e localização, para os compradores pesquisarem o catálogo. |
| RF05 | **Conectividade de Negócio** — disponibiliza botões de contacto direto via WhatsApp ou chamada telefónica para o responsável do lote. |
| RF06 | **Relatórios PDF** — gera relatórios consolidados em PDF com volume de produção e previsão de safras. |
| RF07 | **Sincronização Offline-First** — permite o registo sem internet e sincroniza automaticamente assim que deteta rede. |
| RF08 | **Gestão de Ponto Focal e Validação Remota** — disponibiliza um painel de supervisão para acompanhar as sincronizações enviadas pelos pontos focais no terreno. |

### 3.2 Requisitos Não-Funcionais (RNF)

| ID | Descrição |
|---|---|
| RNF01 | **Desempenho e Eficiência de Dados** — interface leve (menos de 2 MB de tráfego inicial) e carregamento em menos de 3 segundos sob redes 3G. |
| RNF02 | **Disponibilidade e Resiliência** — armazenamento local (IndexedDB) para garantir operação contínua offline. |
| RNF03 | **Usabilidade e Acessibilidade** — design simples e intuitivo, otimizado para ecrãs de telemóvel. |
| RNF04 | **Segurança** — proteção de dados com encriptação de palavras-passe e autenticação baseada em tokens JWT. |

## 4. Arquitetura Técnica e Tecnologias

### 4.1 Pilha Tecnológica (Tech Stack)

- **Linguagem de Backend:** Java 17 com framework Spring Boot.
- **Framework Web/API:** Spring Data JPA, Spring Security JWT, Spring MVC.
- **Base de Dados:** MySQL 8.0.
- **Camada de Apresentação:** Web Responsiva / Progressive Web App (PWA), com suporte a Service Workers e IndexedDB.
- **Relatórios:** JasperReports, para geração nativa de resumos operacionais em PDF.

### 4.2 Padrão Arquitetural

Arquitetura em Camadas (Layered Architecture):

1. **Controller Layer** — endpoints RESTful para comunicação com o PWA.
2. **Service Layer** — regras de negócio, validações e controlo transacional.
3. **Repository Layer (DAO)** — persistência de dados com Spring Data JPA.
4. **Database Layer** — modelo relacional em MySQL.

## 5. Modelação e Esquema da Base de Dados (MySQL)

O esquema relacional que se segue cobre as entidades principais do sistema — utilizadores, associações, produtores, culturas, lotes de produção e registos de contacto/negócio.

```sql
CREATE DATABASE IF NOT EXISTS agritech_dondo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agritech_dondo_db;

-- 1. TABELA DE UTILIZADORES
CREATE TABLE utilizador (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telemovel VARCHAR(20) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'GESTOR_ASSOCIACAO', 'PRODUTOR', 'COMPRADOR') NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. TABELA DE ASSOCIAÇÕES AGRÍCOLAS DO DONDO
CREATE TABLE associacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    localidade VARCHAR(100) NOT NULL,
    povoado VARCHAR(100),
    contacto_principal VARCHAR(20),
    gestor_id BIGINT,
    ponto_focal_nome VARCHAR(100),
    ponto_focal_telemovel VARCHAR(20),
    data_registo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_associacao_gestor FOREIGN KEY (gestor_id) REFERENCES utilizador(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 3. TABELA DE PRODUTORES
CREATE TABLE produtor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telemovel VARCHAR(20) NOT NULL,
    associacao_id BIGINT NOT NULL,
    localizacao_detalhada TEXT,
    data_registo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_produtor_associacao FOREIGN KEY (associacao_id) REFERENCES associacao(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. TABELA DE CULTURAS AGRÍCOLAS
CREATE TABLE cultura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(50) NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL DEFAULT 'KG'
) ENGINE=InnoDB;

-- 5. TABELA DE LOTES DE PRODUÇÃO (OFERTA)
CREATE TABLE lote_producao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produtor_id BIGINT NOT NULL,
    cultura_id BIGINT NOT NULL,
    quantidade_estimada DECIMAL(10,2) NOT NULL,
    data_sementeira DATE,
    data_colheita_prevista DATE NOT NULL,
    estado ENUM('EM_CRESCIMENTO', 'PRONTO_PARA_COLHEITA', 'RESERVADO', 'VENDIDO') NOT NULL DEFAULT 'EM_CRESCIMENTO',
    preco_por_unidade DECIMAL(10,2),
    observacoes TEXT,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lote_produtor FOREIGN KEY (produtor_id) REFERENCES produtor(id) ON DELETE CASCADE,
    CONSTRAINT fk_lote_cultura FOREIGN KEY (cultura_id) REFERENCES cultura(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. TABELA DE REGISTO DE CONTACTOS E INTERAÇÕES DE NEGÓCIO
CREATE TABLE reserva_contacto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lote_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    data_contacto TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_negocio ENUM('INICIADO', 'EM_NEGOCIACAO', 'CONCLUIDO', 'CANCELADO') DEFAULT 'INICIADO',
    CONSTRAINT fk_reserva_lote FOREIGN KEY (lote_id) REFERENCES lote_producao(id) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_comprador FOREIGN KEY (comprador_id) REFERENCES utilizador(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- DADOS INICIAIS
INSERT INTO cultura (nome, categoria, unidade_medida) VALUES
('Tomate', 'Hortícola', 'KG'),
('Cebola', 'Hortícola', 'KG'),
('Repolho', 'Hortícola', 'KG'),
('Milho', 'Cereal', 'KG'),
('Feijão Nhemba', 'Leguminosa', 'KG'),
('Mandioca', 'Tubérculo', 'KG');
```

## 6. Equipa do Projecto e Responsabilidades

Preenche esta tabela com os nomes reais da equipa — os papéis indicados abaixo são apenas uma proposta, com base no âmbito técnico do projecto, e podem ser ajustados.

| Papel | Nome | Responsabilidades principais |
|---|---|---|
| Coordenador(a) do Projecto | [a preencher] | Supervisão geral, gestão de stakeholders e relação com as associações do Dondo. |
| Programador(a) Backend (Java/Spring Boot) | [a preencher] | Desenvolvimento da API REST, regras de negócio e integração com o MySQL. |
| Programador(a) Frontend (PWA) | [a preencher] | Interface web responsiva, Service Workers e armazenamento local (IndexedDB). |
| Ponto Focal no Dondo | [a preencher] | Validação no terreno, recolha de requisitos junto das associações e apoio aos testes piloto. |
| Gestor(a) de Base de Dados / QA | [a preencher] | Modelação de dados, testes de qualidade e validação da sincronização offline. |

## 7. Plano de Execução

O desenvolvimento está dividido em 5 fases ao longo de 12 semanas, que refletem a estratégia de desenvolvimento remoto entre Maputo e o Dondo.

| Fase | Duração | Atividades Principais |
|---|---|---|
| Fase 1 | Semanas 1–2 | Definição do ponto focal local no Dondo e levantamento remoto com líderes em Mafambisse. |
| Fase 2 | Semanas 3–4 | Estrutura Java Spring Boot + MySQL em Maputo, com simulações de ambiente offline-first. |
| Fase 3 | Semanas 5–8 | Construção da interface PWA e envio de versões de teste para validação no terreno. |
| Fase 4 | Semanas 9–10 | Testes piloto no Dondo, conduzidos localmente com acompanhamento remoto. |
| Fase 5 | Semanas 11–12 | Lançamento oficial do MVP, focado nas associações do Dondo e compradores da Beira. |

## 8. Orçamento e Recursos Necessários

Os valores desta secção devem ser preenchidos de acordo com o orçamento real disponível — as categorias indicadas cobrem as áreas de custo típicas de um projecto deste tipo.

| Categoria | Descrição | Valor Estimado (MZN) |
|---|---|---|
| Recursos Humanos | Equipa de desenvolvimento (backend, frontend, QA). | [a preencher] |
| Infraestrutura Tecnológica | Hosting, domínio, certificado SSL e base de dados. | [a preencher] |
| Deslocações e Validação no Terreno | Viagens Maputo–Dondo e ajudas de custo do ponto focal. | [a preencher] |
| Equipamento | Dispositivos móveis para testes (gama baixa/média). | [a preencher] |
| Comunicação | Dados móveis para testes de rede (3G/2G). | [a preencher] |
| Contingência | Reserva para imprevistos (cerca de 10%). | [a preencher] |
| **Total** | | **[a preencher]** |

## 9. Análise de Riscos e Mitigação

| Risco | Impacto | Estratégia de Mitigação |
|---|---|---|
| Baixa literacia digital dos produtores e gestores de associações. | Alto | Interface simplificada, formação prática presencial via ponto focal, ícones e fluxos visuais intuitivos. |
| Dependência de um único ponto focal no terreno. | Médio | Identificar pelo menos um ponto focal alternativo por associação e documentar todos os processos. |
| Conectividade instável nas zonas rurais do Dondo. | Alto | Arquitetura offline-first já prevista (IndexedDB, sincronização automática) e testes com redes simuladas. |
| Resistência dos intermediários informais ("mapezas"). | Médio | Posicionar a plataforma como complemento, não substituição imediata; envolver alguns intermediários como utilizadores. |
| Baixa adoção inicial por compradores da Beira. | Médio | Estabelecer parcerias-piloto com 2 a 3 compradores âncora antes do lançamento oficial. |
| Atrasos por ausência de presença física constante no Dondo. | Médio | Validação incremental agendada e uso de compilações PWA testáveis remotamente. |
| Conflitos de dados na sincronização offline. | Alto | Testes extensivos de sincronização e regras claras de resolução de conflitos. |

## 10. Indicadores de Sucesso (KPIs)

A partir do lançamento do MVP, sugere-se acompanhar os seguintes indicadores:

- Número de associações agrícolas ativas registadas na plataforma.
- Número de produtores/membros registados por associação.
- Número de lotes de produção registados por semana.
- Volume total (kg/toneladas) de produção catalogada no sistema.
- Número de contactos comprador-produtor iniciados (WhatsApp/chamada).
- Taxa de conclusão de negócios (lotes com estado "VENDIDO").
- Taxa de sucesso de sincronização offline (percentagem de registos sincronizados sem erro).
- Tempo médio de carregamento da interface em redes 3G (meta: inferior a 3 segundos, conforme RNF01).
- Número de compradores ativos (hotéis, restaurantes, grossistas) da Beira e do Dondo.

## 11. Conclusão

O AgriTech Dondo responde a um problema estrutural real do setor agrícola no Distrito do Dondo, sem ignorar as limitações de conectividade e de literacia digital que existem no contexto rural moçambicano. A opção por uma abordagem offline-first, combinada com uma estratégia de desenvolvimento remoto e validação incremental no terreno, torna possível construir uma solução tecnicamente sólida sem exigir presença física constante da equipa de desenvolvimento.

O plano de execução está faseado ao longo de 12 semanas e aponta para a entrega de um MVP funcional que ligue as associações produtoras do Dondo diretamente aos compradores da Beira. Isso reduz a dependência de intermediários informais e começa a construir uma base histórica de dados de produção — algo que poderá sustentar decisões futuras de investimento e de política agrícola local.

## 12. Referências

Esta secção deve reunir as fontes efetivamente consultadas na elaboração deste documento — por exemplo, dados oficiais sobre o Distrito do Dondo, estudos sobre o Corredor da Beira, ou documentação técnica das tecnologias usadas (Spring Boot, MySQL, PWA). O texto original tinha marcações de citação ("[cite: 3]") sem fonte identificável, por isso foram removidas nesta versão. Recomenda-se substituir este espaço pelas referências reais.

## 13. Anexos

Espaço reservado para materiais de apoio adicionais, tais como:

- Mockups ou protótipos da interface do utilizador.
- Diagrama entidade-relacionamento da base de dados.
- Diagrama da arquitetura em camadas.
- Atas de reuniões com as associações do Dondo.
- Resultados e observações dos testes piloto.
