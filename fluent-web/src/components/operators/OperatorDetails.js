import React, { Component } from 'react';
import { Button, Form, Input, Modal, Row, Col, Select, Space } from "antd";
import PlusOutlined from "@ant-design/icons/lib/icons/PlusOutlined";
import MinusCircleOutlined from "@ant-design/icons/lib/icons/MinusCircleOutlined";
import './css/OperatorDetails.css'

const tailLayout = {
    wrapperCol: { offset: 19, span: 5 },
};

const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 22 },
};

const paramsKey=['method', 'uri', 'path', 'headers', 'body']


const onFinishFailed = errorInfo => {
};

export class OperatorDetails extends Component {
    constructor(props, context) {
        super(props, context);
        this.state = {
            typeChanged: false,
            isHttpType: this.props.selectedOperator ? 'HTTP_REQUEST' === this.props.selectedOperator.type : false,
            isDataValidationType: this.props.selectedOperator ?
                'DATA_VALIDATION' === this.props.selectedOperator.type : false
        }
        this.typeChanged = false;
    }

    onFinish = (values) => {
        // reorganize the data
        let data = {}
        data['name'] = values['name'].trim()
        data['type'] = values['type'].trim()
        data['params'] = {}
        Object.keys(values).filter(k => k !== 'name' && k !== 'type' && k != 'conditions').map(k => {
            if (values[k]) {
                values[k] = values[k].trim()
            }
            data['params'][k] = values[k]
        })
        if(values['conditions']) {
            values['conditions'].forEach(con => {
                data['params'][con['variable'].trim()] = 'equals,' + con['condition'].trim()
            })
        }
        this.props.postOperator(Object.assign({}, data));
        this.props.updateState('operatorDetailsVisible', false, "selectedOperator", null)
    }

    onCancel = () => {
        this.setState({ typeChanged: false, isHttpType: false, isDataValidationType: false })
        this.props.updateState('operatorDetailsVisible', false, "selectedOperator", null)
    }


    render() {
        return (
            <Modal
                title="OperatorDetails"
                visible={this.props.visible}
                footer={null}
                onCancel={this.onCancel}
                destroyOnClose={true}
                width={1200}
            >
                <Form
                    preserve={false}
                    {...layout}
                    name="basic"
                    onFinish={this.onFinish}
                    onFinishFailed={onFinishFailed}
                >
                    <Form.Item
                        label="Operator Name"
                        name="name"
                        rules={[{ required: true, message: 'Please input your username!' }]}
                        initialValue={this.props.selectedOperator ? this.props.selectedOperator.name : null}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item
                        label="Operator Type"
                        name="type"
                        rules={[{ required: true, message: 'Please select operator type!' }]}
                        initialValue={this.props.selectedOperator ? this.props.selectedOperator.type : null}
                    >
                        <Select onChange={(value) => {
                            this.setState({ typeChanged:true, isHttpType: 'HTTP_REQUEST' === value, isDataValidationType: 'DATA_VALIDATION' === value })
                        }}>
                            <Select.Option value="HTTP_REQUEST">HTTP_REQUEST</Select.Option>
                            <Select.Option value="DATA_VALIDATION">DATA_VALIDATION</Select.Option>
                        </Select>
                    </Form.Item>
                    {this.state.isHttpType || (this.props.selectedOperator && 'HTTP_REQUEST' === this.props.selectedOperator.type && !this.state.typeChanged) ?
                        <HttpParameters
                            params={this.props.selectedOperator ? this.props.selectedOperator.params : null} /> : ''}
                    {this.state.isDataValidationType || (this.props.selectedOperator && 'DATA_VALIDATION' === this.props.selectedOperator.type && !this.state.typeChanged) ? 
                            <DataValidation
                            params={this.props.selectedOperator ? this.props.selectedOperator.params : null} /> : ''}
                    <Form.Item {...tailLayout}>
                        <div>
                            <Button style={{ "marginRight": 8, width: '100px' }}
                                onClick={this.onCancel}>Cancel
                            </Button>
                            <Button style={{width: '100px' }} type="primary" htmlType="submit">
                                Save
                            </Button>
                        </div>
                    </Form.Item>
                </Form>
            </Modal>
        )
    }
}

class HttpParameters extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
            <div>
                <Form.Item
                    label="HTTP URI"
                    name="uri"
                    rules={[{ required: true, message: 'Please enter url' }]}
                    initialValue={this.props.params ? this.props.params.uri : null}
                >
                    <Input placeholder="https://google.com" />
                </Form.Item>
                <Form.Item
                    label="HTTP Path"
                    name="path"
                    rules={[{ required: true, message: 'Please enter the path' }]}
                    initialValue={this.props.params ? this.props.params.path : null}
                >
                    <Input placeholder="/api/v1/some" />
                </Form.Item>
                <Form.Item
                    label="HTTP Method"
                    name="method"
                    rules={[{ required: true, message: 'Please select http method' }]}
                    initialValue={this.props.params ? this.props.params.method : null}
                >
                    <Select>
                        <Select.Option value="GET">GET</Select.Option>
                        <Select.Option value="POST">POST</Select.Option>
                        <Select.Option value="PUT">PUT</Select.Option>
                        <Select.Option value="DELETE">DELETE</Select.Option>
                        <Select.Option value="OPTION">OPTION</Select.Option>
                        <Select.Option value="TRACE">TRACE</Select.Option>
                    </Select>
                </Form.Item>
                <Form.Item
                    label="HTTP Headers"
                    name="headers"
                    initialValue={this.props.params ? this.props.params.headers : null}

                >
                    <Input.TextArea placeholder="key1: value1&#13;key2: value2" autoSize={true} />
                </Form.Item>
                <Form.Item
                    label="RequestBody"
                    name="body"
                    initialValue={this.props.params ? this.props.params.body : null}

                >
                    <Input.TextArea placeholder="{ key: value }" autoSize={true} />
                </Form.Item>
            </div>
        )
    }
}

class DataValidation extends Component {

    constructor(props, context) {
        super(props, context);
    }

    notInKeys(name) {
        let result = false;
        paramsKey.forEach(k => {if (k === name) {result = true;}})
        return result;
    }

    render() {
        let dvInitialValue=[]
        if (this.props.params) {
            Object.keys(this.props.params).filter(key => !this.notInKeys(key)).map(key => {
            dvInitialValue = dvInitialValue.concat(
                {
                    variable: key,
                    condition: this.props.params[key].substring(this.props.params[key].indexOf(',') + 1)
                })
           })
        }
        return (
            <Form.List name="conditions" initialValue={dvInitialValue}>
                {(fields, { add, remove }) => (
                    <div>
                        <Row>
                            <Col span={6}>
                                <span style={{paddingRight: 9, float: 'right'}}>Conditions:</span>
                            </Col>
                            <Col className='dv-condition-form' span={18}>
                                {fields.map(field => (
                                    <div key={field.key} style={{display: 'flex'}}>
                                        <Space style={{ width: '90%', display: 'flex', marginBottom: 8 }} align="baseline">
                                            <Form.Item
                                                label="variable"
                                                {...field}
                                                name={[field.name, 'variable']}
                                                fieldKey={[field.fieldKey, 'variable']}
                                                rules={[{ required: true, message: 'Missing variable name' }]}
                                                style={{marginBottom: 0, width: '100%'}}
                                            >
                                                <Input placeholder="Variable name to be check" />
                                            </Form.Item>
                                            <Form.Item
                                                label="expression"
                                                {...field}
                                                name={[field.name, 'condition']}
                                                fieldKey={[field.fieldKey, 'condition']}
                                                rules={[{ required: true, message: 'Missing check condition' }]}
                                            >
                                                <Input placeholder="Check Condition" />
                                            </Form.Item>
                                        </Space>
                                        <MinusCircleOutlined onClick={() => remove(field.name)} />
                                    </div>
                                ))}
                                <Form.Item style={{width: '100%'}}>
                                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                                        Add Check Condition
                                    </Button>
                                </Form.Item>
                            </Col>
                        </Row>
                    </div>
                )}
            </Form.List>
        )
    }
}
