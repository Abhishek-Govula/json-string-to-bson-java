#JSON string to Bson converter

###Summary
This is a Conveter library written for Java and MongoDB.
MongoShell queries with a JSON syntax. While trying to do the same using Java library, you will have to create a new DBObject and query the collection. When there and nested objects involved this task is prone to errors and confusion.
To reduce the confusion and to ease the conversion of the JSON Object syntax into a way in which Java will be able to query (BasicDBObject of MongoDB), this converter was made.
	
###Use

	Converter.createBsonObj(String jsonStr)
	This will take a JSON object in the form of string and will convert it into BSON Object.

###Libraries
Has a dependency of 
* Google GSON
* MongoDB java driver

###Sample
You can refer to the [sample here](src/com/converter/main/Test.java)