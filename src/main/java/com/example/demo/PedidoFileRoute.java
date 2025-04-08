package com.example.demo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class PedidoFileRoute extends RouteBuilder {

    private static final String EXPECTED_HEADER = "id_cliente,nombre_cliente,tipo_cliente,monto_compra";

    @Override
    public void configure() {
        from("file:input?noop=true&include=.*\\.csv")
                .process(this::validateFileHeader)
                .choice()
                    .when(exchange -> exchange.getProperty("validHeader", Boolean.class))
                        .process(this::validateTypeClient)
                        .choice()
                            .when(exchange -> "empty".equals(exchange.getProperty("clientType")))
                                .process(exchange -> {
                                    String fileName = (String) exchange.getIn().getHeader("CamelFileName");
                                    System.out.println("Archivo vacío: " + fileName);
                                })
                                .to("file:error/empty")
                            .otherwise()
                                .process(exchange -> {
                                    String clientType = exchange.getProperty("clientType", String.class);
                                    String fileName = (String) exchange.getIn().getHeader("CamelFileName");
                                    System.out.println("Archivo válido - Tipo cliente: " + clientType + " (" + fileName + ")");
                                })
                                .toD("file:output/${exchangeProperty.clientType}/") // Dynamic destination
                        .endChoice()
                    .otherwise()
                        .process(exchange -> {
                            String fileName = (String) exchange.getIn().getHeader("CamelFileName");
                            System.out.println("Archivo inválido (sin encabezado): " + fileName);
                        })
                        .to("file:error/invalid") // Invalid header files
                .end();
    }

    private void validateFileHeader(Exchange exchange) {
        GenericFile<?> genericFile = (GenericFile<?>) exchange.getIn().getBody();
        File file = new File(genericFile.getAbsoluteFilePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            boolean isValid = firstLine != null && firstLine.equals(EXPECTED_HEADER);
            exchange.setProperty("validHeader", isValid);
        } catch (IOException e) {
            exchange.setProperty("validHeader", false);
        }
    }

    private void validateTypeClient(Exchange exchange) {
        GenericFile<?> genericFile = (GenericFile<?>) exchange.getIn().getBody();
        File file = new File(genericFile.getAbsoluteFilePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip header

            String firstDataLine = reader.readLine(); // Read first data row
            if (firstDataLine == null || firstDataLine.trim().isEmpty()) {
                exchange.setProperty("clientType", "empty"); // No data rows
                return;
            }

            String[] columns = firstDataLine.split(",");
            if (columns.length >= 3) {
                String clientType = columns[2].trim(); // Extract client type
                exchange.setProperty("clientType", clientType);
            } else {
                exchange.setProperty("clientType", "unknown"); // Handle unexpected cases
            }

        } catch (IOException e) {
            exchange.setProperty("clientType", "error");
        }
    }
}
