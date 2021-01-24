import React, {Component} from "react";
import {Button, List} from "antd";
import './css/PipelineList.css'
import {PlusOutlined} from "@ant-design/icons";

export class PipelineList extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
            <div className="pipeline-list">
                <List
                    header={
                        <div className="pipeline-list-title">
                            <span>Pipeline list</span>
                            <Button
                                onClick={() => this.props.updatePipelinePageState('pipelineDetailsVisible', true)}
                                style={{'marginLeft': 'auto', 'marginRight': '18px'}}><PlusOutlined/></Button>
                        </div>}
                    dataSource={this.props.pipelines}
                    renderItem={item => (
                        <List.Item
                            className="pipeline-list-item"
                            onClick={() => this.props.selectPipeline(item)}
                            style={{
                                backgroundColor: this.props.selectedPipeline ?
                                    this.props.selectedPipeline.name === item ? "#e1e1e1" : null
                                    : null
                            }}
                        >
                            <span style={{'width': 'fit-content'}}>{item}</span>
                        </List.Item>
                    )}
                />
            </div>
        )
    }
}
