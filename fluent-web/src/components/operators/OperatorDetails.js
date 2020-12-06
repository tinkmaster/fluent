import React, {Component} from 'react';
import {Button, Form, Input, Modal, Select} from "antd";

const tailLayout = {
    wrapperCol: {offset: 16, span: 16},
};

const layout = {
    labelCol: {span: 6},
    wrapperCol: {span: 17},
};


const onFinishFailed = errorInfo => {
    console.log('Failed:', errorInfo);
};

export class OperatorDetails extends Component {
    constructor(props, context) {
        super(props, context);
        this.state = {
            isHttpType: this.props.selectedOperator ? 'HTTP_REQUEST' === this.props.selectedOperator.type : false
        }
    }

    onFinish = (values) => {
        // reorganize the data
        let data = {}
        data['name'] = values['name'].trim()
        data['type'] = values['type'].trim()
        data['params'] = {}
        Object.keys(values).filter(k => k !== 'name' && k !== 'type').map(k => {
            if (values[k]) {
                values[k] = values[k].trim()
            }
            data['params'][k] = values[k]
        })
        console.log('Success:', data);
        this.props.postOperator(Object.assign({}, data));
        this.props.updateState('operatorDetailsVisible', false, "selectedOperator", null)
    }

    onCancel = () => {
        this.setState({isHttpType: false})
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
                        rules={[{required: true, message: 'Please input your username!'}]}
                        initialValue={this.props.selectedOperator ? this.props.selectedOperator.name : null}
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item
                        label="Operator Type"
                        name="type"
                        rules={[{required: true, message: 'Please select operator type!'}]}
                        initialValue={this.props.selectedOperator ? this.props.selectedOperator.type : null}
                    >
                        <Select onChange={(value) => {
                            this.setState({isHttpType: 'HTTP_REQUEST' === value})
                        }}>
                            <Select.Option value="HTTP_REQUEST">HTTP_REQUEST</Select.Option>
                            <Select.Option value="DATA_VALIDATION">DATA_VALIDATION</Select.Option>
                        </Select>
                    </Form.Item>
                    {(this.props.selectedOperator && 'HTTP_REQUEST' === this.props.selectedOperator.type) || this.state.isHttpType ?
                        <HttpParameters
                            params={this.props.selectedOperator ? this.props.selectedOperator.params : null}/> : ''}
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
                    rules={[{required: true, message: 'Please enter url'}]}
                    initialValue={this.props.params ? this.props.params.uri : null}
                >
                    <Input placeholder="https://google.com"/>
                </Form.Item>
                <Form.Item
                    label="HTTP Path"
                    name="path"
                    rules={[{required: true, message: 'Please enter the path'}]}
                    initialValue={this.props.params ? this.props.params.path : null}
                >
                    <Input placeholder="/api/v1/some"/>
                </Form.Item>
                <Form.Item
                    label="HTTP Method"
                    name="method"
                    rules={[{required: true, message: 'Please select http method'}]}
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
                    <Input.TextArea placeholder="key1: value1&#13;key2: value2" autoSize={true}/>
                </Form.Item>
                <Form.Item
                    label="RequestBody"
                    name="body"
                    initialValue={this.props.params ? this.props.params.body : null}

                >
                    <Input.TextArea placeholder="{ key: value }" autoSize={true}/>
                </Form.Item>
            </div>
        )
    }
}
