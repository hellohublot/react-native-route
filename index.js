
import React, { Component } from 'react'
import { View, StyleSheet, NativeModules, NativeEventEmitter, requireNativeComponent, UIManager, findNodeHandle, processColor, Platform, DeviceEventEmitter } from 'react-native'

const HTRouteEventManagerEmitter = new NativeEventEmitter(NativeModules.HTRouteEventManager)

const HTRouteView = requireNativeComponent('HTRouteView')

const HTNaviveRouteManager = NativeModules.HTRouteManager

const globalValue = {
	registerList: {},
	count: 1000,
	componentList: {}
}

const sureComponentItem = (id, defaultValue = {}) => {
	let componentItem = globalValue.componentList[id]
	if (componentItem == null) {
		globalValue.componentList[id] = defaultValue
	}
}

const encodeRouteData = (routeData, navigation) => {
	let reloadRouteData = { ...routeData }
	let componentName = reloadRouteData?.componentName
	let componentPropList = reloadRouteData?.componentPropList ?? {}
	let componentRouteOptionList = reloadRouteData?.componentRouteOptionList ?? {}
	componentRouteOptionList = {
		...HTRouteManager.defaultRouteOption({ ...routeData, navigation }),
		...componentRouteOptionList,
	}

	if (componentName && !globalValue.registerList[componentName]) {
		return null
	}


	let id = componentRouteOptionList['id']
	if (id == null) {
		globalValue.count += 1
		id = `${globalValue.count}`
	}
	componentRouteOptionList['id'] = id

	reloadRouteData['componentRouteOptionList'] = componentRouteOptionList

	sureComponentItem(id, { componentPropList })

	return reloadRouteData
}

const decodeRouteData = (props) => {
	let reloadProps = { ...props }
	let componentRouteOptionList = reloadProps?.componentRouteOptionList ?? {}
	let id = componentRouteOptionList?.id
	sureComponentItem(id)

	let componentValueList = globalValue.componentList[id] ?? {}
	let componentPropList = componentValueList['componentPropList'] ?? {}
	reloadProps.componentPropList = componentPropList
	return reloadProps
}


const routeListener = HTRouteEventManagerEmitter.addListener('onHTRouteEventChange', (value) => {
	let id = value['id']
	let actionName = value['actionName']
	let componentRef = globalValue.componentList[id]?.ref
	if (componentRef == null) {
		return
	}
	DeviceEventEmitter.emit('onHTRouteEvent', { title: 'controller', value: value })

	if (value?.actionName == 'dealloc') {
		globalValue.componentList[id].ref = null
		return
	}
	

	let componentFunction = componentRef[actionName]
	if (componentFunction == null) {
		return
	}
	componentFunction.call(componentRef, value)
})



const createNavigationWithRootRefFunction = (rootRefFunction, props) => {
	const actionList = ['push', 'pop', 'navigate', 'replace', 'popToRoot', 'present', 'dismiss']
	let navigation = {}
	
	for (let action of actionList) {
		navigation[action] = (componentName, componentPropList, componentRouteOptionList, animated) => {
			let routeData = {
				action, 
				componentName, 
				componentPropList,
				componentRouteOptionList,
				animated
			}
			DeviceEventEmitter.emit('onHTRouteEvent', { title: 'navigation', value: routeData })
			routeData = encodeRouteData(routeData, navigation)
			if (!routeData) {
				return
			}
			if (rootRefFunction == null) {
				HTNaviveRouteManager.route(routeData)
			} else {
				UIManager.dispatchViewManagerCommand(
					findNodeHandle(rootRefFunction()), 
					'touchRouteData',
					[routeData]
				)
			}
		}
	}
	navigation['goBack'] = navigation['pop']
	navigation['back'] = navigation['pop']
	navigation['popToTop'] = navigation['popToRoot']


	navigation['createRouteData'] = (action, componentName, componentPropList, componentRouteOptionList, animated) => {
		let routeData = {
			action,
			componentName,
			componentPropList,
			animated,
			componentRouteOptionList,
		}
		routeData = encodeRouteData(routeData, navigation)
		if (!routeData) {
			return
		}
		return routeData
	}



	let componentPropList = decodeRouteData(props).componentPropList ?? {}
	navigation['state'] = {}
	navigation['state'].params = componentPropList
	navigation['getParam'] = (key, defaultValue) => {
		let value = componentPropList[key]
		return value ?? defaultValue
	}
	navigation['setParams'] = (key, value) => {
		componentPropList[key] = value
	}


	return navigation
}



class HTRouteManager {

	static defaultRouteOption = (props) => ({
		backgroundColor: processColor('white'),
		lazyRender: false,
	})

	static defaultRouteNavigationRender = (props) => {
		return null
	}


	static defaultNavigation = createNavigationWithRootRefFunction(null)



	static register(registerList) {
		globalValue.registerList = registerList
	}

	static readRegisterFunction = (props) => {
		let registerFunction = globalValue.registerList[props?.componentName]
		if (registerFunction == null) {
			registerFunction = () => {
				return {}
			}
		}
		return registerFunction
	}

}

class HTRouteComponent extends Component {

	constructor(props) {
		super(props)
		let navigation = createNavigationWithRootRefFunction(() => this.rootRef, props)

		this.reloadProps = { ...props, navigation }

	}

	render() {
		let bindRef = (ref) => {
			if (ref?.renderWrappedComponent) {
				return
			}
			let id = this.reloadProps.componentRouteOptionList['id']
			let componentItem = globalValue.componentList[id]
			if (componentItem) {
				componentItem.ref = ref
				globalValue.componentList[id] = componentItem
			}
		}
		let ComponentClass = HTRouteManager.readRegisterFunction(this.reloadProps)()
		let pointerEvents = this.reloadProps.componentRouteOptionList['pointerEvents'] ?? null
		return (
			<HTRouteView 
				style={{ flex: 1 }}
				pointerEvents={ pointerEvents }
				ref={ref => this.rootRef = ref}>
				{ 
					HTRouteManager.defaultRouteNavigationRender(this.reloadProps)
				}
				<ComponentClass ref={bindRef} getRef={bindRef} navigation={this.reloadProps.navigation} />
			</HTRouteView>
		)
	}

}

module.exports = {
	HTRouteManager,
	HTRouteComponent,
	HTRouteView,
}
