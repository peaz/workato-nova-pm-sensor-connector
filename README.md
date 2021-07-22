The Nova PM Sensor Extension allows you to read the air particulate sensor data via the serial interface. 

- Sensor Details: http://inovafitness.com/en/Laser-PM2-5-Sensor-35.html
- Sensor Specs: http://inovafitness.com/software/SDS011%20laser%20PM2.5%20sensor%20specification-V1.3.pdf
- Where to get: https://www.aliexpress.com/item/nova-PM-sensor-SDS011-High-precision-laser-pm2-5-air-quality-detection-sensor-module-Super-dust/32317805049.html

# Details
It reads the data from the sensor over the serial interface and returns the following map object.

| Key | Type | Description |
| --- | --- | --- |
| hex | String | The raw data output from the sensor in hex code |
| pm10 | Double | The PM 10 reading |
| pm25 | Double | The PM 2.5 reading |

## Building extension

Steps to build an extension:

1. Install the latest Java 8 SDK
2. Use `./gradlew jar` command to bootstrap Gradle and build the project.
3. The output is in `build/libs`.

## Installing the extension to OPA

1. Add a new directory called `ext` under Workato agent install directory.
2. Copy the extension JAR file to `ext` directory.
  - opa-extensions-sensor-0.1.jar [pre-built in build/libs](https://gitlab.com/peaz/workato-nova-pm-sensor-connector/-/blob/main/build/libs/workato-nova-pm-sensor-connector-0.1.jar)
  - jSerialComm-2.7.0.jar [in libs](https://gitlab.com/peaz/workato-nova-pm-sensor-connector/-/blob/main/libs/jSerialComm-2.7.0.jar)
3. Update the `config/config.yml` to add the `ext` file to class path.

```yml
    server:
      classpath: /opt/opa/workato-agent/ext
```

4. Update the `config/config.yml` to configure the new extension.

```yml
    extensions:
      security:
        controllerClass: com.mycompany.onprem.NovaPMSensorExtension
        serialPortDevice: /dev/ttyUSB0
```

## Using the extension in Recipe

In order to use the extension in a recipe, we need a custom adapter in Workato. The custom SDK for the extension 
can be found [here](https://gitlab.com/peaz/workato-nova-pm-sensor-connector/-/blob/main/custom-sdk/Nova-PM-Sensor-Connector.rb).

```ruby
{
    title: 'Nova PM Sensor',
    secure_tunnel: true,
  
    connection: {
      fields: [{ name: 'profile', hint: 'On-prem sensor connection profile' }],
      authorization: { type: 'none'}
    },
  
    test: ->(connection) {
      get("http://localhost/ext/#{connection['profile']}/readSensor").headers('X-Workato-Connector': 'enforce')
    },
  
    actions: {
      read_sensor_data: {
  
        title: 'Get sensor reading',
        description: 'Read latest particulate matter sensor reading.',
  
        output_fields: ->(_) { [{name: 'pm25'},{name: 'pm10'},{name: 'hex'}] },
  
        execute: ->(connection) {
          get("http://localhost/ext/#{connection['profile']}/readSensor").headers('X-Workato-Connector': 'enforce')
        }
      }
    }
  }
```
