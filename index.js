import React from 'react';
import {AppRegistry, StyleSheet, Text, View} from 'react-native';

class HelloWorld extends React.Component {
  render() {
    global.foo='bar';
    console.log("global.foo"+global.foo)
    return (
      <View style={styles.container}>
        <Text style={styles.hello}>Main App</Text>
      </View>);
  }
}

var styles =StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  hello:{
    fontSize: 30,
    textAlign: 'center',
  },
});

AppRegistry.registerComponent('MainReactNativeApp', ()=> HelloWorld);
