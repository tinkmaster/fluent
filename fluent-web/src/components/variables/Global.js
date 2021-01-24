import React, {Component} from "react";
import './css/Global.css'
import {Button, Form, Input, Space, Spin} from "antd";
import {PlusOutlined, MinusCircleOutlined} from "@ant-design/icons";

export default class Global extends Component {
    onFinish = (values) => {
        let global = {}
        global['variables'] = {}
        if(values['globals']) {
            values['globals'].forEach(con => {
                global['variables'][con['varName'].trim()] = con['value'].trim()
            })
        }
        this.props.postGlobal(global)
    }

    render() {
        let globalVarsInitialValue=[]
        if (this.props.globalVariable && this.props.globalVariable.variables) {
            Object.keys(this.props.globalVariable.variables).forEach(key => {
                globalVarsInitialValue = globalVarsInitialValue.concat(
                {
                    varName: key,
                    value: this.props.globalVariable.variables[key]
                })
           })
        }
        return (
            <div className='global-variables-form' >
                <h1>Global</h1>
                {this.props.globalFormLoading ?
                <Spin spinning={true}></Spin> :
                <Form name="global_vars_form" className='global-var-form' onFinish={this.onFinish} autoComplete="off">
                    <Form.List name="globals" initialValue={globalVarsInitialValue}>
                        {(fields, { add, remove }) => (
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
                                Add global variable
                            </Button>
                            </Form.Item>
                        </>
                        )}
                    </Form.List>
                    <Form.Item>
                        <Button style = {{float: 'right', width: '100px'}}type="primary" htmlType="submit">
                        Submit
                        </Button>
                    </Form.Item>
                </Form>}
            </div>
        )
    }
}