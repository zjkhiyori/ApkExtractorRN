import React, { PureComponent } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Image,
  InteractionManager,
} from 'react-native';
import { observer } from 'mobx-react/native';
import AppPageStore from './AppPageStore';
import Constant from '../../../resource/constants';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  }
});

@observer
export default class AppPage extends PureComponent {
  constructor(props){
    super(props);
    this.renderItem = this.renderItem.bind(this);
    this.stateStore = new AppPageStore();
  }

  componentDidMount() {
    switch (this.props.type) {
      case Constant.USER_APP:
        this.stateStore.getUserAppsInfo();
        break;
      case Constant.SYSTEM_APP:
        this.stateStore.getSystemAppsInfo();
        break;
      case Constant.ALL_APP:
        this.stateStore.getAllAppsInfo();
    }
    console.log('app page did mount');
  }

  componentWillUnmount() {
    console.log('app page will unmount');
  }

  renderItem({ item }) {
    return (
      <View style={{ flexDirection: 'row' }}>
        <Image
          style={{ width: 50, height: 50, resizeMode: 'stretch' }}
          source={{ uri:  'data:image/png;base64,' + item.appIcon}}
        />
        <View>
          <Text>{item.appName}</Text>
          <Text>{item.appPackageName}</Text>
        </View>
      </View>
    )
  }

  render() {
    return (
      <View style={styles.container}>
       <FlatList
         data={this.stateStore.itemSource}
         renderItem={this.renderItem}
         initialNumToRender={20}
         keyExtractor={(item) => `${item.appPackageName}`}
       />
      </View>
    );
  }
}