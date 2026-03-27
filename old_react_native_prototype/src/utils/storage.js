import AsyncStorage from '@react-native-async-storage/async-storage';

const STORAGE_KEY = '@poop_logs';

export const addLog = async (timestamp) => {
  try {
    const existingLogs = await getLogs();
    const newLogs = [timestamp, ...existingLogs];
    await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(newLogs));
  } catch (e) {
    console.error('Error saving log', e);
  }
};

export const getLogs = async () => {
  try {
    const jsonValue = await AsyncStorage.getItem(STORAGE_KEY);
    return jsonValue != null ? JSON.parse(jsonValue) : [];
  } catch (e) {
    console.error('Error reading logs', e);
    return [];
  }
};

export const clearLogs = async () => {
    try {
        await AsyncStorage.removeItem(STORAGE_KEY);
    } catch(e) {
        console.error('Error clearing logs', e);
    }
}
