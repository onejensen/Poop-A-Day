import React, { useState, useCallback } from 'react';
import { StyleSheet, Text, View, ScrollView } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { getLogs } from '../utils/storage';

export default function StatsScreen() {
  const [stats, setStats] = useState({
    today: 0,
    week: 0,
    month: 0,
    year: 0
  });

  const calculateStats = async () => {
    const logs = await getLogs();
    const now = new Date();
    
    let todayCount = 0;
    let weekCount = 0;
    let monthCount = 0;
    let yearCount = 0;

    const startOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    
    // Start of week (Sunday)
    const startOfWeek = new Date(startOfDay);
    startOfWeek.setDate(startOfDay.getDate() - startOfDay.getDay());

    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const startOfYear = new Date(now.getFullYear(), 0, 1);

    logs.forEach(log => {
      const logDate = new Date(log);
      
      if (logDate >= startOfDay) todayCount++;
      if (logDate >= startOfWeek) weekCount++;
      if (logDate >= startOfMonth) monthCount++;
      if (logDate >= startOfYear) yearCount++;
    });

    setStats({
      today: todayCount,
      week: weekCount,
      month: monthCount,
      year: yearCount
    });
  };

  useFocusEffect(
    useCallback(() => {
      calculateStats();
    }, [])
  );

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.header}>Statistics 📊</Text>
      
      <View style={styles.card}>
        <Text style={styles.label}>Today</Text>
        <Text style={styles.number}>{stats.today}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.label}>This Week</Text>
        <Text style={styles.number}>{stats.week}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.label}>This Month</Text>
        <Text style={styles.number}>{stats.month}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.label}>This Year</Text>
        <Text style={styles.number}>{stats.year}</Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    backgroundColor: '#fff',
    paddingTop: 50,
    paddingHorizontal: 20,
    alignItems: 'center',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
  },
  card: {
    width: '100%',
    backgroundColor: '#f9f9f9',
    padding: 20,
    borderRadius: 15,
    marginBottom: 15,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  label: {
    fontSize: 18,
    fontWeight: '500',
    color: '#333',
  },
  number: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#4CAF50',
  }
});
