import { observable, action } from 'mobx';
import { Lang } from '../../utils';

export default class HomeStore {
  @observable tabState = {
    index: 0,
    routes: [
      {key: 'userApp', title: Lang.get('homePage.userApp')},
      {key: 'systemApp', title: Lang.get('homePage.sysApp')},
      {key: 'allApp', title: Lang.get('homePage.allApp')}
    ]
  };

  @action
  changeIndex(index) {
    console.log('---------indexChange----------');
    console.log(index);
    this.tabState = Object.assign({}, this.tabState, {index});
  }

  @action
  goPage(route) {

  }
}
