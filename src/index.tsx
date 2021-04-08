import { NativeModules } from 'react-native';

type OneShotType = {
  multiply(a: number, b: number): Promise<number>;
};

const { OneShot } = NativeModules;

export default OneShot as OneShotType;
