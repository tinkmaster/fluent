import './VariablePage.css';
import React from "react";
import {Col, Row} from "antd";
import Environment, { EnvironmentDetails } from '../../components/variables/Environment';
import Global from '../../components/variables/Global';
import Secret , {SecretDetails} from '../../components/variables/Secret';

export class VariablePage extends React.Component {
    componentDidMount() {
        this.props.fetchEnvsList()
        this.props.fetchGlobal()
        this.props.fetchSecretsList()
    }

    onFinish = values => {
        console.log('Received values of form:', values);
    };

    render() {
        return (
            <div className='variable-page'>
                <Row gutter={24} style={{height: '100%', background: '#f0f2f5'}}>
                    <Col span={14}>
                        <Environment 
                            envsList={this.props.envsList}
                            selectedEnv={this.props.selectedEnv}
                            fetchSpecifiedEnv={this.props.fetchSpecifiedEnv}
                            postEnv={this.props.postEnv}
                            deleteEnv={this.props.deleteEnv}
                            updatePageState={this.props.updateVariablePageState}
                            envFormLoading={this.props.envFormLoading}
                        />
                    </Col>
                    <Col span={10} style={{ 
                        display: 'flex', 
                        flexDirection: 'column', 
                        height: 'calc(100vh - 120px)', 
                        background: 'white',
                        paddingLeft: '10px'}}>
                        <Global
                            globalVariable={this.props.globalVariable}
                            postGlobal={this.props.postGlobal}
                            globalFormLoading={this.props.globalFormLoading}
                        />
                        <Secret
                            secretsList={this.props.secretsList}
                            deleteSecret={this.props.deleteSecret}
                            updatePageState={this.props.updateVariablePageState}
                        />
                    </Col>
                </Row>
                <EnvironmentDetails
                    visible={this.props.environmentDetailsVisible}
                    postEnv={this.props.postEnv}
                    updatePageState={this.props.updateVariablePageState}
                />
                <SecretDetails
                    visible={this.props.secretDetailsVisible}
                    postSecret={this.props.postSecret}
                    updatePageState={this.props.updateVariablePageState}
                />
            </div>
        )
    }
}