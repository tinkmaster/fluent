import React from "react";
import './css/ExecutionGraph.css'
import {edgeTypes} from "./DataFlowEdgeType";
import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {Controls, MiniMap} from "react-flow-renderer";
import {PageHeader, Tag, Tabs, Popover} from 'antd';
import { CheckOutlined, CloseOutlined, HourglassOutlined, LoadingOutlined, StopOutlined } from "@ant-design/icons";
import { string } from "prop-types";

const { TabPane } = Tabs;

export class ExecutionGraph extends React.Component {

    constructor(props, context) {
        super(props, context);
        this.graphInstance = undefined
        this.lastselectedExecutionName = undefined
    }

    onLoad = (reactFlowInstance) => {
        if (!this.graphInstance) {
            this.graphInstance = reactFlowInstance;
            this.graphInstance.fitView()
        }
        if (this.props.selectedExecution) {
            this.lastselectedExecutionName = this.props.selectedExecution.name
        }
    };

    handleStageChange = (value) => {
        this.props.updatePipelinePageState('executionCurrentStage', value, 'selectedExecutionNode', undefined)
    }

    getTabText(key) {
        let status = this.props.selectedExecution.stages[key.toLowerCase()].status
        return (
            <div>
                {status === 'CREATED' ?
                <Popover content={'Waiting to be scheduled...'}
                        trigger="hover">
                    <span style={{paddingLeft: '16px'}}>{key} <HourglassOutlined/></span>
                </Popover>
                : ''}
                {status === 'RUNNING' ?
                    <Popover content={'Running...'} trigger="hover">
                        <span style={{paddingLeft: '16px'}}>{key} <LoadingOutlined/></span>
                    </Popover>
                    : ''}
                {status === 'FINISHED' ?
                    <Popover content={'Finished'} trigger="hover">
                        <span style={{paddingLeft: '16px'}}>{key} <CheckOutlined style={{color: 'green'}}/></span>
                    </Popover>
                    : ''}
                {status === 'SKIPPED' ?
                    <Popover content={'Skipped'} trigger="hover">
                        <span style={{paddingLeft: '16px'}}>{key} <StopOutlined style={{color: 'blue'}}/></span>
                    </Popover>
                    : ''}
                {status === 'FAILED' ?
                    <Popover content={'Failed'} trigger="hover">
                        <span style={{paddingLeft: '16px'}}>{key} <CloseOutlined style={{color: 'red'}}/></span>
                    </Popover>
                    : ''}
            </div>
        )
    }

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
                                        {this.props.selectedExecution.environment ? 
                                            <Tag color='success'> {this.props.selectedExecution.environment} </Tag> : 
                                            <Tag icon={<CloseOutlined />} color="error">Have not chosen.</Tag>}</span>
                                </div>
                            ]}>
                        </PageHeader>
                    </div>
                </div>
                <div className='execution-graph-page-stage-tabs'>
                    <Tabs onChange={this.handleStageChange} activeKey={this.props.executionCurrentStage}>
                        <TabPane tab={<span style={{paddingLeft: '24px'}}>Stages:</span>} key="stages" disabled></TabPane>
                        <TabPane tab={this.getTabText('Before')} key="before"></TabPane>
                        <TabPane tab={this.getTabText('Execute')} key="execute"></TabPane>
                        <TabPane tab={this.getTabText('Clean')} key="clean"></TabPane>
                    </Tabs>
                </div>
                <div style={{height:'86%', width: '100%'}}>
                    <ReactFlow
                        elements={this.props.executionData[this.props.executionCurrentStage]}
                        nodesConnectable={false}
                        nodeTypes={nodeTypes}
                        edgeTypes={edgeTypes}
                        defaultZoom={0.5}
                        onElementClick={(event, element) =>
                            this.props.updatePipelinePageState("selectedExecutionNode", element.id)}
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
                        </div>
                        <Controls/>
                    </ReactFlow>
                </div>
            </div>
        )
    }
}
