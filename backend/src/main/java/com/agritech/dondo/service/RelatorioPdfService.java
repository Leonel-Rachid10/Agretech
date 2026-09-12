package com.agritech.dondo.service;

import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.model.LoteProducao;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioPdfService {

    private final LoteProducaoRepository loteProducaoRepository;

    public RelatorioPdfService(LoteProducaoRepository loteProducaoRepository) {
        this.loteProducaoRepository = loteProducaoRepository;
    }

    public byte[] gerarRelatorioProducaoPdf() {
        List<LoteProducao> lotes = loteProducaoRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fontes
            Font tituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(46, 125, 50));
            Font subTituloFonte = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);
            Font cabecalhoTabela = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font textoTabela = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font kpiTituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.GRAY);
            Font kpiValorFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(33, 33, 33));

            // Título e Cabeçalho
            Paragraph titulo = new Paragraph("AgriTech Dondo — Relatório Consolidado de Produção Agrícola", tituloFonte);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Distrito do Dondo, Província de Sofala — Emissão: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), subTituloFonte);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(15);
            document.add(subtitulo);

            // Cálculos de KPIs
            int totalLotes = lotes.size();
            BigDecimal volumeTotalKg = lotes.stream()
                    .map(LoteProducao::getQuantidadeEstimada)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long prontosColheita = lotes.stream()
                    .filter(l -> l.getEstado() == EstadoLote.PRONTO_PARA_COLHEITA)
                    .count();

            long emCrescimento = lotes.stream()
                    .filter(l -> l.getEstado() == EstadoLote.EM_CRESCIMENTO)
                    .count();

            // Tabela de KPIs
            PdfPTable kpiTable = new PdfPTable(4);
            kpiTable.setWidthPercentage(100);
            kpiTable.setSpacingAfter(15);

            adicionarKpiCell(kpiTable, "TOTAL DE LOTES", String.valueOf(totalLotes), kpiTituloFonte, kpiValorFonte, new Color(232, 245, 233));
            adicionarKpiCell(kpiTable, "VOLUME TOTAL (KG)", volumeTotalKg.toPlainString() + " KG", kpiTituloFonte, kpiValorFonte, new Color(227, 242, 253));
            adicionarKpiCell(kpiTable, "PRONTOS PARA COLHEITA", String.valueOf(prontosColheita), kpiTituloFonte, kpiValorFonte, new Color(255, 243, 224));
            adicionarKpiCell(kpiTable, "EM CRESCIMENTO", String.valueOf(emCrescimento), kpiTituloFonte, kpiValorFonte, new Color(243, 229, 245));

            document.add(kpiTable);

            // Tabela de Dados
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 1.2f, 1.8f, 2.0f, 1.3f, 1.3f, 1.2f, 1.5f});

            String[] headers = {"Cultura", "Categoria", "Produtor", "Associação / Local", "Qtd. Estimada", "Colheita Prevista", "Preço (MZN)", "Estado"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, cabecalhoTabela));
                cell.setBackgroundColor(new Color(46, 125, 50));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            boolean zebrado = false;
            for (LoteProducao lote : lotes) {
                Color rowBg = zebrado ? new Color(245, 245, 245) : Color.WHITE;
                zebrado = !zebrado;

                adicionarLinha(table, lote.getCultura().getNome(), textoTabela, rowBg, Element.ALIGN_LEFT);
                adicionarLinha(table, lote.getCultura().getCategoria(), textoTabela, rowBg, Element.ALIGN_LEFT);
                adicionarLinha(table, lote.getProdutor().getNome(), textoTabela, rowBg, Element.ALIGN_LEFT);

                String local = lote.getProdutor().getAssociacao() != null
                        ? lote.getProdutor().getAssociacao().getNome() + " (" + lote.getProdutor().getAssociacao().getLocalidade() + ")"
                        : "N/D";
                adicionarLinha(table, local, textoTabela, rowBg, Element.ALIGN_LEFT);

                adicionarLinha(table, lote.getQuantidadeEstimada() + " " + lote.getCultura().getUnidadeMedida(), textoTabela, rowBg, Element.ALIGN_RIGHT);
                adicionarLinha(table, lote.getDataColheitaPrevista().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), textoTabela, rowBg, Element.ALIGN_CENTER);

                String preco = lote.getPrecoPorUnidade() != null ? lote.getPrecoPorUnidade().toPlainString() + " MT" : "-";
                adicionarLinha(table, preco, textoTabela, rowBg, Element.ALIGN_RIGHT);

                adicionarLinha(table, lote.getEstado().name(), textoTabela, rowBg, Element.ALIGN_CENTER);
            }

            document.add(table);

            // Rodapé
            Paragraph rodape = new Paragraph("\nDocumento gerado pelo sistema AgriTech Dondo — Apoio ao Corredor da Beira e Produtores do Dondo", subTituloFonte);
            rodape.setAlignment(Element.ALIGN_RIGHT);
            document.add(rodape);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório PDF: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    private void adicionarKpiCell(PdfPTable table, String titulo, String valor, Font fTitulo, Font fValor, Color bg) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setPadding(8);
        cell.setBorderWidth(1);
        cell.setBorderColor(new Color(220, 220, 220));

        Paragraph p1 = new Paragraph(titulo, fTitulo);
        p1.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph(valor, fValor);
        p2.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(p1);
        cell.addElement(p2);
        table.addCell(cell);
    }

    private void adicionarLinha(PdfPTable table, String texto, Font font, Color bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "-", font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
