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

export class PipelineDetails extends Component {

    constructor(props, context) {
        super(props, context);
    }

    onFinish = (values) => {
        // reorganize the data
        this.props.postPipeline(values);
        this.props.updateState('pipelineDetailsVisible', false, "selectedPipeline", null)
    }

    onCancel = () => {
        this.props.updateState('pipelineDetailsVisible', false, "selectedPipeline", null)
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
                        label="Pipeline Name"
                        name="name"
                        rules={[{required: true, message: 'Please input your pipeline name!'}]}
                        initialValue={this.props.selectedOperator ? this.props.selectedOperator.name : null}
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
