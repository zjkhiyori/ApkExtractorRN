import { observable, action } from 'mobx';

export default class HomeStore {
  @observable tabState = {
    index: 0,
    routes: [
      {key: 'userApp', title: '用户应用'},
      {key: 'systemApp', title: '系统应用'},
      {key: 'allApp', title: '全部应用'}
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
