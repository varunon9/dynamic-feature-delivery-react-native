/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import type {Node} from 'react';
import {
  Image,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

const approvedIcon = require('./src/images/approved.png');

const Section = ({children, title}): Node => {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}>
        {title}
      </Text>
      <Text
        style={[
          styles.sectionDescription,
          {
            color: isDarkMode ? Colors.light : Colors.dark,
          },
        ]}>
        {children}
      </Text>
    </View>
  );
};

const App: () => Node = () => {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Text>Approved icon:</Text>
        <View style={{borderWidth: 1, borderColor: 'blue'}}>
          <Image source={approvedIcon} style={{width: 60, height: 60}} />
        </View>
        <Text>Learniti icon:</Text>
        <Image 
          source={{uri: 'https://learniti.com/images/google-play.png'}} 
          style={{width: 194, height: 64}} 
        />
        <Text>This is a normal Text</Text>
        <Text style={styles.semiBold}>This is a semi bold Text</Text>
        <Text style={styles.bold}>This is a bold Text</Text>
        <Text style={styles.happyHotels}>
          phone-24x7 {String.fromCharCode(0xe900)}
        </Text>
        <Text style={styles.happyHotels}>
          alarm-bell {String.fromCharCode(0xe903)}
        </Text>
        <Text style={styles.happyHotels}>
          double-bed {String.fromCharCode(0xe912)}
        </Text>
        <Text style={styles.happyHotels}>
          briefcase {String.fromCharCode(0xe917)}
        </Text>
        <Text allowFontScaling={false} style={styles.happyHotels}>
          airport {String.fromCharCode(0xe902)}
        </Text>
        <Header />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <Section title="Step One">
            Edit <Text style={styles.highlight}>App.js</Text> to change this
            screen and then come back to see your edits.
          </Section>
          <Section title="See Your Changes">
            <ReloadInstructions />
          </Section>
          <Section title="Debug">
            <DebugInstructions />
          </Section>
          <Section title="Learn More">
            Read the docs to discover what to do next:
          </Section>
          <LearnMoreLinks />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
  semiBold: {
    fontFamily: 'Roboto-Medium'
  },
  bold: {
    fontWeight: 'bold'
  },
  happyHotels: {
    fontFamily: 'happyHotels',
    fontSize: 48
  }
});

export default App;
