import React, { Component } from 'react'
import { View, Text, StyleSheet, StatusBar } from 'react-native'


export default class Mine extends Component {

	static navigationOptions = {
		header: null
	}

	componentDidAppear({ isSecondAppear }) {
		if (isSecondAppear) {
			StatusBar.setBarStyle('dark-content', true)
		}
	}

	render() {
		let title = `Mine Page`
		return (
			<View style={styleList.container}>
				<Text style={styleList.itemTitle}>{title}</Text>
			</View>
		)
	}

}

const styleList = StyleSheet.create({
	container: {
		flex: 1,
		paddingBottom: 100,
		backgroundColor: 'pink',
		alignItems: 'center',
		justifyContent: 'center'
	},
	itemTitle: {
		fontSize: 25,
		color: 'white'
	}
})