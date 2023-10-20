package com.snater.basicIoc;

import java.lang.reflect.InvocationTargetException;

import com.snater.basicIoc.container.Container;
import com.snater.basicIoc.scanning.ClassPathScanner;
import com.snater.basicIoc.services.UserAccountClientComponent;

public class Main {

	public static void main(String[] args) throws Exception {
		
		
		try {
			Container container = Container.getInstance(Main.class) ;
			UserAccountClientComponent obj =  container.getService(UserAccountClientComponent.class);
			obj.accountServiceImp.setAmount(323);
			obj.displayUserAccount();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
