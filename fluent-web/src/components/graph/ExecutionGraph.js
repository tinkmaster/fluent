import React from "react";
import './css/ExecutionGraph.css'
import {edgeTypes} from "./DataFlowEdgeType";
import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {Controls, MiniMap} from "react-flow-renderer";
import {PageHeader, Tag} from 'antd';
import { CloseOutlined } from "@ant-design/icons";


export class ExecutionGraph extends React.Component {

    constructor(props, context) {
        super(props, context);
        this.graphInstance = undefined
        this.lastselectedHistoryName = undefined
    }

    onLoad = (reactFlowInstance) => {
        if (!this.graphInstance) {
            this.graphInstance = reactFlowInstance;
            this.graphInstance.fitView()
        }
        if (this.props.selectedHistory) {
            this.lastselectedHistoryName = this.props.selectedHistory.name
        }
    };

    render() {
        return (
            <div style={{height:'100%'}}>
                <div>
                    <div className='execution-graph-page-header'>
                        <PageHeader
                            title="Execution Graph"
                            extra={[
                                <div style={{display: 'flex', paddingTop: '4px'}}>
                                    <h3 style={{paddingLeft: '8px', margin: 'auto'}}>Env:</h3>
                                    <span style={{paddingLeft: '8px', margin: 'auto'}}>
                                        {this.props.selectedHistory.environment ? 
                                            <Tag color='success'> {this.props.selectedHistory.environment} </Tag> : 
                                            <Tag icon={<CloseOutlined />} color="error">Have not chosen.</Tag>}</span>
                                </div>
                            ]}>
                        </PageHeader>
                    </div>
                </div>
                <div style={{height:'100%', width: '100%'}}>
                    <ReactFlow
                        elements={this.props.executionData}
                        nodesConnectable={false}
                        nodeTypes={nodeTypes}
                        edgeTypes={edgeTypes}
                        defaultZoom={0.5}
                        onElementClick={(event, element) =>
                            this.props.updatePipelinePageState("selectedHistoryNode", element.id)}
                    >
                        <MiniMap
                            style={{marginBottom: '44px'}}
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
                        </div>
                        <Controls/>
                    </ReactFlow>
                </div>
            </div>
        )
    }
}
