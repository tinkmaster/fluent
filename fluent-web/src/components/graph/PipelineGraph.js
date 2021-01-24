import React from "react";

import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {addEdge, Controls, isEdge, isNode, MiniMap, removeElements} from "react-flow-renderer";
import {Button, Col, Row, Select, Spin} from "antd";
import {edgeTypes} from "./DataFlowEdgeType";
const { Option } = Select;

export class PipelineGraph extends React.Component {
    constructor(props, context) {
        super(props, context);
    }

    onConnect = (params) => {
        params['type'] = 'custom'
        params['arrowHeadType'] = 'arrowclosed'
        this.props.updateGraph(addEdge(params, this.props.pipelineData))
    }
    onRemove = (params) => {
        this.props.updateGraph(removeElements(params, this.props.pipelineData))
    }

    saveDiagram = () => {
        let obj = {
            name: this.props.selectedPipeline.name,
            operators: {},
            connections: [],
            environment: this.props.pipelineSelectedEnv ? this.props.pipelineSelectedEnv : this.props.selectedPipeline.environment
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
            environment: this.props.pipelineSelectedEnv ? (this.props.pipelineSelectedEnv === '' ? null : this.props.pipelineSelectedEnv) : this.props.selectedPipeline.environment
        }
        this.props.pipelineData.filter(v => isNode(v)).map(v => {
            obj.operators[v['id']] = v.data.name
        });
        this.props.pipelineData.filter(v => isEdge(v)).map(v => obj.connections.push(v.source + '->' + v.target));
        this.props.runPipeline(Object.assign({}, obj));
    }

    handleChange = (value) => {
        console.log(value)
        if(value === undefined) {
            this.props.updatePipelinePageState('pipelineSelectedEnv', '')
        } else {
            this.props.updatePipelinePageState('pipelineSelectedEnv', value)
        }
    }

    render() {
        return (
            <ReactFlow
                elements={this.props.pipelineData}
                nodeTypes={nodeTypes}
                edgeTypes={edgeTypes}
                nodesDraggable={true}
                nodesConnectable={true}
                onElementClick={this.onElementClick}
                onConnect={this.onConnect}
                onElementsRemove={this.onRemove}
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
                <div
                    style={{
                        position: 'absolute',
                        left: 32,
                        top: 32,
                        zIndex: 4,
                        textTransform: 'none'
                    }}>
                    <Row gutter={26}>
                        <Col>
                            <Button type="primary" shape="round" onClick={this.saveDiagram}>Save
                                Diagram</Button>
                        </Col>
                        <Col>
                            <Button type="primary" shape="round" danger
                                    onClick={this.runDiagram}>
                                Run Pipeline
                            </Button>
                        </Col>
                        <Col>
                            <Row>
                                <h2>Env:</h2>
                                {this.props.envSelectLoading?
                                <Spin style={{margin: 'auto', paddingLeft: 36 }} loading={true}/> : 
                                    <Select 
                                        allowClear
                                        defaultValue={this.props.pipelineSelectedEnv ? this.props.pipelineSelectedEnv : this.props.selectedPipeline.environment}
                                        placeholder="Choose Env"
                                        style={{ width: 180, marginLeft: 8 }}
                                        onChange={this.handleChange}
                                        
                                        >
                                        {this.props.envsList ? 
                                            this.props.envsList.map(opt => (<Option key={opt} value={opt}>{opt}</Option>)): 
                                        ''}
                                    </Select>}
                            </Row>
                        </Col>
                    </Row>
                </div>
                <Controls/>
            </ReactFlow>
        )
    }
}
