import React, { Component } from 'react'
import { View, Text, FlatList, StyleSheet, StatusBar, processColor } from 'react-native'
import { HTRouteView } from 'react-native-route'


export default class Home extends Component {

	static navigationOptions = {
		headerShown: false
	}

	constructor(props) {
		super(props)
		let navigation = this.props.navigation
		let paramList = 
		this.state = {
			itemList: [
				{ title: 'Push To Detail', routeData: navigation.createRouteData(
					'push', 'Detail', {
						'title': 'Detail Page', 
						'block': () => {}
					}
				) },
				{ title: 'Navigate To Mine', routeData: navigation.createRouteData(
					'navigate', 'Mine'
				) },
				{ title: 'Present To Address', routeData: navigation.createRouteData(
					'present', 'Address', { 'title': 'Address Page', backAction: 'dismiss' }, 
					{ presentEdgeTop: 200, presentBackgroundColor: processColor('rgba(0, 0, 0, 0.2)'), presentAnimatedDuration: 0.25, backgroundColor: processColor('clear') }
				) },
			]
		}
	}

	componentDidAppear({ isSecondAppear }) {
		if (isSecondAppear) {
			StatusBar.setBarStyle('light-content', true)
		}
	}

	_renderItem = (item, index) => {
		return (
			<HTRouteView style={styleList.itemContainer} routeData={item.routeData}>
				<Text style={styleList.itemTitle}>{item.title}</Text>
			</HTRouteView>
		)
	}

	render() {
		return (
			<View style={styleList.container}>
				<FlatList
					data={this.state.itemList}
					renderItem={({ item, index }) => this._renderItem(item, index)}
				/>
			</View>
		)
	}

}

const styleList = StyleSheet.create({
	container: {
		flex: 1,
		paddingTop: 200,
		backgroundColor: 'salmon'
	},
	itemContainer: {
		height: 100,
		justifyContent: 'center',
		alignItems: 'center'
	},
	itemTitle: {
		fontSize: 23,
		color: 'white'
	}
})