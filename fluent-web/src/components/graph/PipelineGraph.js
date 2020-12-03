import React from "react";

import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {addEdge, Background, Controls, isEdge, isNode, MiniMap, removeElements} from "react-flow-renderer";
import {Button, Col, Row} from "antd";
import {edgeTypes} from "./DataFlowEdgeType";


export class PipelineGraph extends React.Component {

    constructor(props, context) {
        super(props, context);
    }

    onConnect = (params) => {
        console.log(params)
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
            connections: []
        }
        this.props.pipelineData.filter(v => isNode(v)).map(v => {
            obj.operators[v['id']] = v.data.name;
        });
        this.props.pipelineData.filter(v => isEdge(v)).map(v => obj.connections.push(v.source + '->' + v.target));
        this.props.updatePipeline(this.props.selectedPipeline.name, Object.assign({}, obj));
    }

    runDiagram = () => {
        let obj = {
            name: this.props.selectedPipeline.name,
            pipelineName: this.props.selectedPipeline.name,
            operators: {},
            connections: []
        }
        this.props.pipelineData.filter(v => isNode(v)).map(v => {
            obj.operators[v['id']] = v.data.name
        });
        this.props.pipelineData.filter(v => isEdge(v)).map(v => obj.connections.push(v.source + '->' + v.target));
        this.props.runPipeline(Object.assign({}, obj));
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
                    <Row>
                        <Col>
                            <Button type="primary" shape="round" onClick={this.saveDiagram}>Save
                                Diagram</Button>
                        </Col>
                        <Col>
                            <Button type="primary" shape="round" danger style={{marginLeft: 26}}
                                    onClick={this.runDiagram}>
                                Run Pipeline
                            </Button>
                        </Col>
                    </Row>
                </div>
                <Controls/>
                <Background/>
            </ReactFlow>
        )
    }
}
