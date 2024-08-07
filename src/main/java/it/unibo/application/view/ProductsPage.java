package it.unibo.application.view;

import it.unibo.application.controller.Controller;
import it.unibo.application.data.entities.components.BaseInfo;
import it.unibo.application.data.entities.components.Component;
import it.unibo.application.data.entities.enums.Specs;
import it.unibo.application.data.entities.price.ComponentPrice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;

public class ProductsPage extends JPanel {

    private final Controller controller;

    public ProductsPage(final Controller controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.add(new TopBar(controller), BorderLayout.NORTH);

        final List<Component> components = controller.getComponents(controller.getDesiredPart());

        if (components.isEmpty()) {
            this.add(new JLabel("No components available"), BorderLayout.CENTER);
            return;
        }

        final List<String> baseInfoColumns = List.of("ID", "Name", "Manufacturer", "Launch Year", "MSRP");

        final Map<Specs, String> firstComponentSpecs = components.get(0).getSpecificAttributes();
        final List<String> columnNames = new ArrayList<>(baseInfoColumns);
        for (final Specs spec : firstComponentSpecs.keySet()) {
            columnNames.add(spec.getFieldName());
        }

        final DefaultTableModel tableModel = new DefaultTableModel(columnNames.toArray(), 0) {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };

        for (final Component component : components) {
            final BaseInfo baseInfo = component.getBaseInfo();
            final Map<Specs, String> specs = component.getSpecificAttributes();

            final List<String> rowData = new ArrayList<>();
            rowData.add(String.valueOf(baseInfo.getId()));
            rowData.add(baseInfo.getName());
            rowData.add(baseInfo.getManufacturer());
            rowData.add(String.valueOf(baseInfo.getLaunchYear()));
            rowData.add(String.format("%.2f €", baseInfo.getMsrp()));

            for (final Specs spec : firstComponentSpecs.keySet()) {
                final String value = specs.getOrDefault(spec, "N/A");
                final String suffix = spec.getSuffix();
                rowData.add(value + (suffix != null ? " " + suffix : ""));
            }
            tableModel.addRow(rowData.toArray());
        }

        final JTable table = new JTable(tableModel);
        final JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        final Component component = components.get(selectedRow);
                        showComponentDetails(component);
                    }
                }
            }
        });
    }

    private void showComponentDetails(final Component component) {
        final BaseInfo baseInfo = component.getBaseInfo();

        final List<ComponentPrice> amazonPrices = controller.getRecentComponentPricesByReseller("Amazon", baseInfo.getId());
        final List<ComponentPrice> ebayPrices = controller.getRecentComponentPricesByReseller("Ebay", baseInfo.getId());

        if (amazonPrices.isEmpty() && ebayPrices.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No price data available for chart.",
                    "Price History",
                    JOptionPane.PLAIN_MESSAGE
            );
        } else {
            final JFreeChart chart = createChart(amazonPrices, ebayPrices);
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 600));
    
            JOptionPane.showMessageDialog(
                    this,
                    chartPanel,
                    "Price History",
                    JOptionPane.PLAIN_MESSAGE
            );
        }
    }

    private JFreeChart createChart(final List<ComponentPrice> amazonPrices, final List<ComponentPrice> ebayPrices) {
        final XYSeries amazonSeries = new XYSeries("Amazon", false, false);
        final XYSeries ebaySeries = new XYSeries("eBay", false, false);
    
        for (final ComponentPrice price : amazonPrices) {
            final LocalDate date = price.getScrapeDate();
            final long millis = date.atStartOfDay().toInstant(ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay())).toEpochMilli();
            amazonSeries.add(millis, price.getComponentPrice());
        }
    
        for (final ComponentPrice price : ebayPrices) {
            final LocalDate date = price.getScrapeDate();
            final long millis = date.atStartOfDay().toInstant(ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay())).toEpochMilli();
            ebaySeries.add(millis, price.getComponentPrice());
        }
    
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(amazonSeries);
        dataset.addSeries(ebaySeries);
    
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Date",
                "Price (€)",
                dataset
        );
    
        final XYPlot plot = (XYPlot) chart.getPlot();
        final DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat("yyyy-MM-dd")));
        plot.setDomainAxis(dateAxis);
    
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        plot.setRenderer(renderer);
    
        return chart;
    }
}