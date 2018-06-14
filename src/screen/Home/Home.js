import React, { Component } from 'react';
import {
  View,
  StatusBar,
  Dimensions
} from 'react-native';
import { observer } from 'mobx-react/native';
import { TabView, TabBar, SceneMap } from 'react-native-tab-view';
import HomeStore from './HomeStore';
import AppPage from './pages/AppPage';
import Color from '../../resource/color';
import Constants from '../../resource/constants'

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
          renderScene={({ route }) => {
            switch(route.key) {
              case 'userApp':
                return <AppPage focused={this.stateStore.tabState.index === 0} type={Constants.USER_APP}/>;
              case 'systemApp':
                return <AppPage focused={this.stateStore.tabState.index === 1} type={Constants.SYSTEM_APP}/>;
              case 'allApp':
                return <AppPage focused={this.stateStore.tabState.index === 2} type={Constants.ALL_APP}/>;
            }
          }}
          initialLayout={{ height: 0, width: Dimensions.get('window').width }}
          onIndexChange={index => this.stateStore.changeIndex(index)}
          renderTabBar={props => <TabBar
            {...props}
            style={{ backgroundColor: Color.cyan, paddingTop: StatusBar.currentHeight }}
            indicatorStyle={{ backgroundColor: Color.white }}
          />}
        />
      </View>
    );
  }
}
