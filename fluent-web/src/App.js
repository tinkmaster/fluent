import './App.css';
import {Layout, Menu} from 'antd';
import {Link, Route,} from 'react-router-dom';
import React from "react";
import ExecutionContainer from "./pages/pipelines/PipelinesPageReduxContainer";
import VariablePageContainer from './pages/envs/VariablePageReduxContainer';
import WikiPage from './pages/wiki/WikiPage';

const {Header, Content, Footer} = Layout;

function App() {
    document.title = "Fluent Web"
    return (
        <Layout>
            <Header style={{position: 'fixed', zIndex: 1, width: '100%'}}>
                <Menu theme="dark" mode="horizontal">
                    <Menu.Item key="pipelines">
                        <Link to={'/pipelines'}>Pipelines</Link>
                    </Menu.Item>
                    <Menu.Item key="variables">
                        <Link to={'/variables'}>Variables</Link>
                    </Menu.Item>
                    <Menu.Item key="wiki">
                        <Link to={'/wiki'}>Wiki</Link>
                    </Menu.Item>
                </Menu>
            </Header>
            <Content className="site-layout" style={{padding: '0 18px', paddingTop: 80}}>
                <div className="site-layout-background" style={{minHeight: 600}}>
                    <Route exact={true} path="/pipelines" component={ExecutionContainer}/>
                    <Route exact={true} path='/variables' component={VariablePageContainer}/>
                    <Route exact={false} path='/wiki' component={WikiPage}/>
                </div>
            </Content>
            <Footer style={{textAlign: 'center', padding: '9px 0px'}}>Fluent©2020 Created by Tink Master Tech</Footer>
        </Layout>
    );
}

export default App;
