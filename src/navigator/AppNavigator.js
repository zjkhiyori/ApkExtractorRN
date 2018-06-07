import React from 'react';
import { StackNavigator } from 'react-navigation';

import Home from '../screen/Home/Home';

const routeConfigs = {
  Home: {
    screen: Home,
  },
};
const stackNavigatorConfig = {
  initialRouteName: 'Home',
  navigationOptions: {
    header: null,
  }
};

const AppNavigator = StackNavigator(routeConfigs, stackNavigatorConfig);

export default AppNavigator;
