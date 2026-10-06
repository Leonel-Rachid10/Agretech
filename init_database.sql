CREATE DATABASE IF NOT EXISTS agritech_dondo_db;
USE agritech_dondo_db;

CREATE TABLE IF NOT EXISTS cultura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(100) NOT NULL,
    unidade_medida VARCHAR(20) DEFAULT 'KG'
);

TRUNCATE TABLE cultura;

INSERT INTO cultura (nome, categoria, unidade_medida) VALUES
('Arroz', 'Cereais', 'KG'),
('Mapira (Sorgo)', 'Cereais', 'KG'),
('Mexoeira', 'Cereais', 'KG'),
('Milho', 'Cereais', 'KG'),
('Algodao', 'Culturas de Rendimento', 'KG'),
('Cana-de-acucar', 'Culturas de Rendimento', 'TON'),
('Castanha de Caju', 'Culturas de Rendimento', 'KG'),
('Gergelim (Sesamo)', 'Culturas de Rendimento', 'KG'),
('Tabaco', 'Culturas de Rendimento', 'KG'),
('Ananas', 'Frutas e Arvores de Fruto', 'UN'),
('Banana', 'Frutas e Arvores de Fruto', 'CACHO'),
('Citrinos (Laranja, Limao, Tangerina)', 'Frutas e Arvores de Fruto', 'KG'),
('Coco', 'Frutas e Arvores de Fruto', 'UN'),
('Manga', 'Frutas e Arvores de Fruto', 'KG'),
('Alface', 'Horticolas', 'UN'),
('Alho', 'Horticolas', 'KG'),
('Cebola', 'Horticolas', 'KG'),
('Couve', 'Horticolas', 'MOLHO'),
('Quiabo', 'Horticolas', 'KG'),
('Repolho', 'Horticolas', 'UN'),
('Tomate', 'Horticolas', 'CAIXA'),
('Amendoim', 'Leguminosas e Oleaginosas', 'KG'),
('Feijao-boer', 'Leguminosas e Oleaginosas', 'KG'),
('Feijao-holandes', 'Leguminosas e Oleaginosas', 'KG'),
('Feijao-jugo', 'Leguminosas e Oleaginosas', 'KG'),
('Feijao-manteiga', 'Leguminosas e Oleaginosas', 'KG'),
('Feijao-nhemba', 'Leguminosas e Oleaginosas', 'KG'),
('Feijao-soroco', 'Leguminosas e Oleaginosas', 'KG'),
('Soja', 'Leguminosas e Oleaginosas', 'KG'),
('Batata-doce', 'Raizes e Tuberculos', 'KG'),
('Mandioca', 'Raizes e Tuberculos', 'KG');
