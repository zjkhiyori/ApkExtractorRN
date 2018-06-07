import React, { Component } from 'react';
import {
  Platform,
  Alert,
  StatusBar,
} from 'react-native';
import AppNavigator from './navigator/AppNavigator';

const isAndroid = Platform.OS === 'android';

alert = (...args) => Alert.alert(...args);

export default class App extends Component {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    StatusBar.setBarStyle('light-content');
    if (isAndroid) {
      StatusBar.setTranslucent(true);
      StatusBar.setBackgroundColor('rgba(0, 0, 0, 0.2)');
    }
  }

  render() {
    return (
      <AppNavigator
        ref={(nav) => {
          global.NAVIGATOR = nav;
        }}
        onNavigationStateChange={(prevState, currentState) => {
          global.CURRENT_ROUTE_NAME = currentState.routes[currentState.index].routeName;
        }}
      />
    );
  }
}
