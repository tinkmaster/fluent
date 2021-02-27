import React, { Component } from "react";
import './css/PipelineGraph.css'
import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {addEdge, Controls, isEdge, isNode, MiniMap, removeElements} from "react-flow-renderer";
import {Button, Form, Space, Input, Select, Spin, PageHeader} from "antd";
import {edgeTypes} from "./DataFlowEdgeType";
import Modal from "antd/lib/modal/Modal";
import { MinusCircleOutlined, PlusCircleTwoTone } from "@ant-design/icons";
const { Option } = Select;
const tailLayout = {
    wrapperCol: { offset: 19, span: 5 },
};

const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 22 },
};

export class PipelineGraph extends React.Component {
    constructor(props, context) {
        super(props, context);
        this.graphInstance = undefined
        this.lastSelectedPipelineName = undefined
    }
    
    onConnect = (params) => {
        params['type'] = 'custom'
        params['arrowHeadType'] = 'arrowclosed'
        this.props.updateGraph(addEdge(params, this.props.pipelineData))
    }
    onRemove = (params) => {
        params.forEach(v => v['id'] = v['id'] + '');
        params.forEach( v=> {if (v['source']) {v['id'] = v['source'] + '->' + v['target']}})
        this.props.updateGraph(removeElements(params, this.props.pipelineData))
    }

    saveDiagram = () => {
        let obj = {
            name: this.props.selectedPipeline.name,
            operators: {},
            connections: [],
            environment: this.props.pipelineSelectedEnv ? this.props.pipelineSelectedEnv : this.props.selectedPipeline.environment,
            parameters: this.props.selectedPipeline.parameters
        }
        if (this.props.pipelineSelectedEnv === '') {
            obj['environment'] = null
        }
        this.props.pipelineData.filter(v => isNode(v)).map(v => {
            obj.operators[v['id']] = v.data.name;
        });
        this.props.pipelineData.filter(v => isEdge(v)).map(v => obj.connections.push(v.source + '->' + v.target));
        this.props.updatePipeline(this.props.selectedPipeline.name, Object.assign({}, obj));
    }

    runDiagram = () => {
        this.saveDiagram()
        let obj = {
            name: this.props.selectedPipeline.name,
            pipelineName: this.props.selectedPipeline.name,
            operators: {},
            connections: [],
            environment: this.props.pipelineSelectedEnv ? (this.props.pipelineSelectedEnv === '' ? null : this.props.pipelineSelectedEnv) : this.props.selectedPipeline.environment,
            parameters: this.props.selectedPipeline.parameters
        }
        this.props.pipelineData.filter(v => isNode(v)).map(v => {
            obj.operators[v['id']] = v.data.name
        });
        this.props.pipelineData.filter(v => isEdge(v)).map(v => obj.connections.push(v.source + '->' + v.target));
        this.props.runPipeline(Object.assign({}, obj));
    }

    onLoad = (reactFlowInstance) => {
        if (!this.graphInstance) {
            this.graphInstance = reactFlowInstance;
        }
        if (this.props.selectedPipeline) {
            this.lastSelectedPipelineName = this.props.selectedPipeline.name
        }
    };

    handleChange = (value) => {
        if(value === undefined) {
            this.props.updatePipelinePageState('pipelineSelectedEnv', '')
        } else {
            this.props.updatePipelinePageState('pipelineSelectedEnv', value)
        }
    }

    render() {
        return (
            <div style={{height:'100%'}}>
                <div className='pipeline-graph-page-header'>
                    <PageHeader
                        title="Pipeline Graph"
                        extra={[
                            <div style={{display: 'flex'}}>
                                <h3 style={{paddingLeft: '8px', margin: 'auto'}}>Env:</h3>
                                 {this.props.envSelectLoading?
                                    <div style={{marginLeft: '8px', width: 130}}>
                                        <Spin style={{margin: 'auto', paddingLeft: 64, paddingTop: 8 }}/>
                                    </div> : 
                                    <Select 
                                         allowClear
                                         defaultValue={this.props.selectedPipeline.environment}
                                         placeholder="Choose Env"
                                         style={{ width: 130, marginLeft: 8 }}
                                         onChange={this.handleChange}
                                         >
                                         {this.props.envsList ? 
                                             this.props.envsList.map(opt => (<Option key={opt} value={opt}>{opt}</Option>)): 
                                        ''}
                                    </Select>}
                                <Button style={{marginLeft: '8px'}} key="params" type="primary" 
                                    onClick={() => this.props.updatePipelinePageState('pipelineParamsFormVisible', true)}>Parameters</Button>
                                <Button style={{marginLeft: '8px'}} key="save" type="primary" onClick={this.saveDiagram}>Save</Button>
                                <Button style={{marginLeft: '8px'}} key="run" danger onClick={this.runDiagram}>Run</Button>
                            </div>
                        ]}>
                    </PageHeader>
                </div>
                <div style={{height:'88%', width: '100%'}}>
                    <ReactFlow
                        elements={this.props.pipelineData}
                        nodeTypes={nodeTypes}
                        edgeTypes={edgeTypes}
                        nodesDraggable={true}
                        nodesConnectable={true}
                        onElementClick={this.onElementClick}
                        onConnect={this.onConnect}
                        onElementsRemove={this.onRemove}
                        defaultZoom={0.5}
                >
                        <MiniMap
                            nodeColor={(node) => {
                                switch (node.type) {
                                    case 'input':
                                        return 'red';
                                    case 'default':
                                        return '#00ff00';
                                    case 'output':
                                        return 'rgb(0,0,255)';
                                    default:
                                        return '#eee';
                                }
                            }}
                        />
                        <Controls/>
                    </ReactFlow>
                </div>
                <PipelineParametersFormModal
                    pipelineParamsFormVisible={this.props.pipelineParamsFormVisible}
                    updateState={this.props.updatePipelinePageState}
                    updatePipeline={this.props.updatePipeline}
                    selectedPipeline={this.props.selectedPipeline}
                />
            </div>
        )
    }
}

export class PipelineParametersFormModal extends Component{

    onCancel = () => {
        this.props.updateState('pipelineParamsFormVisible', false)
    }

    onFinish = (values) => {
        let obj = Object.assign({}, this.props.selectedPipeline)
        obj['parameters'] = {}
        values['pipelineParams'].forEach(v => {
            obj['parameters'][v['varName'].trim()] = v['value']
        })
        this.props.updatePipeline(this.props.selectedPipeline.name, Object.assign({}, obj))
        this.props.updateState('pipelineParamsFormVisible', false)
    }

    onFinishFailed = errorInfo => {
    };

    render() {
        let pipelineParams = []
        if (this.props.selectedPipeline.parameters) {
            Object.keys(this.props.selectedPipeline.parameters).forEach(v => {
                pipelineParams = pipelineParams.concat({
                    varName: v,
                    value: this.props.selectedPipeline.parameters[v]
                })
            })
        }

        return (
            <Modal
                title="Pipeline Parameters"
                visible={this.props.pipelineParamsFormVisible}
                footer={null}
                onCancel={this.onCancel}
                destroyOnClose={true}
                width={800}
                className='pipeline-parameter-modal'
            >
                <Form
                    preserve={false}
                    {...layout}
                    name="basic"
                    onFinish={this.onFinish}
                    onFinishFailed={this.onFinishFailed}
                >
                    <Form.List name="pipelineParams" initialValue={pipelineParams}>
                        {(fields, { add, remove }) => (
                            <div>
                            {fields.map(field => (
                                <div key={field.key} style={{display: 'flex'}}>
                                    <Space style={{ width: '90%', display: 'flex', marginBottom: 8 }} align="baseline">
                                        <Form.Item
                                            label="varName"
                                            {...field}
                                            name={[field.name, 'varName']}
                                            fieldKey={[field.fieldKey, 'varName']}
                                            rules={[{ required: true, message: 'Missing variable name' }]}
                                            style={{marginBottom: 0, width: '100%'}}
                                        >
                                            <Input placeholder="Variable name" />
                                        </Form.Item>
                                        <Form.Item
                                            label="value"
                                            {...field}
                                            name={[field.name, 'value']}
                                            fieldKey={[field.fieldKey, 'value']}
                                            rules={[{ required: true, message: 'Missing variable value' }]}
                                        >
                                            <Input placeholder="Variable value" />
                                        </Form.Item>
                                    </Space>
                                    <MinusCircleOutlined style={{marginLeft: '16px', marginTop: '8px'}}onClick={() => remove(field.name)} />
                                </div>
                            ))}
                            <Form.Item style={{width: '100%'}}>
                                <Button type="dashed" onClick={() => add()} block icon={<PlusCircleTwoTone />}>
                                    Add Check Condition
                                </Button>
                            </Form.Item>
                            </div>
                        )}
                    </Form.List>
                    <Form.Item {...tailLayout}>
                        <div style={{display: 'flex'}}>
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
