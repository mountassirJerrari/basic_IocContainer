package com.snater.basicIoc;

import java.lang.reflect.InvocationTargetException;

import com.snater.basicIoc.container.Container;
import com.snater.basicIoc.scanning.ClassPathScanner;
import com.snater.basicIoc.services.UserAccountClientComponent;

public class Main {

	public static void main(String[] args) {
		
		
		try {
			Container container = Container.getInstance(Main.class) ;
			container.getService(UserAccountClientComponent.class).displayUserAccount();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
