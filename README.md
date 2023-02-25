## react-native-route
react-native-route is an extreme performance react-native navigation library, It uses native animations and gestures, so it can give you the best performance experience as native apps

<img src="./example/1.gif" width="300">

## Features

- [x] Support the same interface as react-navigation, so you can switch between them
- [x] Support global navigation variables, so navigation can be used anywhere
- [x] Support customizable modal pop-up windows, pop-up windows also support navigation
- [x] Support Component life cycle, no need to observe, directly overload the life cycle method
- [x] Have the Route component, direct navigation after clicking no longer requires thread asynchronous communication
- [x] Have the NativigationBar component, which can be directly inherited or used globally
- [x] Support global observation of navigation events, more convenient log statistics

## Install

```bash
yarn add 'https://github.com/hellohublot/react-native-route.git'
```

## Usage
[View Full Example](./example/app/App.js)

In order to be compatible with the diversity of startup navigation, and the performance considerations of UITabBarItem click events, we put all startups in the native
#### iOS
You can use native NavigationController and UITabBarController, and FDFullscreenPopGesture has been integrated, We use moduleName to preload bundle
```objective-c
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
	...
	[HTRouteBridgeManager loadBridgeWithURL:[self sourceURLForBridge:nil] moduleName:@"ReactNativeDemo" launchOptions:launchOptions];
	...
	NSArray *keyValueList = @[
		@{ titleKey: @"Home", imageKey: @"tabbar_home", selectedImageKey: @"tabbar_home_selected", componentKey: @"Home" },
		@{ titleKey: @"Mine", imageKey: @"tabbar_mine", selectedImageKey: @"tabbar_mine_selected", componentKey: @"Mine" },
	];
	[keyValueList enumerateObjectsUsingBlock:^(NSDictionary *dictionary, NSUInteger index, BOOL * _Nonnull stop) {
		HTRouteController *routeController = [HTRouteController controllerWithComponentName:dictionary[componentKey] componentRouteOptionList:@{@"id": [NSString stringWithFormat:@"%ld", index]}];
		UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:routeController];
		navigationController.fd_viewControllerBasedNavigationBarAppearanceEnabled = false;
		...
	}];
	...
	return YES;
}
```
#### Android.MainApplication
We use moduleName to preload bundle
```java
	public void onCreate() {
		super.onCreate();
		...
		HTRouteGlobal.application = this;
		HTRouteGlobal.moduleName = "ReactNativeDemo";
		getReactNativeHost().getReactInstanceManager().createReactContextInBackground();
	}
```
#### Android.MainActivity
We handle the back button in invokeDefaultOnBackPressed
```java
private HTRouteTabBarController tabBarController;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
		finish();
		return;
	}
	setReactNativeContentView();
}
private void setReactNativeContentView() {
	HTRouteGlobal.activity = this;
	this.tabBarController = new HTRouteTabBarController() {
		private Map createComponentRouteOption() {
			final int count = modelList.size();
			Map<String, Serializable> componentRouteOption = new HashMap() {{
				put("id", count);
			}};
			return componentRouteOption;
		}
		@Override
		public void initDataSource() {
			modelList.clear();
			modelList.add(
			    new HTRouteTabBarModel("Home", R.mipmap.tabbar_home, R.mipmap.tabbar_home_selected,
			        new HTRouteNavigationController(new HTRouteController("Home", createComponentRouteOption())))
			);
			...
		}
	};
	setContentView(this.tabBarController.getView());
}

@Override
public void invokeDefaultOnBackPressed() {
	HTRouteNavigationController selectedFragment = (HTRouteNavigationController) tabBarController.findSelectedFragment();
	if (selectedFragment.childControllerList.size() > 1) {
		selectedFragment.popViewController(true);
	} else {
		moveTaskToBack(true);
	}
}
```

#### App.js
We can handle the parameters of Component.Static by ourselves
```javascript
import { HTRouteManager, HTRouteComponent, HTRouteView, HTNavigationBar } from 'react-native-route'

HTRouteManager.register({
	'Home': () => require('~/page/Home').default,
	'Mine': () => require('~/page/Mine').default,
})
HTRouteManager.defaultRouteNavigationRender = (props) => {
	const readComponentOptionsFromProps = (props) => {
		...
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
	...

	return (
		<HTNavigationBar
			backgroundColor={'white'}
			titleStyle={{ color: 'white', fontSize: 20 }}
			leftItemList={leftItemList}
			{...navigationOptions}
		/>
	)
}


export default class App extends Component {

	render() {
		return (
			<HTRouteComponent
				{ ...this.props }
			/>
		)
	}

}

```

## Contact

hellohublot, hublot@aliyun.com
