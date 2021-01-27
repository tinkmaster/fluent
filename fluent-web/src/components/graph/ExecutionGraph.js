import React from "react";
import {edgeTypes} from "./DataFlowEdgeType";
import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {Controls, MiniMap} from "react-flow-renderer";
import {Row} from 'antd';


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
        if (this.lastselectedHistoryName && this.lastselectedHistoryName !== this.props.selectedHistory.name) {
            this.graphInstance.fitView()
            this.lastselectedHistoryName = this.props.selectedHistory.name
        }
        return (
            <ReactFlow
                onLoad={this.onLoad}
                elements={this.props.executionData}
                nodesConnectable={false}
                nodeTypes={nodeTypes}
                edgeTypes={edgeTypes}
                onElementClick={(event, element) =>
                    this.props.updatePipelinePageState("selectedHistoryNode", element.id)}
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
                    <Row>
                        <h2>Env:</h2>
                        <p style={{paddingTop: 8, paddingLeft: 8 }}>{this.props.selectedHistory.environment ? this.props.selectedHistory.environment : 'Have not chosen.'}</p>
                    </Row>
                </div>
                <Controls/>
            </ReactFlow>
        )
    }
}
