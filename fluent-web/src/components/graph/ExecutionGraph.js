import React from "react";
import {edgeTypes} from "./DataFlowEdgeType";
import {nodeTypes} from "./DataFlowNodeTypes";
import ReactFlow, {Background, Controls, MiniMap} from "react-flow-renderer";


export class ExecutionGraph extends React.Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
            <ReactFlow
                elements={this.props.executionData}
                nodesConnectable={false}
                nodesDraggable={false}
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
                {/*<div*/}
                {/*    style={{*/}
                {/*        position: 'absolute',*/}
                {/*        left: 32,*/}
                {/*        top: 32,*/}
                {/*        zIndex: 4,*/}
                {/*        textTransform: 'none'*/}
                {/*    }}>*/}
                {/*    <Row>*/}

                {/*        <Col>*/}
                {/*            <Button type="primary" shape="round" onClick={this.props.saveDiagram}>Save*/}
                {/*                Diagram</Button>*/}
                {/*        </Col>*/}
                {/*        <Col>*/}
                {/*            <Button type="primary" shape="round" danger style={{marginLeft: 26}}*/}
                {/*                    onClick={this.props.runDiagram}>*/}
                {/*                Run Pipeline*/}
                {/*            </Button>*/}
                {/*        </Col>*/}
                {/*    </Row>*/}
                {/*</div>*/}
                <Controls/>
                <Background/>
            </ReactFlow>
        )
    }
}
