import { NativeModules } from 'react-native';

type OneShotType = {
  startConfig(ssid: String, psw: String, timeoutSecond: number): Promise<any>;
  stopConfig(): void;
};

const { OneShot } = NativeModules;

export default OneShot as OneShotType;
