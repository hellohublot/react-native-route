- 纯原生的导航栏
- 转场动画, 点击导航返回按钮和点击最下面标签 TabBar 都是原生的事件, 性能做到和原生一致
- 给 HTRouteView 绑定页面参数, 当点击的时候直接原生响应跳转绑定的页面
- 支持 presentModalController 模态弹出页面 
- 留出来的接口和 react-navigation 一致, 方便切换

## Usage

[点击查看完整示例 Example](./example/App.js)

```bash
yarn add 'https://github.com/hellohublot/react-native-route.git'
```

```javascript
import { HTRouteManager, HTRouteComponent, HTRouteView } from 'react-native-route'

class First extends Component {

	render() {
		let block = () => {
			HTRouteManager.route('navigate', 'First')
		}
		let param = { 'hello': 'world', 'block': block }
		return <HTRouteView routeData={this.props.navigation.createRouteData('push', 'Second', param)} />
	}

}

class Second extends Component {

	render() {
		return <HTRouteView routeData={this.props.navigation.createRouteData('pop')} />
	}
	
}

HTRouteManager.register({
	First: () => require('./First').default,
	Second: () => require('./Second').default,
})

class App extends Component {
	render() {
		<HTRouteComponent 
			{...this.props} 
		/>
	}
}

```

## Author

hellohublot, hublot@aliyun.com
