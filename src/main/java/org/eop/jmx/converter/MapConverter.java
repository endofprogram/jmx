package org.eop.jmx.converter;

import java.util.List;
import java.util.Map;

import org.eop.jmx.builder.XmlBuilder;
import org.eop.jmx.converter.exception.ConvertException;
import org.eop.jmx.converter.setting.ActionWhenNullValue;
import org.eop.jmx.converter.setting.ConvertSetting;
import org.eop.jmx.converter.setting.ValueListToXmlStrategy;

/**
 * lixinjie
 */
public class MapConverter {

	public static String toXml(Map<String, Object> map, ConvertSetting convertSetting) {
		return toXml(map, convertSetting, false);
	}
	
	public static String toXml(Map<String, Object> map, ConvertSetting convertSetting, boolean format) {
		XmlBuilder xmlBuilder = new XmlBuilder();
		xmlBuilder.rootElement(getMapKey(convertSetting.getSetting("map.root.src.key"), "", convertSetting));
		convertMap(map, nextLevelPrefix("", convertSetting.getSetting("map.root.src.key")), convertSetting, xmlBuilder);
		xmlBuilder.end();
		return xmlBuilder.toXml(format);
	}
	
	@SuppressWarnings("unchecked")
	protected static void convertMap(Map<String, Object> map, String prefix, ConvertSetting convertSetting, XmlBuilder xmlBuilder) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() == null) {
				convertValue(entry.getKey(), null, prefix, convertSetting, xmlBuilder);
			} else if (entry.getValue() instanceof Map<?, ?>) {
				xmlBuilder.element(getMapKey(entry.getKey(), prefix, convertSetting));
				convertMap((Map<String, Object>)entry.getValue(), nextLevelPrefix(prefix, entry.getKey()), convertSetting, xmlBuilder);
				xmlBuilder.end();
			} else if (entry.getValue() instanceof List<?>) {
				xmlBuilder.element(getMapKey(entry.getKey(), prefix, convertSetting));
				convertList((List<Object>)entry.getValue(), nextLevelPrefix(prefix, entry.getKey()), convertSetting, xmlBuilder);
				xmlBuilder.end();
			} else {
				xmlBuilder.element(getMapKey(entry.getKey(), prefix, convertSetting));
				convertValue(entry.getKey(), entry.getValue(), prefix, convertSetting, xmlBuilder);
				xmlBuilder.end();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void convertList(List<Object> list, String prefix, ConvertSetting convertSetting, XmlBuilder xmlBuilder) {
		if (!list.isEmpty()) {
			if (list.get(0) instanceof Map<?, ?>) {
				for (Map<String, Object> map : (List<Map<String, Object>>)(Object)list) {
					xmlBuilder.element(getMapKey(convertSetting.getSetting("list.item.src.key"), prefix, convertSetting));
					convertMap(map, nextLevelPrefix(prefix, convertSetting.getSetting("list.item.src.key")), convertSetting, xmlBuilder);
					xmlBuilder.end();
				}
			} else if (list.get(0) instanceof List<?>) {
				for (List<Object> ilist : (List<List<Object>>)(Object)list) {
					xmlBuilder.element(getMapKey(convertSetting.getSetting("list.item.src.key"), prefix, convertSetting));
					convertList(ilist, nextLevelPrefix(prefix, convertSetting.getSetting("list.item.src.key")), convertSetting, xmlBuilder);
					xmlBuilder.end();
				}
			} else {
				if (convertSetting.getValueListToXmlStrategy() == ValueListToXmlStrategy.MultipleChildrenWithSameName) {
					for (Object value : list) {
						xmlBuilder.element(getMapKey(convertSetting.getSetting("list.item.src.key"), prefix, convertSetting));
						convertValue(convertSetting.getSetting("list.item.src.key"), value, prefix, convertSetting, xmlBuilder);
						xmlBuilder.end();
					}
				} else if (convertSetting.getValueListToXmlStrategy() == ValueListToXmlStrategy.SingleChildWithSplittedText) {
					convertValue(convertSetting.getSetting("list.item.src.key"), listToString(list, convertSetting.getSetting("xml.list.split.char")), prefix, convertSetting, xmlBuilder);
				}
			}
		}
	}
	
	protected static void convertValue(String key, Object value, String prefix, ConvertSetting convertSetting, XmlBuilder xmlBuilder) {
		if (value == null) {
			if (convertSetting.getActionWhenNullValue() == ActionWhenNullValue.UseNullString) {
				xmlBuilder.text(convertSetting.getSetting("null.string"));
			} else if (convertSetting.getActionWhenNullValue() == ActionWhenNullValue.ThrowException) {
				throw new ConvertException("unexpected null value with name '" + prefix + key + "', when convert map to xml");
			} else if (convertSetting.getActionWhenNullValue() == ActionWhenNullValue.ExcludeSilently) {
				//do nothing
			}
		} else {
			xmlBuilder.text(value);
		}
	}
	
	protected static String getMapKey(String key, String prefix, ConvertSetting convertSetting) {
		return convertSetting.getKeyMapper().getMapKey(prefix + key);
	}
	
	protected static String nextLevelPrefix(String prefix, String key) {
		return prefix + key + ".";
	}
	
	protected static String listToString(List<Object> list, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (Object value : list) {
			sb.append(value).append(delimiter);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
}
