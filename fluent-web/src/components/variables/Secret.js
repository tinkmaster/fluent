import React, {Component} from "react";
import './css/Secret.css'
import {Button, Row, List, Form, Input, Modal, Popconfirm} from "antd";
import {PlusOutlined, DeleteOutlined} from "@ant-design/icons";
const tailLayout = {
    wrapperCol: {offset: 16, span: 16},
};

const layout = {
    labelCol: {span: 7},
    wrapperCol: {offset:2, span: 13},
};
export default class Secret extends Component {
    render() {
        return (
            <div className='secret-var-form'>
                <Row>
                    <h1>Secrets</h1>
                    <Button onClick={
                                () => this.props.updatePageState('secretDetailsVisible', true
                            )}
                            style={{'marginLeft': 'auto'}}><PlusOutlined/></Button>
                </Row>
                <List
                    itemLayout="horizontal"
                    dataSource={this.props.secretsList}
                    renderItem={item => (
                    <List.Item 
                        className="secret-list-item"
                        actions={[
                            <Popconfirm
                                placement="bottomRight"
                                title={'Are you sure to delete this secret?'}
                                onConfirm={() => this.props.deleteSecret(item)}
                                okText="Yes"
                                cancelText="No"
                                style={{backgroundColor: 'white', color: 'white'}}
                            >
                                <DeleteOutlined className="secret-list-item-icon"/>
                            </Popconfirm>
                        ]}
                    >
                        <span style={{paddingLeft: '18px'}}>{item}</span>
                    </List.Item>
                    )}
                />
            </div>
        )
    }
}

export class SecretDetails extends Component {
    onFinish = (values) => {
        // reorganize the data
        let body = {}
        body['name'] = values.name
        body['value'] = values.value
        this.props.postSecret(body);
        this.props.updatePageState('secretDetailsVisible', false)
    }

    onCancel = () => {
        this.props.updatePageState('secretDetailsVisible', false)
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
                >
                    <Form.Item
                        label="Secret Name"
                        name="name"
                        rules={[{required: true, message: 'Please input your secret name!'}]}
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item
                        label="Secret Value"
                        name="value"
                        rules={[{required: true, message: 'Please input your secret value!'}]}
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
        )
    }
}