/*
 * Copyright (c) 2018 MyCompany, Inc. All rights reserved.
 */

package com.mycompany.onprem;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.fazecast.jSerialComm.*;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NovaPMSensorExtension {

    @Inject
    private Environment env;

    @RequestMapping(path = "/readSensor", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> readSensor() throws Exception {
        Charset encoding = Charset.forName("UTF-8");
        
        String serialPortDevice = env.getProperty("serialPortDevice");

        SerialPort comPort = SerialPort.getCommPort(serialPortDevice);
		comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		String data = "";
		Map<String, Object> entityData = new HashMap<String, Object>();
		byte[] readBuffer = new byte[9];
		
		try {
			comPort.openPort();
			comPort.readBytes(readBuffer, readBuffer.length);
			data = byteArrayToHex(readBuffer);
			//re-read sensor data if data is "00000000000"
			if (data.equals("00000000000")){
				comPort.readBytes(readBuffer, readBuffer.length);
			}
			int pm25Low = readBuffer[2] & 0xff;
			int pm25High = readBuffer[3] & 0xff;
			int pm10Low = readBuffer[4] & 0xff;
			int pm10High = readBuffer[5] & 0xff;

			entityData.put("pm25", ((pm25High*256)+pm25Low)/10.0); 
			entityData.put("pm10", ((pm10High*256)+pm10Low)/10.0);
			entityData.put("hex", data);
			
			//Debug code: Print actual data read from Connector
			System.out.println("Read:" + data);
		} catch (Exception e) { e.printStackTrace(); }
		comPort.closePort();
		return entityData;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
           sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
     }
}
