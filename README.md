## Features

- Pure native navigation for route
- Transition animation, clicking the navigation back button and clicking the bottom tab TabBar are all native events, and the performance is consistent with the native one
- Bind page parameters to HTRouteView, and will navigate to the page when clicked without javascript call
- Support presentModalController modal pop-up page, you can customize the pop-up page size and animation, etc.
- Support componentDidAppear componentDidDisappear lifecycle
- Supports AOP log collection each page
- The interface is same as `react-navigation`, which is convenient for switching

<img src="./example/1.gif" width="300">

## Usage

[Example](./example/app/App.js)

```bash
yarn add 'https://github.com/hellohublot/react-native-route.git'
```
#### iOS
```objective-c
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

	...

	[HTRouteBridgeManager loadBridgeWithURL:[self sourceURLForBridge:nil] moduleName:@"ReactNativeDemo" launchOptions:launchOptions];

	UITabBarController *tabBarController = [[UITabBarController alloc] init];
	tabBarController.tabBar.backgroundColor = [UIColor whiteColor];

	NSString *titleKey = @"title";
	NSString *imageKey = @"image";
	NSString *selectedImageKey = @"selectedImageKey";
	NSString *componentKey = @"component";
	NSArray *keyValueList = @[
		@{ titleKey: @"Home", imageKey: @"tabbar_home", selectedImageKey: @"tabbar_home_selected", componentKey: @"Home" },
		@{ titleKey: @"Mine", imageKey: @"tabbar_mine", selectedImageKey: @"tabbar_mine_selected", componentKey: @"Mine" },
	];
	[keyValueList enumerateObjectsUsingBlock:^(NSDictionary *dictionary, NSUInteger index, BOOL * _Nonnull stop) {
		HTRouteController *routeController = [HTRouteController controllerWithComponentName:dictionary[componentKey] componentRouteOptionList:@{@"id": [NSString stringWithFormat:@"%ld", index]}];
		UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:routeController];
		navigationController.fd_viewControllerBasedNavigationBarAppearanceEnabled = false;
		[tabBarController addChildViewController:navigationController];

		routeController.tabBarItem.title = dictionary[titleKey];
		routeController.tabBarItem.image = [[UIImage imageNamed:dictionary[imageKey]] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
		routeController.tabBarItem.selectedImage = [[UIImage imageNamed:dictionary[selectedImageKey]] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
	}];

	UIViewController *rootViewController = [[UIViewController alloc] init];
	rootViewController.view.backgroundColor = [UIColor whiteColor];
	[rootViewController addChildViewController:tabBarController];
	[rootViewController.view addSubview:tabBarController.view];
	[tabBarController didMoveToParentViewController:rootViewController];

	self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
	self.window.rootViewController = rootViewController;
	[self.window makeKeyAndVisible];
	return YES;
}
```
#### Android.MainApplication
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
			modelList.add(
			    new HTRouteTabBarModel("Mine", R.mipmap.tabbar_mine, R.mipmap.tabbar_mine_selected,
			        new HTRouteNavigationController(new HTRouteController("Mine", createComponentRouteOption())))
			);
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
### Android.res.value.style.xml
```xml
<style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
	// fullscreen
	<item name="android:windowTranslucentStatus">true</item>
</style>
```

#### App.js
```javascript
import { HTRouteManager, HTRouteComponent, HTRouteView, HTNavigationBar } from 'react-native-route'

HTRouteManager.register({
	'Home': () => require('~/page/Home').default,
	'Mine': () => require('~/page/Mine').default,
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

## Author

hellohublot, hublot@aliyun.com
