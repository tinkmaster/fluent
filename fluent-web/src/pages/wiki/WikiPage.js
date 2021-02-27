import './WikiPage.css';
import React, {Component} from "react";
import { Menu } from 'antd';
import ReactMarkdown from 'react-markdown'
import gfm from 'remark-gfm'
import pipelineIntroductionMd from './markdown/pipelines/Introduction.md'
import pipelineStagesMd from './markdown/pipelines/Stages.md'
import pipelineEnvMd from './markdown/pipelines/Env.md'
import pipelineInnerParametersMd from './markdown/pipelines/InnerParameters.md'
import pipelineHowToUseMd from './markdown/pipelines/HowToUse.md'

import operatorIntroductionMd from './markdown/operators/Introduction.md'
import operatorHttpRequestionMd from './markdown/operators/types/Http_Request.md'
import operatorDataValidationMd from './markdown/operators/types/Data_Validation.md'

import variablesIntroductionMd from './markdown/variables/Introduction.md'
import variablesEnvsMd from './markdown/variables/Environments.md'
import variablesGlobalsMd from './markdown/variables/Globals.md'
import variablesSecretsMd from './markdown/variables/Secrets.md'
import variablesVRMd from './markdown/VariableReferrence.md'

import CodeBlock from "./CodeBlock";
import { Route } from 'react-router';
import { Link } from 'react-router-dom';

const { SubMenu } = Menu;

export default class WikiPage extends Component {
    handleClick = e => {
        console.log('click ', e);
    };

    getPathKey() {
        return this.props.location.pathname.replace('/wiki', '')
    }

    getOpenKey() {
        let result = []
        let path = this.getPathKey().substring(1)
        let str = ''
        path.split('/').forEach(p => {
            str = str + '/' + p
            result = result.concat(str)
        })
        return result
    }

    // All menu leaves must be Menu.Item type to easily get open keys when open this webpage
    render() {
        console.log(this.getOpenKey())
        return (
            <div className='wiki-page' style={{height: '100%', width: '100%', display: 'flex', flexDirection: 'row'}}>
                <div style={{height: 'calc(100vh - 120px)', width: '288'}}>
                    <h1 style={{width: 288, margin: '0 0 0 0', padding: '16px 24px 16px 24px', borderRight: '1px solid #e4e3e3', borderBottom: '1px dashed #e4e3e3'}}>Category</h1>
                    <Menu
                        onClick={this.handleClick}
                        style={{ width: 288, height: '91%', overflowY: 'auto', overflowX: 'hidden'}}
                        selectedKeys={[this.getPathKey()]}
                        defaultOpenKeys={this.getOpenKey()}
                        mode="inline"
                    >
                        <SubMenu key="/pipelines" title="Pipelines">
                                <Menu.Item key="/pipelines/introduction">
                                    <Link to="/wiki/pipelines/introduction" rel="noopener noreferrer">
                                        Introduction
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/pipelines/stages">
                                    <Link to="/wiki/pipelines/stages" rel="noopener noreferrer">
                                        Stages
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/pipelines/env">
                                    <Link to="/wiki/pipelines/env" rel="noopener noreferrer">
                                        Env
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/pipelines/innerparameters">
                                    <Link to="/wiki/pipelines/innerparameters" rel="noopener noreferrer">
                                        Inner Parameters
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/pipelines/usage">
                                    <Link to="/wiki/pipelines/usage" rel="noopener noreferrer">
                                        How to use
                                    </Link>
                                    </Menu.Item>
                            
                        </SubMenu>
                        <SubMenu key="/operators" title="Operatos">
                            <Menu.Item key="/operators/introduction">
                                <Link to="/wiki/operators/introduction" rel="noopener noreferrer">
                                    Introduction
                                </Link>
                            </Menu.Item>
                            <Menu.ItemGroup key="/operators/types" title="Types">
                                <Menu.Item key="/operators/types/http-request">
                                    <Link to="/wiki/operators/types/http-request" rel="noopener noreferrer">
                                        Http Request
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/operators/types/data-validation">
                                    <Link to="/wiki/operators/types/data-validation" rel="noopener noreferrer">
                                        Data Validation
                                    </Link>
                                </Menu.Item>
                            </Menu.ItemGroup>
                        </SubMenu>
                        <SubMenu key="/variables" title="Variables">
                            <Menu.Item key="/variables/introduction">
                                <Link to="/wiki/variables/introduction" rel="noopener noreferrer">
                                    Introduction
                                </Link>
                            </Menu.Item>
                            
                            <Menu.ItemGroup key="/variables/types" title="Types">
                                <Menu.Item key="/variables/envs">
                                    <Link to="/wiki/variables/envs" rel="noopener noreferrer">
                                        Environment
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/variables/globals">
                                    <Link to="/wiki/variables/globals" rel="noopener noreferrer">
                                        Global
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/variables/secrets">
                                    <Link to="/wiki/variables/secrets" rel="noopener noreferrer">
                                        Secrets
                                    </Link>
                                </Menu.Item>
                            </Menu.ItemGroup>
                        </SubMenu>
                        <Menu.Item key="/variables-referrence">
                                <Link to="/wiki/variables-referrence" rel="noopener noreferrer">
                                    Variable Referrence
                                </Link>
                            </Menu.Item>
                    </Menu>
                </div>
                <div style={{width: '100%', height: 'calc(100vh - 120px)', overflow: 'auto', padding: '24px 48px 24px 48px'}}>
                    <Route exact path='/wiki' component={() => <FluentMarkdown mdfile={pipelineInnerParametersMd}/>} />
                    <Route exact path='/wiki/pipelines/introduction' component={() => <FluentMarkdown mdfile={pipelineIntroductionMd}/>} />
                    <Route exact path='/wiki/pipelines/stages' component={() => <FluentMarkdown mdfile={pipelineStagesMd}/>} />
                    <Route exact path='/wiki/pipelines/env' component={() => <FluentMarkdown mdfile={pipelineEnvMd}/>} />
                    <Route exact path='/wiki/pipelines/innerParameters' component={() => <FluentMarkdown mdfile={pipelineInnerParametersMd}/>} />
                    <Route exact path='/wiki/pipelines/usage' component={() => <FluentMarkdown mdfile={pipelineHowToUseMd}/>} />
                    
                    <Route exact path='/wiki/operators/introduction' component={() => <FluentMarkdown mdfile={operatorIntroductionMd}/>} />
                    <Route exact path='/wiki/operators/types/http-request' component={() => <FluentMarkdown mdfile={operatorHttpRequestionMd}/>} />
                    <Route exact path='/wiki/operators/types/data-validation' component={() => <FluentMarkdown mdfile={operatorDataValidationMd}/>} />

                    <Route exact path='/wiki/variables/introduction' component={() => <FluentMarkdown mdfile={variablesIntroductionMd}/>} />
                    <Route exact path='/wiki/variables/envs' component={() => <FluentMarkdown mdfile={variablesEnvsMd}/>} />
                    <Route exact path='/wiki/variables/globals' component={() => <FluentMarkdown mdfile={variablesGlobalsMd}/>} />
                    <Route exact path='/wiki/variables/secrets' component={() => <FluentMarkdown mdfile={variablesSecretsMd}/>} />

                    <Route exact path='/wiki/variables-referrence' component={() => <FluentMarkdown mdfile={variablesVRMd}/>} />
                </div>
            </div>
        )
        //all the icon is provided in here: https://fjc0k.github.io/ant-design-icons/
    }
}

export class FluentMarkdown extends Component {
    render() {
        return (
            <ReactMarkdown 
                source={this.props.mdfile} 
                renderers={{
                    code: CodeBlock
                }}
                escapeHtml={false}
                plugins={[gfm]}>
            </ReactMarkdown>
        )
    }
}