import { observable, action } from 'mobx';
import { NativeModules } from 'react-native';

const { ApkExtractorModule } = NativeModules;

export default class UserAppPageStore {
  @observable itemSource = [];

  @action
  async getUserAppsInfo() {
    try {
      this.itemSource  = await ApkExtractorModule.getUserApp();
    } catch (error) {
      alert('Error!', error.message);
    }
  }

  @action
  async getSystemAppsInfo() {
    try {
      this.itemSource  = await ApkExtractorModule.getSystemApp();
    } catch (error) {
      alert('Error!', error.message);
    }
  }

  @action
  async getAllAppsInfo() {
    try {
      this.itemSource  = await ApkExtractorModule.getAllApp();
    } catch (error) {
      alert('Error!', error.message);
    }
  }
}
