import React from "react";
import './css/PipelineGraph.css'
import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {addEdge, Controls, isEdge, isNode, MiniMap, removeElements, useZoomPanHelper} from "react-flow-renderer";
import {Button, Col, Row, Select, Spin, PageHeader} from "antd";
import {edgeTypes} from "./DataFlowEdgeType";
const { Option } = Select;


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
                                    <div style={{marginLeft: '8px', width: 180}}>
                                        <Spin style={{margin: 'auto', paddingLeft: 64 }} loading={true}/></div> : 
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
                                <Button style={{marginLeft: '8px'}} key="save" type="primary" onClick={this.saveDiagram}>Save</Button>
                                <Button style={{marginLeft: '8px'}} key="run" danger onClick={this.runDiagram}>Run</Button>
                            </div>
                        ]}>
                    </PageHeader>
                </div>
                <div style={{height:'100%', width: '100%'}}>
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
            </div>
        )
    }
}
