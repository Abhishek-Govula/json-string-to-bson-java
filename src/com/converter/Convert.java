package com.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;

/**
 * Provides the functionality to convert JSON Object in the form of a string to
 * a BasicDBObject which is used by the MongoDB. Using this converter, the query
 * written for MongoDB can be converted into BasicDBObject and used directly as
 * a java object to query the MongoDB and fetch the results
 * 
 * @author abhishek520.govula@gmail.com
 *
 */
public class Convert {
	/**
	 * JsonParser used to parse the raw string into an object which can be
	 * processed by the GSON library
	 */
	private static final JsonParser JSON_PARSER = new JsonParser();

	/**
	 * Returns the {@link BasicDBObject} which can be used for MongoDB query
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static BasicDBObject createBsonObj(String jsonStr) {
		return createBsonObj(JSON_PARSER.parse(jsonStr).getAsJsonObject());
	}

	/**
	 * This will use the JsonObject of the GSON library as an argument and
	 * return the {@link BasicDBObject}
	 * 
	 * @param obj
	 * @return
	 */
	private static BasicDBObject createBsonObj(JsonObject obj) {
		BasicDBObject dbObject = new BasicDBObject();
		for (Entry<String, JsonElement> e : obj.entrySet()) {
			String key = e.getKey();
			JsonElement val = e.getValue();

			// Value can be null or JsonObject or JsonArray or JsonPrimitive
			// Json primitives will contain the actual values, whereas the JSON
			// Object and the Json Array will contain the non primitive values
			if (!val.isJsonObject()) {
				if (val.isJsonPrimitive()) {
					dbObject.append(key, getPrimitive(val));
				} else {
					// Here the object will be either a json null or a json
					// array

					// If the value is JSON null, then we will directly insert
					// the null value into the BasicDBObject
					if (val.isJsonNull()) {
						dbObject.append(key, null);
					}
					// If the value is JSON Array, then we will iterate and
					// insert the value
					if (val.isJsonArray()) {
						List<Object> al = getListOfElems(val.getAsJsonArray());
						dbObject.append(key, new BasicDBObject("$in", al));
					}
				}
			} else {
				if (val.isJsonObject()) {
					dbObject.append(key, createBsonObj(val.getAsJsonObject()));
				}
			}
		}
		return dbObject;
	}

	/**
	 * Returns the JsonArray as a List which can be inserted into the
	 * BasicDBObject
	 * 
	 * @param jsonArray
	 * @return
	 */
	private static List<Object> getListOfElems(JsonArray jsonArray) {
		List<Object> al = new ArrayList<>();
		for (JsonElement e : jsonArray) {
			if (e.isJsonObject()) {
				al.add(createBsonObj(e.getAsJsonObject()));
			} else if (e.isJsonPrimitive()) {
				al.add(getPrimitive(e));
			} else if (e.isJsonNull()) {
				al.add(null);
			} else {
				al.add(new BasicDBObject("$in", getListOfElems(e.getAsJsonArray())));
			}
		}
		return al;
	}

	/**
	 * Handles the primitive values. Will return the primitive values as a
	 * String or Double or Long
	 * 
	 * @param e
	 * @return
	 */
	private static Object getPrimitive(JsonElement e) {
		try {
			Double _doubleValue = Double.parseDouble(e.getAsString().trim());
			// No number format exception. Hence the value is a number
			// Now we will check if the number is a floating point or an non
			// floating point number
			// Now we will try to parse the number into a Long. If it parses
			// into a long, then the number is a long and we will return the
			// long. Else we will return the double value
			try {
				Long _longValue = Long.parseLong(e.getAsString().trim());
				return _longValue;
			} catch (NumberFormatException ex) {
				// The number can e formatted into a long. Hence it is a double.
				return _doubleValue;
			}
			/*
			// Now checking if the string value and the return value are the same.
			// If they are the same, then the input data is a number in string format
			// and we will send the response as a string and not a number
			if(e.getAsString().trim().equals(returnVal.toString())){
				//This mean the string and number value are same and then numberic value has been intentionally been sent as string. Hence we will return thr string
				return e.getAsString().trim();
			}*/
		} catch (NumberFormatException ex) {
			// Value is a string
			//If value is a string, there is no more check required, hence we will return it
			return e.getAsString().trim();
		}
	}
}
