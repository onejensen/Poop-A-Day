import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { NavigationContainer } from '@react-navigation/native';
import TrackerScreen from '../screens/TrackerScreen';
import LogScreen from '../screens/LogScreen';
import StatsScreen from '../screens/StatsScreen';

const Tab = createBottomTabNavigator();

export default function AppNavigator() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={({ route }) => ({
          tabBarIcon: ({ focused, color, size }) => {
            let iconName;

            if (route.name === 'Tracker') {
              iconName = '🚽';
            } else if (route.name === 'Log') {
              iconName = '📝';
            } else if (route.name === 'Stats') {
              iconName = '📊';
            }

            // Using Text for emoji icons for simplicity
            return <Text style={{ fontSize: size }}>{iconName}</Text>;
          },
          tabBarActiveTintColor: 'tomato',
          tabBarInactiveTintColor: 'gray',
          headerShown: false,
        })}
      >
        <Tab.Screen name="Tracker" component={TrackerScreen} />
        <Tab.Screen name="Log" component={LogScreen} />
        <Tab.Screen name="Stats" component={StatsScreen} />
      </Tab.Navigator>
    </NavigationContainer>
  );
}
