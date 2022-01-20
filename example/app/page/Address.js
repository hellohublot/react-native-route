import React, { Component } from 'react'
import { View, Text, Image, StyleSheet, processColor } from 'react-native'
import { HTRouteView } from 'react-native-route'


export default class Detail extends Component {

	static navigationOptions = ({ navigation }) => {
		let backItemAction = navigation.getParam('backAction')
		return {
			statusHeight: 0,
			headerBackgroundColor: 'turquoise',
			title: navigation.state.params['title'],
			backgroundStyle: { borderTopLeftRadius: 10, borderTopRightRadius: 10 },
			leftItemList: [
				<HTRouteView style={{ paddingRight: 30, height: '100%', justifyContent: 'center' }}
					routeData={navigation.createRouteData(backItemAction, 'Address')}
				>
					<Image source={require('~/img/back_white.png')} />
				</HTRouteView>
			]
		}
	}

	render() {
		let navigation = this.props.navigation
		let routeData = navigation.createRouteData('push', 'Address', {  title: 'Again Address Page', backAction: 'pop' }, { backgroundColor: processColor('clear') })
		return (
			<View style={styleList.container}>
				<HTRouteView routeData={routeData}>
					<Text style={styleList.itemTitle}>Tap Push A Address Page Again</Text>
				</HTRouteView>
			</View>
		)
	}

}

const styleList = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: 'white',
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: 'white',
	},
	itemTitle: {
		fontSize: 20,
		color: '#333',
		textAlign: 'center',
	}
})