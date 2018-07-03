import { NativeModules } from 'react-native';
import _ from 'lodash';
import Locales from '../resource/locales';

const { ApkExtractorModule } = NativeModules;
const locale = ApkExtractorModule.deviceLocale;

export default class Lang {
  static get(key) {
    console.log(locale);
    console.log(Locales[locale]);
    let message = _.get(Locales, `${locale}.${key}`);
    if (!message) {
      message = _.get(Locales, `en.${key}`);
    }
    if (!message) {
      message = `missing ${key}`;
    }
    return message;
  }
}
