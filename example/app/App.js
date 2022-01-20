/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { Component } from 'react'
import { Image } from 'react-native'
import { HTRouteManager, HTRouteComponent, HTRouteView, HTNavigationBar } from 'react-native-route'

HTRouteManager.register({
	'Home': () => require('~/page/Home').default,
	'Mine': () => require('~/page/Mine').default,
	'Detail': () => require('~/page/Detail').default,
	'Address': () => require('~/page/Address').default,
})
HTRouteManager.defaultRouteNavigationRender = (props) => {
	const readComponentOptionsFromProps = (props) => {
		let componentClass = HTRouteManager.readRegisterFunction(props)()
		let navigationOptions = componentClass?.navigationOptions ?? {}
		if (typeof(navigationOptions) == 'function') {
			let _navigationOptions = navigationOptions(props) ?? {}
			navigationOptions = _navigationOptions
		}
		navigationOptions = { ...navigationOptions }
		return navigationOptions
	}
	let navigationOptions = readComponentOptionsFromProps(props)

	// header
	if ((navigationOptions.hasOwnProperty('header') && navigationOptions.header == null) || navigationOptions.headerShown == false) {
		return null
	}

	// header item
	let leftItemList = [
		<HTRouteView style={{ paddingRight: 30, height: '100%', justifyContent: 'center' }}
			routeData={props.navigation.createRouteData('pop')}
		>
			<Image source={require('~/img/back_white.png')} />
		</HTRouteView>
	]
	if (navigationOptions.header_left) {
		leftItemList = [ navigationOptions.header_left ]
	}

	// title
	if (!navigationOptions.title) {
		navigationOptions.title = navigationOptions.headerTitle
	}
	navigationOptions.backgroundColor = navigationOptions.headerBackgroundColor
	

	return (
		<HTNavigationBar
			backgroundColor={'white'}
			titleStyle={{ color: 'white', fontSize: 20 }}
			leftItemList={leftItemList}
			{...navigationOptions}
		/>
	)
}


import { DeviceEventEmitter } from 'react-native'

DeviceEventEmitter.addListener('onHTRouteEvent', (noticeList) => {
	try {
		let title = noticeList?.title ?? ''
		let value = noticeList?.value ?? null
		switch(title) {
		case 'controller': {
			console.log('w', 'controller', `${JSON.stringify(value)}`)
			break
		}
		case 'navigation': {
			console.log('w', 'navigation', `${JSON.stringify(value)}`)
			break
		}}
	} catch(e) {

	}
})


export default class App extends Component {

	render() {
		return (
			<HTRouteComponent
				{ ...this.props }
			/>
		)
	}

}
