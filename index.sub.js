import React from 'react';
import {AppRegistry, StyleSheet, Text, View} from 'react-native';

class HelloWorld extends React.Component {
  render() {
    setTimeout(()=>console.log('hogeglobal'+global.foo),2000);
    return (
      <View style={styles.container}>
        <Text style={styles.hello}>Sub App</Text>
      </View>);
  }
}

var styles =StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#99cc00',
    justifyContent: 'center',
  },
  hello:{
    fontSize: 20,
    textAlign: 'center',
  },
});

AppRegistry.registerComponent('SubReactNativeApp', ()=> HelloWorld);
