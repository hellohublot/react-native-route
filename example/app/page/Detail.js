import React, { Component } from 'react'
import { View, Text, StyleSheet } from 'react-native'
import { HTRouteView } from 'react-native-route'


export default class Detail extends Component {

	static navigationOptions = ({ navigation }) => {
		return {
			headerBackgroundColor: 'coral',
			title: navigation.state.params['title']
		}
	}

	render() {
		let navigation = this.props.navigation
		let title = `detail page and param\n\ntitle=${navigation.getParam('title')}\n\nblock=${navigation.getParam('block')}`
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
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: 'white'
	},
	itemTitle: {
		fontSize: 20,
		color: '#333',
		textAlign: 'center',
	}
})