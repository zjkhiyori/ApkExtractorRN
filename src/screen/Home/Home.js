import React, { Component } from 'react';
import {
  View,
  Text,
  ScrollView,
  StatusBar,
} from 'react-native';
import { observer } from 'mobx-react/native';
import { TabView, TabBar, SceneMap } from 'react-native-tab-view';
import HomeStore from './HomeStore';
import UserAppPage from './pages/UserAppPage';
import SystemAppPage from './pages/SystemAppPage';
import AllAppPage from './pages/AllAppPage';
import color from '../../resource/color';

@observer
export default class Home extends Component {
  constructor(props) {
    super(props);
    this.navigation = this.props.navigation;
    this.stateStore = new HomeStore();
  }

  componentDidMount() {
  }

  render() {
    return (
      <View style={{ flex: 1 }}>
        <TabView
          navigationState={this.stateStore.tabState}
          renderScene={SceneMap({
            userApp: () => <UserAppPage/>,
            systemApp: () => <SystemAppPage/>,
            allApp: () => <AllAppPage/>,
          })}
          onIndexChange={index => this.stateStore.changeIndex(index)}
          renderTabBar={props => <TabBar
            {...props}
            style={{ backgroundColor: color.cyan, paddingTop: StatusBar.currentHeight }}
            indicatorStyle={{ backgroundColor: color.white }}
          />}
        />
      </View>
    );
  }
}
