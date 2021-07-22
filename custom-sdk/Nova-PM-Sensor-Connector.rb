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