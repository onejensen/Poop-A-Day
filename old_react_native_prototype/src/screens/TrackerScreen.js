import React from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Dimensions } from 'react-native';
import Animated, { 
  useSharedValue, 
  useAnimatedStyle, 
  withTiming, 
  withSequence,
  runOnJS,
  Easing
} from 'react-native-reanimated';
import { addLog } from '../utils/storage';

const { height } = Dimensions.get('window');

export default function TrackerScreen() {
  const poopTranslateY = useSharedValue(-100);
  const poopOpacity = useSharedValue(0);

  const handlePoop = async () => {
    // Reset position if needed (though we want it to fall newly each time)
    poopTranslateY.value = -100;
    poopOpacity.value = 1;

    // Save log
    await addLog(new Date().toISOString());

    // Animate
    poopTranslateY.value = withSequence(
      withTiming(height / 2 - 50, { duration: 1000, easing: Easing.bounce }),
      withTiming(height / 2 + 50, { duration: 500 }, (finished) => {
         if (finished) {
           poopOpacity.value = withTiming(0, { duration: 200 });
         }
      })
    );
  };

  const animatedStyle = useAnimatedStyle(() => {
    return {
      transform: [{ translateY: poopTranslateY.value }],
      opacity: poopOpacity.value,
    };
  });

  return (
    <View style={styles.container}>
      <Animated.Text style={[styles.poop, animatedStyle]}>💩</Animated.Text>
      
      <TouchableOpacity onPress={handlePoop} activeOpacity={0.8}>
        <Text style={styles.toilet}>🚽</Text>
      </TouchableOpacity>
      
      <Text style={styles.hint}>Tap the toilet!</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  toilet: {
    fontSize: 150,
    zIndex: 1,
  },
  poop: {
    fontSize: 80,
    position: 'absolute',
    top: 0,
    zIndex: 2, // Animate so it goes "into"? Or just falls on top? 
    // If it goes "into", maybe we need it behind the toilet, but visually it's funny on top or falling inside.
    // Let's keep it simple: falls into the middle and fades out.
  },
  hint: {
    marginTop: 20,
    fontSize: 18,
    color: '#888',
  }
});
