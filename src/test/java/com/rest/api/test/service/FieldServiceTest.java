package com.rest.api.test.service;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.api.exception.FieldNotFoundException;
import com.rest.api.model.Field;
import com.rest.api.service.FieldService;

@RunWith(SpringRunner.class)
@DataJpaTest
// @AutoConfigureTestDatabase(replace=Replace.NONE)
public class FieldServiceTest {

	@Autowired
	private FieldService fieldService;

	@Test
	public void getField() throws Exception {
		Field field = fieldService.findById(1L);
		Assert.assertEquals(1, field.getId().intValue());
		Assert.assertEquals("text", field.getfString());
	}
	
	@Test(expected = FieldNotFoundException.class)
	public void getFieldNotFound() throws Exception {
		fieldService.findById(99L);
	}
	
	@Test
	public void addField() throws Exception {
		Field field = new Field();
		field.setfString("prova");

		fieldService.add(field);
		Assert.assertNotNull(field.getId());

		Field ins = fieldService.findById(field.getId().longValueExact());
		Assert.assertEquals(field.getfString(), ins.getfString());

	}

	@Test
	public void updateCar() throws Exception {
		Field field = fieldService.findById(1L);
		field.setfString("string");

		fieldService.update(1L, field);

		Field upd = fieldService.findById(1L);
		Assert.assertEquals(field.getfString(), upd.getfString());
	}
	
	@Test(expected = FieldNotFoundException.class)
	public void updateFieldNotFound() throws Exception {
		Field field = new Field();
		field.setId(BigInteger.valueOf(99));

		fieldService.update(field.getId().longValueExact(), field);
	}

	@Test
	public void deleteField() {
		fieldService.deleteById(1L);
	}
	
	@Test
	public void deleteFieldNotFound() {
		fieldService.deleteById(99L);
	}

	@Test
	public void listFields() {
		Iterable<Field> fieldList = fieldService.findAll();
		//Assert.assertEquals(1, fieldList.size());

		for (Field f : fieldList) {
			switch (f.getId().intValue()) {
			case 1:
				Assert.assertEquals("text", f.getfString());
				break;
			default:
				Assert.fail();
				break;
			}
		}

	}

}
