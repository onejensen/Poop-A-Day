import React, { useState, useCallback } from 'react';
import { StyleSheet, Text, View, FlatList, RefreshControl } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { getLogs } from '../utils/storage';

export default function LogScreen() {
  const [logs, setLogs] = useState([]);
  const [refreshing, setRefreshing] = useState(false);

  const loadLogs = async () => {
    const data = await getLogs();
    // logs are stored as ISO strings, sort descending
    data.sort((a, b) => new Date(b) - new Date(a));
    setLogs(data);
  };

  useFocusEffect(
    useCallback(() => {
      loadLogs();
    }, [])
  );

  const onRefresh = async () => {
    setRefreshing(true);
    await loadLogs();
    setRefreshing(false);
  };

  const renderItem = ({ item }) => {
    const date = new Date(item);
    return (
      <View style={styles.item}>
        <Text style={styles.dateText}>{date.toLocaleDateString()}</Text>
        <Text style={styles.timeText}>{date.toLocaleTimeString()}</Text>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>History 💩</Text>
      <FlatList
        data={logs}
        keyExtractor={(item, index) => index.toString()}
        renderItem={renderItem}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        ListEmptyComponent={
          <Text style={styles.emptyText}>No logs yet. Go make some! 🚽</Text>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    paddingTop: 50,
    paddingHorizontal: 20,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  item: {
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  dateText: {
    fontSize: 16,
  },
  timeText: {
    fontSize: 16,
    color: '#555',
  },
  emptyText: {
    textAlign: 'center',
    marginTop: 50,
    fontSize: 16,
    color: '#888',
  }
});
