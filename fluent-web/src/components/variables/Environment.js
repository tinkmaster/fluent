import React, {Component} from "react";
import './css/Environment.css'
import {Col, List, Button, Row, Popconfirm, Form, Input, Space, Modal, Alert, Spin} from "antd";
import {PlusOutlined, DeleteOutlined, MinusCircleOutlined} from "@ant-design/icons";
const tailLayout = {
    wrapperCol: {offset: 16, span: 16},
};

const layout = {
    labelCol: {span: 7},
    wrapperCol: {offset:2, span: 13},
};
const onFinishFailed = errorInfo => {
};


export default class Environment extends Component {
    onFinish = (values) => {
        let env = {}
        env['name'] = this.props.selectedEnv.name
        env['variables'] = {}
        if(values['variables']) {
            values['variables'].forEach(con => {
                env['variables'][con['varName'].trim()] = con['value'].trim()
            })
        }
        this.props.postEnv(env)
    }

    componentDidUpdate() {

    }

    render() {
        let envVarsInitialValue=[]
        if (this.props.selectedEnv && this.props.selectedEnv.variables) {
            Object.keys(this.props.selectedEnv.variables).forEach(key => {
                envVarsInitialValue = envVarsInitialValue.concat(
                {
                    varName: key,
                    value: this.props.selectedEnv.variables[key]
                })
           })
        }
        return (
            <div className='environment-form'>
                <List style={{backgroundColor: 'white', height: '100%', width: '40%', overflow: 'auto'}}
                    header={<div className="environment-list-title">
                        <Row style={{'width': '100%', textAlign: 'center'}}>
                            <Col>
                                <span>Environments</span>
                            </Col>
                            <Button onClick={
                                () => this.props.updatePageState('environmentDetailsVisible', true
                            )}
                            style={{'marginLeft': 'auto'}}><PlusOutlined/></Button>
                        </Row>
                        </div>}
                        dataSource={this.props.envsList}
                        renderItem={item => (
                            <List.Item
                                style={{
                                    backgroundColor: this.props.selectedEnv ?
                                        this.props.selectedEnv.name === item ?
                                            "#e1e1e1" : null
                                        : null,
                                    paddingLeft: 24,
                                    paddingRight: 24
                                }}
                                className='environment-list-item'
                                onClick={() => {
                                    this.setState({loading: true})
                                    this.props.fetchSpecifiedEnv(item)
                                }}
                            >
                            <span>{item}</span>
                            <Popconfirm
                                placement="bottomRight"
                                title={'Are you sure to delete this operator?'}
                                onConfirm={() => this.props.deleteEnv(item)}
                                okText="Yes"
                                cancelText="No"
                                style={{backgroundColor: 'white', color: 'white'}}
                            >
                                <DeleteOutlined className="environment-list-item-icon" style={{'paddingLeft': 18,}}/>
                            </Popconfirm>
                        </List.Item>
                    )}/>
                {this.props.selectedEnv ? 
                <div className='env-var-form'>
                    <h1>Environment Varibales</h1>
                    {this.props.envFormLoading ? 
                    <Spin spinning={true}></Spin> :
                    <Form style={{paddingTop: '18px'}} name="env_vars_form"  onFinish={this.onFinish} autoComplete="off" >
                        <Form.List name="variables" initialValue={envVarsInitialValue}>
                            {(fields, { add, remove }) => {
                                return (
                            <>
                                {fields.map(field => (
                                    <div key={field.key} style={{display: 'flex'}}>
                                        <Space key={field.key} style={{ width: '95%', display: 'flex', marginBottom: 8 }} align="baseline">
                                            <Form.Item
                                            {...field}
                                            name={[field.name, 'varName']}
                                            fieldKey={[field.fieldKey, 'varName']}
                                            rules={[{ required: true, message: 'Missing variable name' }]}
                                            >
                                            <Input placeholder="Variable Name" />
                                            </Form.Item>
                                            <Form.Item
                                            {...field}
                                            name={[field.name, 'value']}
                                            fieldKey={[field.fieldKey, 'value']}
                                            rules={[{ required: true, message: 'Missing value' }]}
                                            >
                                            <Input placeholder="Value" />
                                            </Form.Item>
                                        </Space>
                                        <MinusCircleOutlined style={{padding: '8px', marginLeft: '8px'}}onClick={() => remove(field.name)} />
                                    </div>
                                ))}
                                <Form.Item>
                                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                                    Add environment variable
                                </Button>
                                </Form.Item>
                            </>
                            )}}
                        </Form.List>
                        <Form.Item>
                            <Button style = {{float: 'right', width: '100px'}}type="primary" htmlType="submit">
                            Save
                            </Button>
                        </Form.Item>
                    </Form>}
                </div> : 
                <div className='env-var-form'>
                    { this.props.envsList && this.props.envsList.length > 0?
                    <Alert style={{textAlign: 'center', margin: 'auto'}} message="Please select the environment in the left siderbar" type="success" />
                    :
                    <Alert style={{textAlign: 'center', margin: 'auto'}} message="Please create environment variable first" type="success" />}
                </div>}
            </div>
        )
    }
}

export class EnvironmentDetails extends Component {
    onFinish = (values) => {
        // reorganize the data
        let body = {}
        body['name'] = values.name
        this.props.postEnv(body);
        this.props.updatePageState('environmentDetailsVisible', false, "selectedEnv", null)
    }

    onCancel = () => {
        this.props.updatePageState('environmentDetailsVisible', false)
    }

    render() {
        return (
            <Modal
                title="PipelineDetails"
                visible={this.props.visible}
                footer={null}
                onCancel={this.onCancel}
                destroyOnClose={true}
            >
                <Form
                    preserve={false}
                    {...layout}
                    name="basic"
                    onFinish={this.onFinish}
                    onFinishFailed={onFinishFailed}
                >
                    <Form.Item
                        label="Environment Name"
                        name="name"
                        rules={[{required: true, message: 'Please input your environment name!'}]}
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item {...tailLayout}>
                        <div>
                            <Button style={{"marginRight": 8}}
                                    onClick={this.onCancel}>Cancel
                            </Button>
                            <Button type="primary" htmlType="submit">
                                Save
                            </Button>
                        </div>
                    </Form.Item>
                </Form>
            </Modal>
        );
    }
}