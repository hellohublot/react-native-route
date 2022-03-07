import React, { Component } from 'react'
import { View, Text, SafeAreaView, StyleSheet, Animated, Dimensions, StatusBar } from 'react-native'
import PropTypes from 'prop-types'

const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } = Dimensions.get('window')
const isIOSFullScreen = (SCREEN_WIDTH == 375 && SCREEN_HEIGHT == 812) 
	|| (SCREEN_WIDTH == 414 && SCREEN_HEIGHT == 896)
	|| (SCREEN_WIDTH == 360 && SCREEN_HEIGHT == 780)
	|| (SCREEN_WIDTH == 390 && SCREEN_HEIGHT == 844)
	|| (SCREEN_WIDTH == 428 && SCREEN_HEIGHT == 926)
let STATUS_BAR_HEIGHT = 0
let HOME_BAR_HEIGHT = 0
if (Platform.OS == 'ios') {
	if (isIOSFullScreen) {
		STATUS_BAR_HEIGHT = 44
		HOME_BAR_HEIGHT = 34
	} else {
		STATUS_BAR_HEIGHT = 20
	}
} else {
	STATUS_BAR_HEIGHT = StatusBar.currentHeight
}

export default class HTNavigationBar extends Component {

	static STATUS_BAR_HEIGHT = STATUS_BAR_HEIGHT

	static HOME_BAR_HEIGHT = HOME_BAR_HEIGHT

	static propTypes = {
		float: PropTypes.bool,
        leftItemList: PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.element, PropTypes.number])),
        backgroundColor: PropTypes.string,
        titleView: PropTypes.element,
        title: PropTypes.string,
        titleStyle: PropTypes.object,
        rightItemList: PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.element, PropTypes.number])),
    }

    _renderNavigationItem = (itemList) => {
    	return itemList?.map((item, index) => {
			if (typeof(item) == 'number') {
				return (<View key={index} style={[styleList.navigationLeftItemSpaceContainer, { width: item }]}></View>)
			} else {
				return <View key={index}>{ item }</View>
			}
		})
    }

	render() {
		return (
			<View style={[styleList.navigationBar, this?.props?.style, this.props.float ? {...StyleSheet.absoluteFill, ...styleList.navigationBarFloat} : null]}>
				<Animated.View style={[StyleSheet.absoluteFill, styleList.navigationBarBackground, { backgroundColor: this.props.backgroundColor }, this.props.backgroundStyle]}>
				</Animated.View>
				<View style={StyleSheet.absoluteFill}>
				{
					this.props.backgroundView
				}
				</View>
				{
						
					<View style={{ height: this?.props?.statusHeight ?? STATUS_BAR_HEIGHT }}></View>
					
					/*
						<SafeAreaView />
					*/
				}
				
				<View style={[styleList.navigationContent, this?.props?.contentStyle]}>
					<View style={[StyleSheet.absoluteFill, styleList.navigationTitleContainer]}>
					 	<Text style={this.props.titleStyle} numberOfLines={1}>{ this.props.title }</Text>
					</View>
					<View style={[StyleSheet.absoluteFill, styleList.navigationTitleView]}>
					 	{ this.props.titleView }
					</View>

					<View style={styleList.navigationLeftContainer}>
						{ this._renderNavigationItem(this.props.leftItemList) }
					</View>
					<View style={{ flex: 1 }}>
					</View>
					<View style={styleList.navigationRightContainer}>
						{ this._renderNavigationItem(this.props.rightItemList) }
					</View>
				</View>
			</View>
		)
	}

}

const styleList = StyleSheet.create({
	navigationBar: {
		zIndex: 10,
	},
	navigationBarFloat: {
		bottom: null,
	},
	navigationBarBackground: {

	},
	navigationContent: {
		height: 44,
		flexDirection: 'row',
		alignItems: 'center',
		paddingLeft: 15,
		paddingRight: 15,
	},
	navigationLeftContainer: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-start',
		alignItems: 'center',
	},
	navigationTitleView: {
		justifyContent: 'center',
		alignItems: 'center',
	},
	navigationTitleContainer: {
		left: 80,
		right: 80,
		justifyContent: 'center',
		alignItems: 'center',
	},
	navigationRightContainer: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-end',
		alignItems: 'center',
	},
})