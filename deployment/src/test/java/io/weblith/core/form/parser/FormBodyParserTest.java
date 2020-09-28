package io.weblith.core.form.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.weblith.core.form.Form;
import io.weblith.core.form.parsing.BodyParserObjectMapperProvider;
import io.weblith.core.form.parsing.FormBodyParser;
import io.weblith.core.form.validating.Violation;
import io.weblith.core.i18n.LocaleHandlerImpl;
import io.weblith.core.request.RequestContext;

public class FormBodyParserTest {

    private final Logger LOGGER = Logger.getLogger(FormBodyParserTest.class);

    @Mock
    RequestContext requestContext;

    @Mock
    LocaleHandlerImpl localeHandler;

    private Form<Object> currentForm;

    FormBodyParser bodyParser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(localeHandler.current()).thenReturn(Locale.ENGLISH);
        when(requestContext.locale()).thenReturn(localeHandler);

        currentForm = Form.of(Object.class);
        when(requestContext.get(Form.class)).thenReturn(this.currentForm);

        bodyParser = new FormBodyParser(requestContext, new BodyParserObjectMapperProvider(requestContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapConversion() {

        Map<String, Object> map = new HashMap<>();
        bodyParser.put(map, "myObject.myProperty", Arrays.asList("value1", "value2", "value3"));

        assertThat(map, aMapWithSize(1));
        assertThat(map, hasKey("myObject"));
        Map<String, Object> object = (Map<String, Object>) map.get("myObject");
        assertThat(object, aMapWithSize(1));
        assertThat(object, hasEntry("myProperty", Arrays.asList("value1", "value2", "value3")));

        map.clear();
        bodyParser.put(map, "myObject[].myProperty", Arrays.asList("value1", "value2", "value3"));

        assertThat(map, aMapWithSize(1));
        assertThat(map, hasKey("myObject"));
        List<Map<String, Object>> objects = (List<Map<String, Object>>) map.get("myObject");
        assertThat(objects, iterableWithSize(3));
        for (int i = 0; i < 3; i++) {
            object = (Map<String, Object>) objects.get(i);
            assertThat(object, hasEntry("myProperty", "value" + (i + 1)));
        }

    }

    @Test
    public void testSimpleValueMap() {

        Map<String, Object> form = new HashMap<>();
        form.put("integerPrimitive", "1000");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.integerPrimitive, equalTo(1000));
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testSimpleValue() {

        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("integerPrimitive", "1000");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.integerPrimitive, equalTo(1000));
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testSimpleValuesMap() {

        Map<String, Object> form = new HashMap<>();
        form.put("integerPrimitive", "1000");
        form.put("integerObject", "2000");
        form.put("longPrimitive", "3000");
        form.put("longObject", "4000");
        form.put("floatPrimitive", "1.234");
        form.put("floatObject", "2.345");
        form.put("doublePrimitive", "3.456");
        form.put("doubleObject", "4.567");
        form.put("string", "aString");
        form.put("characterPrimitive", "a");
        form.put("characterObject", "b");
        form.put("localDate", "2020-05-03");
        form.put("localTime", "07:00:30");
        form.put("localDateTime", "2020-05-03T07:08:09");
        form.put("timestamp", "2020-05-03T07:08:09");
        form.put("date", "2020-03-01");

        form.put("somethingElseWhatShouldBeSkipped", "somethingElseWhatShouldBeSkipped");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.integerPrimitive, equalTo(1000));
        assertThat(testObject.integerObject, equalTo(2000));
        assertThat(testObject.longPrimitive, equalTo(3000L));
        assertThat(testObject.longObject, equalTo(4000L));
        assertThat(testObject.floatPrimitive, equalTo(1.234F));
        assertThat(testObject.floatObject, equalTo(2.345F));
        assertThat(testObject.doublePrimitive, equalTo(3.456D));
        assertThat(testObject.doubleObject, equalTo(4.567D));
        assertThat(testObject.characterPrimitive, equalTo('a'));
        assertThat(testObject.characterObject, equalTo('b'));
        assertThat(testObject.localDate, equalTo(LocalDate.of(2020, 5, 3)));
        assertThat(testObject.localTime, equalTo(LocalTime.of(7, 0, 30)));
        assertThat(testObject.localDateTime, equalTo(LocalDateTime.of(2020, 5, 3, 7, 8, 9)));
        assertThat(testObject.timestamp, equalTo(Date.from(LocalDateTime.of(2020, 5, 3, 7, 8, 9).atZone(ZoneId.systemDefault()).toInstant())));
        assertThat(testObject.date, equalTo(Date.from(LocalDate.of(2020, 03, 01).atStartOfDay(ZoneId.systemDefault()).toInstant())));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        assertThat(sdf.format(testObject.timestamp), equalTo("07:08"));

        for (Violation cv : currentForm.getViolations()) {
            LOGGER.info(cv.getDefaultMessage());
        }
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testSimpleValues() {

        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("integerPrimitive", "1000");
        form.add("integerObject", "2000");
        form.add("longPrimitive", "3000");
        form.add("longObject", "4000");
        form.add("floatPrimitive", "1.234");
        form.add("floatObject", "2.345");
        form.add("doublePrimitive", "3.456");
        form.add("doubleObject", "4.567");
        form.add("string", "aString");
        form.add("characterPrimitive", "a");
        form.add("characterObject", "b");
        form.add("localDate", "2020-05-03");
        form.add("localTime", "07:00:30");
        form.add("localDateTime", "2020-05-03T07:08:09");
        form.add("timestamp", "2020-05-03T07:08:09");
        form.add("date", "2020-03-01");

        form.add("somethingElseWhatShouldBeSkipped", "somethingElseWhatShouldBeSkipped");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.integerPrimitive, equalTo(1000));
        assertThat(testObject.integerObject, equalTo(2000));
        assertThat(testObject.longPrimitive, equalTo(3000L));
        assertThat(testObject.longObject, equalTo(4000L));
        assertThat(testObject.floatPrimitive, equalTo(1.234F));
        assertThat(testObject.floatObject, equalTo(2.345F));
        assertThat(testObject.doublePrimitive, equalTo(3.456D));
        assertThat(testObject.doubleObject, equalTo(4.567D));
        assertThat(testObject.characterPrimitive, equalTo('a'));
        assertThat(testObject.characterObject, equalTo('b'));
        assertThat(testObject.localDate, equalTo(LocalDate.of(2020, 5, 3)));
        assertThat(testObject.localTime, equalTo(LocalTime.of(7, 0, 30)));
        assertThat(testObject.localDateTime, equalTo(LocalDateTime.of(2020, 5, 3, 7, 8, 9)));
        assertThat(testObject.timestamp, equalTo(Date.from(LocalDateTime.of(2020, 5, 3, 7, 8, 9).atZone(ZoneId.systemDefault()).toInstant())));
        assertThat(testObject.date, equalTo(Date.from(LocalDate.of(2020, 03, 01).atStartOfDay(ZoneId.systemDefault()).toInstant())));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        assertThat(sdf.format(testObject.timestamp), equalTo("07:08"));

        for (Violation cv : currentForm.getViolations()) {
            LOGGER.info(cv.getDefaultMessage());
        }
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testTimestampValue() throws ParseException {

        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("timestamp", "2020-05-03T07:08:09");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.timestamp, equalTo(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("03/05/2020 07:08:09")));
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testDateValue() throws ParseException {

        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("date", "2020-03-01");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.date, equalTo(new SimpleDateFormat("dd/MM/yyyy").parse("01/03/2020")));
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testLocalizedSimpleValues() {
        when(localeHandler.current()).thenReturn(Locale.FRENCH);

        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("floatPrimitive", "1,234");
        form.add("floatObject", "2,345");
        form.add("localDate", "20/02/2020");
        form.add("localTime", "7:00");
        form.add("date", "01/03/2020");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        assertThat(testObject.floatPrimitive, equalTo(1.234F));
        assertThat(testObject.floatObject, equalTo(2.345F));
        assertThat(testObject.localDate, equalTo(LocalDate.of(2020, 2, 20)));
        assertThat(testObject.localTime, equalTo(LocalTime.of(7, 0, 0)));
        assertThat(testObject.date, equalTo(Date.from(LocalDate.of(2020, 03, 01).atStartOfDay(ZoneId.systemDefault()).toInstant())));

        for (Violation cv : currentForm.getViolations()) {
            LOGGER.info(cv.getDefaultMessage());
        }
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testISO8601Values() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("localDate", "2020-05-01T07:08:09Z");
        form.add("localDateTime", "2020-05-02T07:08:09Z");
        form.add("timestamp", "2020-05-03T06:07:08Z");
        form.add("date", "2020-05-04T07:08:09Z");

        TestObject testObject = bodyParser.parse(TestObject.class, form);

        // Zulu time, so UTC based
        assertThat(testObject.localDate, equalTo(LocalDate.of(2020, 5, 1)));
        assertThat(testObject.localDateTime, equalTo(LocalDateTime.of(2020, 5, 2, 7, 8, 9)));
        assertThat(testObject.timestamp, equalTo(Date.from(LocalDateTime.of(2020, 5, 3, 6, 7, 8).toInstant(ZoneOffset.UTC))));
        assertThat(testObject.date, equalTo(Date.from(LocalDateTime.of(2020, 5, 4, 7, 8, 9).toInstant(ZoneOffset.UTC))));

        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testValidationError() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("integerPrimitive", "a");

        TestObject testObject = bodyParser.parse(TestObject.class, form);
        assertTrue(currentForm.hasViolations());
        assertViolation("integerPrimitive", "validation.is.int.violation");
        assertThat(testObject.integerPrimitive, equalTo(0));
    }

    @Test
    public void testValidationErrors() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("integerPrimitive", "a");
        form.add("integerObject", "b");
        form.add("longPrimitive", "c");
        form.add("longObject", "d");
        form.add("floatPrimitive", "e");
        form.add("floatObject", "f");
        form.add("doublePrimitive", "g");
        form.add("doubleObject", "h");
        form.add("date", "cc");
        form.add("timestamp", "dd");
        form.add("uuid", "ee");
        form.add("somethingElseWhatShouldBeSkipped", "somethingElseWhatShouldBeSkipped");

        TestObject testObject = bodyParser.parse(TestObject.class, form);
        assertTrue(currentForm.hasViolations());
        assertViolation("integerPrimitive", "validation.is.int.violation");
        assertThat(testObject.integerPrimitive, equalTo(0));
        assertViolation("integerObject", "validation.is.integer.violation"); // TODO
        assertNull(testObject.integerObject);
        assertViolation("longPrimitive", "validation.is.long.violation");
        assertThat(testObject.longPrimitive, equalTo(0L));
        assertViolation("longObject", "validation.is.long.violation");
        assertNull(testObject.longObject);
        assertViolation("floatPrimitive", "validation.is.float.violation");
        assertThat(testObject.floatPrimitive, equalTo(0F));
        assertViolation("floatObject", "validation.is.float.violation");
        assertNull(testObject.floatObject);
        assertViolation("doublePrimitive", "validation.is.double.violation");
        assertThat(testObject.doublePrimitive, equalTo(0D));
        assertViolation("doubleObject", "validation.is.double.violation");
        assertNull(testObject.doubleObject);
        assertViolation("date", "validation.is.date.violation");
        assertNull(testObject.date);
        assertViolation("timestamp", "validation.is.date.violation");
        assertNull(testObject.timestamp);
        assertViolation("uuid", "validation.is.uuid.violation");
        assertNull(testObject.uuid);
        assertNull(testObject.string);
        assertThat(testObject.characterPrimitive, equalTo('\0'));
        assertNull(testObject.characterObject);
        assertNull(testObject.string);
    }

    @Test
    public void testValidationErrors2() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("integerPrimitive", "1.2");
        form.add("integerObject", "2,3");
        form.add("date", "20/20/2020");

        TestObject testObject = bodyParser.parse(TestObject.class, form);
        assertTrue(currentForm.hasViolations());
        assertViolation("integerPrimitive", "validation.is.int.violation");
        assertThat(testObject.integerPrimitive, equalTo(0));
        assertViolation("integerObject", "validation.is.integer.violation"); // TODO
        assertNull(testObject.integerObject);
        assertViolation("date", "validation.is.date.violation");
        assertNull(testObject.date);
    }

    @Test
    public void testUnsupportedField() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("string", "aString");
        form.add("notSupportedField", "notSupportedField");
        form.add("longs", "1");
        form.add("longs", "2");

        TestObjectWithUnsupportedField testObject = bodyParser.parse(TestObjectWithUnsupportedField.class, form);
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.notSupportedField, equalTo(null));
        assertThat(testObject.longs, arrayContaining(1L, 2L));
    }

    @Test
    public void testLotOfUnknownFields() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("string", "aString");
        for (int i = 1; i <= 100; i++) {
            form.add("unknwonField" + i, "value");
        }
        form.add("longs", "1");
        form.add("longs", "2");

        TestObjectWithUnsupportedField testObject = bodyParser.parse(TestObjectWithUnsupportedField.class, form);
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.longs, arrayContaining(1L, 2L));
    }

    @Test
    public void testCollectionAndArray() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("string", "only one");
        form.add("integers", "1");
        form.add("integers", "2");
        form.add("strings", "hello");
        form.add("strings", "world");

        TestObjectWithArraysAndCollections testObject = bodyParser.parse(TestObjectWithArraysAndCollections.class, form);
        assertThat(testObject.string, equalTo("only one"));
        assertThat(testObject.integers, arrayContaining(1, 2));
        assertThat(testObject.strings, iterableWithSize(2));
        assertThat(testObject.strings, hasItems("hello", "world"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testEnumerations() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("enum1", MyEnum.VALUE_A.name());
        form.add("enum2", new String("VALUE_B"));

        TestObjectWithEnum testObject = bodyParser.parse(TestObjectWithEnum.class, form);
        assertThat(testObject.enum1, equalTo(MyEnum.VALUE_A));
        assertThat(testObject.enum2, equalTo(MyEnum.VALUE_B));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testEmptyStrings() {

        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("string", "");

        TestObject testObject = bodyParser.parse(TestObject.class, form);
        assertNull(testObject.string);
        assertFalse(currentForm.hasViolations());

        form = new MultivaluedMapImpl<>();
        form.add("string", "   ");

        testObject = bodyParser.parse(TestObject.class, form);
        assertNull(testObject.string);
        assertFalse(currentForm.hasViolations());

    }

    @Test
    public void testInnerObjects() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("object1.integerPrimitive", "1000");
        form.add("object1.integerObject", "2000");
        form.add("object2.integerPrimitive", "3000");
        form.add("object2.integerObject", "4000");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.object1);
        assertThat(testObject.object1.integerPrimitive, equalTo(1000));
        assertThat(testObject.object1.integerObject, equalTo(2000));
        assertNotNull(testObject.object2);
        assertThat(testObject.object2.integerPrimitive, equalTo(3000));
        assertThat(testObject.object2.integerObject, equalTo(4000));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testEmptyInnerObjects() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("object1.integerPrimitive", "");
        form.add("object1.integerObject", "");
        form.add("object2.integerPrimitive", "");
        form.add("object2.integerObject", "");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNull(testObject.object1);
        assertNull(testObject.object2);
        assertNull(testObject.objectWithId);
        assertNull(testObject.objectWithUuid);
        assertFalse(currentForm.hasViolations());

        form = new MultivaluedMapImpl<>();
        form.add("object1", "");
        form.add("object2", "");

        testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNull(testObject.object1);
        assertNull(testObject.object2);
        assertNull(testObject.objectWithId);
        assertNull(testObject.objectWithUuid);
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testObjectWithIdByStringConstructor() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithId", "myId");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithId);
        assertThat(testObject.objectWithId.id, equalTo("myId"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testObjectWithId() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithId.id", "myId");
        form.add("objectWithId.value", "value");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithId);
        assertThat(testObject.objectWithId.id, equalTo("myId"));
        assertThat(testObject.objectWithId.value, equalTo("value"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testObjectWithEmptyId() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithId", "");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNull(testObject.objectWithId);
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testObjectWithUuid() {
        final UUID uuid = UUID.randomUUID();
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithUuid.id", uuid.toString());
        form.add("objectWithUuid.value", "value");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithUuid);
        assertThat(testObject.objectWithUuid.getId(), equalTo(uuid));
        assertThat(testObject.objectWithUuid.value, equalTo("value"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testObjectWithEmptyUuid() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithUuid.id", "");
        form.add("objectWithUuid.value", "");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNull(testObject.objectWithUuid);
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testCollectionOfInnerObjectsByStringConstructor() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithIdList", "objectList1");
        form.add("objectWithIdList", "objectList2");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithIdList);
        assertThat(testObject.objectWithIdList.size(), equalTo(2));
        assertNotNull(testObject.objectWithIdList.get(0));
        assertThat(testObject.objectWithIdList.get(0).id, equalTo("objectList1"));
        assertNotNull(testObject.objectWithIdList.get(1));
        assertThat(testObject.objectWithIdList.get(1).id, equalTo("objectList2"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testArrayOfInnerObjectsByStringConstructor() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithIdArray", "objectArray1");
        form.add("objectWithIdArray", "objectArray2");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithIdArray);
        assertThat(testObject.objectWithIdArray.length, equalTo(2));
        assertNotNull(testObject.objectWithIdArray[0]);
        assertThat(testObject.objectWithIdArray[0].id, equalTo("objectArray1"));
        assertNotNull(testObject.objectWithIdArray[1]);
        assertThat(testObject.objectWithIdArray[1].id, equalTo("objectArray2"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testCollectionInnerObjectsWithString() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithIdList.id", "objectList1");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithIdList);
        assertThat(testObject.objectWithIdList.size(), equalTo(1));
        assertNotNull(testObject.objectWithIdList.get(0));
        assertThat(testObject.objectWithIdList.get(0).id, equalTo("objectList1"));
        assertFalse(currentForm.hasViolations());

        form = new MultivaluedMapImpl<>();
        form.add("objectWithIdList[].id", "objectList1");
        form.add("objectWithIdList[].value", "value1");
        form.add("objectWithIdList[].id", "objectList2");
        form.add("objectWithIdList[].value", "value2");

        testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithIdList);
        assertThat(testObject.objectWithIdList.size(), equalTo(2));
        assertNotNull(testObject.objectWithIdList.get(0));
        assertThat(testObject.objectWithIdList.get(0).id, equalTo("objectList1"));
        assertThat(testObject.objectWithIdList.get(0).value, equalTo("value1"));
        assertNotNull(testObject.objectWithIdList.get(1));
        assertThat(testObject.objectWithIdList.get(1).id, equalTo("objectList2"));
        assertThat(testObject.objectWithIdList.get(1).value, equalTo("value2"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testArrayOfInnerObjectsWithString() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("objectWithIdArray[].id", "objectArray1");
        form.add("objectWithIdArray[].value", "value1");
        form.add("objectWithIdArray[].id", "objectArray2");
        form.add("objectWithIdArray[].value", "value2");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithIdArray);
        assertThat(testObject.objectWithIdArray.length, equalTo(2));
        assertNotNull(testObject.objectWithIdArray[0]);
        assertThat(testObject.objectWithIdArray[0].id, equalTo("objectArray1"));
        assertThat(testObject.objectWithIdArray[0].value, equalTo("value1"));
        assertNotNull(testObject.objectWithIdArray[1]);
        assertThat(testObject.objectWithIdArray[1].id, equalTo("objectArray2"));
        assertThat(testObject.objectWithIdArray[1].value, equalTo("value2"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testCollectionOfInnerObjectsWithId() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        form.add("objectWithUuidList[].id", id1.toString());
        form.add("objectWithUuidList[].value", "value1");
        form.add("objectWithUuidList[].id", id2.toString());
        form.add("objectWithUuidList[].value", "value2");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithUuidList);
        assertThat(testObject.objectWithUuidList.size(), equalTo(2));
        assertNotNull(testObject.objectWithUuidList.get(0));
        assertThat(testObject.objectWithUuidList.get(0).getId(), equalTo(id1));
        assertThat(testObject.objectWithUuidList.get(0).value, equalTo("value1"));
        assertNotNull(testObject.objectWithUuidList.get(1));
        assertThat(testObject.objectWithUuidList.get(1).getId(), equalTo(id2));
        assertThat(testObject.objectWithUuidList.get(1).value, equalTo("value2"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testArrayOfInnerObjectsWithId() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        form.add("objectWithUuidArray[].id", id1.toString());
        form.add("objectWithUuidArray[].value", "value1");
        form.add("objectWithUuidArray[].id", id2.toString());
        form.add("objectWithUuidArray[].value", "value2");

        TestObjectWithInnerObjects testObject = bodyParser.parse(TestObjectWithInnerObjects.class, form);
        assertNotNull(testObject);
        assertNotNull(testObject.objectWithUuidArray);
        assertThat(testObject.objectWithUuidArray.length, equalTo(2));
        assertNotNull(testObject.objectWithUuidArray[0]);
        assertThat(testObject.objectWithUuidArray[0].getId(), equalTo(id1));
        assertThat(testObject.objectWithUuidArray[0].value, equalTo("value1"));
        assertNotNull(testObject.objectWithUuidArray[1]);
        assertThat(testObject.objectWithUuidArray[1].getId(), equalTo(id2));
        assertThat(testObject.objectWithUuidArray[1].value, equalTo("value2"));
        assertFalse(currentForm.hasViolations());
    }

    @Test
    public void testObjectWithWrongUuidValue() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("id", "1234");

        TestObjectWithUuid testObject = bodyParser.parse(TestObjectWithUuid.class, form);
        assertNotNull(testObject);
        assertTrue(currentForm.hasViolations());
        assertViolation("id", "validation.is.uuid.violation");
    }

    @Test
    public void testObjectWithWrongUuidKey() {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl<>();
        form.add("id.id", "1234");

        TestObjectWithUuid testObject = bodyParser.parse(TestObjectWithUuid.class, form);
        assertNotNull(testObject);
        assertTrue(currentForm.hasViolations());
        // assertViolation("id", "validation.is.uuid.violation");
    }

    private <T> void assertViolation(String fieldName, String violationMessage) {
        assertTrue(currentForm.hasViolation(fieldName));
        assertFalse(currentForm.getViolations().isEmpty());
        assertThat(currentForm.getViolations(fieldName).size(), equalTo(1));
        Violation violation = currentForm.getViolations(fieldName).get(0);
        assertNotNull(violation);
        assertThat(violation.getFieldKey(), equalTo(fieldName));
        assertThat(violation.getMessageKey(), equalTo(violationMessage));
    }

    public static class TestObject {

        public int integerPrimitive;
        public Integer integerObject;
        public long longPrimitive;
        public Long longObject;
        public float floatPrimitive;
        public Float floatObject;
        public double doublePrimitive;
        public Double doubleObject;
        public String string;
        public String emptyString;
        public char characterPrimitive;
        public Character characterObject;
        public Date date;
        public Date timestamp;
        public LocalDate localDate;
        public LocalTime localTime;
        public LocalDateTime localDateTime;
        public UUID uuid;
        @NotNull
        public Object requiredObject;

    }

    public static class TestObjectWithUnsupportedField {

        @JsonIgnore
        public StringBuffer notSupportedField;
        public String string;
        public Long[] longs;

    }

    public static class TestObjectWithArraysAndCollections {

        public String string;
        public Integer[] integers;
        public List<String> strings;

    }

    public static class TestObjectWithId {

        public String id;

        public String value;

        public TestObjectWithId() {}

        public TestObjectWithId(String id) {
            this.id = id;
        }

    }

    public static class TestParentObject {

        private UUID id;

        public TestParentObject() {}

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

    }

    public static class TestObjectWithUuid extends TestParentObject {

        public String value;

        public TestObjectWithUuid() {}

    }

    public static enum MyEnum {
        VALUE_A, VALUE_B, VALUE_C
    }

    public static class TestObjectWithEnum {

        public MyEnum enum1;
        public MyEnum enum2;

    }

    public static class TestObjectWithInnerObjects {

        public TestObject object1;
        public TestObject object2;
        public TestObjectWithId objectWithId;
        public TestObjectWithUuid objectWithUuid;

        public TestObjectWithId[] objectWithIdArray;
        public List<TestObjectWithId> objectWithIdList;
        public TestObjectWithUuid[] objectWithUuidArray;
        public List<TestObjectWithUuid> objectWithUuidList;

    }

}