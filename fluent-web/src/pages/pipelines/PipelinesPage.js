import './PipelinesPage.css';
import React from "react";
import message from "antd/es/message";
import {OperatorDetails} from "../../components/operators/OperatorDetails";
import {PipelineDetails} from "../../components/pipelines/PipelineDetails";
import {ExecutionGraph} from "../../components/graph/ExecutionGraph";
import {PipelineGraph} from "../../components/graph/PipelineGraph";
import {OperatorList} from "../../components/operators/OperatorList";
import {PipelineList} from "../../components/pipelines/PipelineList";
import {ExecutionHistoryList} from "../../components/executions/ExecutionHistoryList";
import {ExecutionHistoryOverview} from "../../components/executions/ExecutionHistoryOverview";
import {ExecutionNodeInfo} from "../../components/executions/ExecutionNodeInfo";
import {Empty} from "antd";

export class PipelinesPage extends React.Component {

    constructor(props, context) {
        super(props, context);
    }

    refreshTime;
    refreshHistoryTime;

    componentDidMount() {
        document.title = "Fluent Web | Pipelines"
        this.props.listPipelines()
        this.props.listOperators();
        if (this.props.selectedPipeline) {
            this.refreshTime = setInterval(() => this.refreshHistoryList(), 3000)

            this.refreshHistoryList()
        }
    }

    componentWillUnmount() {
        clearInterval(this.refreshTime)
        clearInterval(this.refreshHistoryTime)
        this.refreshTime = null
        this.refreshHistoryTime = null
        this.props.updatePipelinePageState('executionData', null,
            'selectedHistory', null, "selectedHistoryNode", null)
    }

    getHistory(name) {
        this.props.getExactExecutionHistory(this.props.selectedPipeline.name, name)
        this.props.updatePipelinePageState("selectedHistoryNode", null)
        if (!this.refreshHistoryTime) {
            this.refreshHistoryTime = setInterval(() => this.refreshHistoryDiagram(), 3000)
        }
    }

    refreshHistoryList() {
        if (this.props.selectedPipeline) {
            this.props.listExecutionDiagram(this.props.selectedPipeline.name)
        }
    }

    refreshHistoryDiagram() {
        if (this.props.selectedHistory) {
            this.props.getExactExecutionHistory(this.props.selectedPipeline.name, this.props.selectedHistory.name)
        }
    }

    selectPipeline = (name) => {
        this.props.selectPipeline(name)
        clearInterval(this.refreshHistoryTime)
        this.refreshHistoryTime = null
        if (!this.refreshTime) {
            this.refreshTime = setInterval(() => this.refreshHistoryList(), 3000)
        }
        this.props.updatePipelinePageState('executionData', null, 'selectedHistory', null)
    }

    onElementClick = (event, element) => this.props.updatePipelinePageState("selectedHistoryNode", element.id)

    runDiagram = () => {
        message.warn('Not support yet.')
    }

    addOperatorToGraph = (event, name) => {
        if (!this.props.selectedPipeline) {
            message.warn("Please select pipeline first.")
            return
        }
        this.props.addOperatorToGraph(name)
    }

    editOperator = (event, name) => {
        this.props.editOperator(name)
    }

    deleteOperator = (event, name) => {
        this.props.deleteOperator(name)
    }

    editPipeline = (event, name) => {
        this.props.fetchPipeline(name)
    }

    deletePipeline = (event, name) => {
        this.props.deletePipeline(name)
    }

    onCancel = () => {
        this.setState({visible: false})
    }

    render() {
        if (this.props.pipelines) {
            this.props.pipelines.sort()
        }
        if (this.props.operators) {
            this.props.operators.sort()
        }

        return (
            <div className="pipeline-page-overview">
                <PipelineList
                    pipelines={this.props.pipelines}
                    selectedPipeline={this.props.selectedPipeline}
                    selectPipeline={this.selectPipeline}
                    updatePipelinePageState={this.props.updatePipelinePageState}
                />
                <div style={{
                    paddingLeft: '12px',
                    paddingRight: '12px',
                    backgroundColor: "#fef2f5",
                    width: '100%',
                    display: 'flex',
                    flexDirection: 'column'
                }}>
                    <div style={{
                        height: '60%',
                        backgroundColor: "#ffffff",
                        width: '100%'
                    }}>
                        {!this.props.selectedPipeline ?
                            <div style={{
                                textAlign: 'center',
                                margin: 'auto',
                                height: '100%',
                                marginTop: '20%'
                            }}><Empty
                                description={
                                    <span>Please select pipeline in the left SideBar first</span>
                                }
                            >
                            </Empty></div>
                            :
                            this.props.selectedHistory && this.props.executionData ?
                                <ExecutionGraph
                                    executionData={this.props.executionData}
                                    updatePipelinePageState={this.props.updatePipelinePageState}
                                />
                                :
                                <PipelineGraph
                                    pipelineData={this.props.pipelineData}
                                    selectedPipeline={this.props.selectedPipeline}
                                    updatePipelineGraph={this.props.updatePipelineGraph}
                                    runPipeline={this.props.runPipeline}
                                    updatePipeline={this.props.updatePipeline}
                                    updateGraph={this.props.updatePipelineGraph}
                                />
                        }
                    </div>
                    <div style={{
                        display: 'flex', displayDirection: 'row',
                        height: "40%", paddingTop: 16, width: '100%'
                    }}>
                        <div style={{width: '25%'}}>
                            <ExecutionHistoryList
                                executionDiagramList={this.props.executionDiagramList}
                                selectedHistory={this.props.selectedHistory}
                                selectedHistoryNode={this.props.selectedHistoryNode}
                                updatePipelinePageState={this.props.updatePipelinePageState}
                                selectExecution={(name) => this.getHistory(name)}
                            />
                        </div>
                        <div style={{width: '75%'}}>
                            {this.props.selectedHistory && this.props.selectedHistoryNode ?
                                <ExecutionNodeInfo
                                    node={this.props.selectedHistory.nodes[this.props.selectedHistoryNode]}
                                    diagram={this.props.selectedHistory}
                                />
                                :
                                <ExecutionHistoryOverview/>
                            }
                        </div>
                    </div>
                </div>
                <OperatorList
                    operators={this.props.operators}
                    updatePipelinePageState={this.props.updatePipelinePageState}
                    addOperatorToPipelineGraph={this.props.addOperatorToPipelineGraph}
                    editOperator={this.editOperator}
                    deleteOperator={this.props.deleteOperator}
                />
                <OperatorDetails
                    postOperator={this.props.postOperator}
                    visible={this.props.operatorDetailsVisible}
                    selectedOperator={this.props.selectedOperator}
                    updateState={this.props.updatePipelinePageState}/>
                <PipelineDetails
                    postPipeline={this.props.postPipeline}
                    visible={this.props.pipelineDetailsVisible}
                    selectedPipeline={this.props.selectedPipeline}
                    updateState={this.props.updatePipelinePageState}
                />
            </div>
        )
            ;
    }
}


