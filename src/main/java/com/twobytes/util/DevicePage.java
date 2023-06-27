package com.twobytes.util;

import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Component;

@Component("DevicePage")
public class DevicePage {
	
	public String page(Device device, String desktopPage, String mobilePage) {
		if (device.isMobile()) {
			return mobilePage;
		}else {
			return desktopPage;
		}
	}

}