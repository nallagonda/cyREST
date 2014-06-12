package org.cytoscape.rest.internal.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class CyTableSerializer {

	public final String serializeTables(final Collection<CyTable> tables) throws IOException {

		final JsonFactory factory = new JsonFactory();

		String result = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonGenerator generator = null;
		generator = factory.createGenerator(stream);
		generator.writeStartArray();
		for(CyTable table:tables) {
			generator.writeStartObject();
			generator.writeNumberField(CyIdentifiable.SUID, table.getSUID());
			generator.writeStringField("title", table.getTitle());
			generator.writeBooleanField("public", table.isPublic());
			generator.writeStringField("mutable", table.getMutability().name());
			generator.writeEndObject();
		}
		generator.writeEndArray();
		generator.close();
		result = stream.toString();
		stream.close();
		return result;
	}

	public String toCSV(final CyTable table) throws Exception {

		final StringBuilder builder = new StringBuilder();

		final Collection<CyColumn> columns = table.getColumns();
		final int colLength = columns.size();

		// Rows to be serialized
		final List<CyRow> rows = table.getAllRows();
		int idx = 1;

		// Add header
		for (final CyColumn column : columns) {
			builder.append(column.getName());
			if (idx == colLength) {
				builder.append("\n");
			} else {
				builder.append(",");
			}
			idx++;
		}

		for (final CyRow row : rows) {
			idx = 1;
			for (final CyColumn column : columns) {
				final Class<?> type = column.getType();
				if (type != List.class) {
					final Object value = row.get(column.getName(), type);
					if (value != null) {
						builder.append(value.toString());
					}
				} else {
					final List<?> listValue = row.getList(column.getName(), column.getListElementType());
					builder.append(listToString(listValue));
				}
				if (idx == colLength) {
					builder.append("\n");
				} else {
					builder.append(",");
				}
				idx++;
			}
		}
		return builder.toString();
	}

	private final String listToString(final List<?> listCell) {
		if (listCell == null) {
			return "";
		}

		final StringBuilder listBuilder = new StringBuilder();

		for (final Object item : listCell) {
			if (item != null) {
				listBuilder.append(item.toString());
				listBuilder.append("|");
			}
		}
		final String cellString = listBuilder.toString();
		if (cellString != null && cellString.isEmpty() == false) {
			return cellString.substring(0, cellString.length() - 1);
		} else {
			return "";
		}
	}
}