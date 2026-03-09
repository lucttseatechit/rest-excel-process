package com.example.demo.service;

import com.example.demo.dto.RowData;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SheetHandler extends DefaultHandler {

    private final SharedStringsTable sst;
    private final BatchDispatcher dispatcher;

    private boolean nextIsString;

    private String lastValue;

    private int rowIndex = -1;

    private String name;
    private Integer age;

    public SheetHandler(SharedStringsTable sst, BatchDispatcher dispatcher) {
        this.sst = sst;
        this.dispatcher = dispatcher;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("row".equals(qName)) {
            rowIndex++;
        }

        if ("c".equals(qName)) {

            String type = attributes.getValue("t");

            nextIsString = "s".equals(type);
        }

        lastValue = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) {

        lastValue += new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        if (nextIsString) {
            int idx = Integer.parseInt(lastValue);
            lastValue = sst.getItemAt(idx).getString();
            nextIsString = false;
        }

        if ("c".equals(qName)) {

            if (name == null) {
                name = lastValue;
            } else {
                age = Integer.parseInt(lastValue);
            }
        }

        if ("row".equals(qName)) {

            if (rowIndex == 0) {
                return; // skip header
            }

            RowData data = new RowData(name, age);

            dispatcher.addRow(data);

            name = null;
            age = null;
        }
    }
}
