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
import { SelectedExecutionOverview } from '../../components/executions/SelectedExecutionOverview';

export class PipelinesPage extends React.Component {

    constructor(props, context) {
        super(props, context);
    }

    refreshTime;
    refreshExecutionOverview;
    refreshHistoryTime;

    componentDidMount() {
        document.title = "Fluent Web | Pipelines"
        this.props.listPipelines()
        this.props.listOperators();
        this.props.listEnv();
        if (this.props.selectedPipeline) {
            this.refreshTime = setInterval(() => this.refreshHistoryList(), 3000)
            this.refreshHistoryList()
        }
    }

    componentWillUnmount() {
        clearInterval(this.refreshTime)
        clearInterval(this.refreshHistoryTime)
        clearInterval(this.refreshExecutionOverview)
        this.refreshTime = null
        this.refreshHistoryTime = null
        this.refreshExecutionOverview = null;
    }

    getHistory(name) {
        clearInterval(this.refreshExecutionOverview)
        this.refreshExecutionOverview = null;
        this.props.getExactExecutionHistory(this.props.selectedPipeline.name, name)
        this.props.updatePipelinePageState("selectedExecutionNode", null)
        if (!this.refreshHistoryTime) {
            this.refreshHistoryTime = setInterval(() => this.refreshHistoryGraph(), 8000)
        }
    }

    refreshHistoryList() {
        if (this.props.selectedPipeline) {
            this.props.listExecutionGraph(this.props.selectedPipeline.name)
        }
    }

    refreshHistoryGraph() {
        if (this.props.selectedExecution && (this.props.selectedExecution.status !== 'FINISHED' && this.props.selectedExecution.status !== 'FAILED')) {
            this.props.getExactExecutionHistory(this.props.selectedPipeline.name, this.props.selectedExecution.name)
        } else if (this.refreshHistoryTime){
            clearInterval(this.refreshHistoryTime)
            this.refreshHistoryTime = null;
        }
    }

    selectPipeline = (name) => {
        this.props.updatePipelinePageState('executionData', null, 
            'selectedExecution', null, 
            'selectedExecutionNode', null,
            'pipelineSelectedEnv', null,
            'envSelectLoading', true)
        this.props.selectPipeline(name)
        clearInterval(this.refreshHistoryTime)
        this.refreshHistoryTime = null
        if (!this.refreshTime) {
            this.refreshTime = setInterval(() => this.refreshHistoryList(), 10000)
        }
        if (!this.refreshExecutionOverview) {
            this.refreshExecutionOverview = setInterval(() => this.props.getExecutionOverview(this.props.selectedPipeline.name), 10000)
        }
    }

    onElementClick = (event, element) => this.props.updatePipelinePageState("selectedExecutionNode", element.id)

    runGraph = () => {
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
                            this.props.selectedExecution && this.props.executionData ?
                                <ExecutionGraph
                                    selectedExecution={this.props.selectedExecution}
                                    executionData={this.props.executionData}
                                    updatePipelinePageState={this.props.updatePipelinePageState}
                                    executionCurrentStage={this.props.executionCurrentStage}
                                />
                                :
                                <PipelineGraph
                                    pipelineParamsFormVisible={this.props.pipelineParamsFormVisible}
                                    envsList={this.props.envsList}
                                    pipelineSelectedEnv={this.props.pipelineSelectedEnv}
                                    envSelectLoading={this.props.envSelectLoading}
                                    pipelineGraphData={this.props.pipelineGraphData}
                                    pipelineCurrentStage={this.props.pipelineCurrentStage}
                                    handleStageChange={this.props.handleStageChange}
                                    selectedPipeline={this.props.selectedPipeline}
                                    updatePipelineGraph={this.props.updatePipelineGraph}
                                    runPipeline={this.props.runPipeline}
                                    updatePipeline={this.props.updatePipeline}
                                    updateGraph={this.props.updatePipelineGraph}
                                    updatePipelinePageState={this.props.updatePipelinePageState}
                                />
                        }
                    </div>
                    <div style={{
                        display: 'flex', displayDirection: 'row',
                        height: "calc(40% - 44px)", paddingTop: 16, width: '100%'
                    }}>
                        <div style={{width: '25%'}}>
                            <ExecutionHistoryList
                                executionGraphList={this.props.executionGraphList}
                                selectedExecution={this.props.selectedExecution}
                                selectedExecutionNode={this.props.selectedExecutionNode}
                                updatePipelinePageState={this.props.updatePipelinePageState}
                                selectExecution={(name) => this.getHistory(name)}
                            />
                        </div>
                        <div style={{width: '75%'}}>
                            { this.props.selectedExecutionNode ? 
                                <ExecutionNodeInfo
                                    node={this.props.selectedExecution ? 
                                        this.props.selectedExecution.stages[this.props.executionCurrentStage].nodes[this.props.selectedExecutionNode] : undefined}
                                    graph={this.props.selectedExecution.stages[this.props.executionCurrentStage]}
                                /> : 
                                (this.props.selectedExecution?
                                    <SelectedExecutionOverview
                                        selectedExecution={this.props.selectedExecution}
                                        executionCurrentStage={this.props.executionCurrentStage}
                                    />
                                    : 
                                    <ExecutionHistoryOverview
                                        executionOverview={this.props.executionOverview}
                                        updatePipelinePageState={this.props.updatePipelinePageState}
                                        selectedPipeline={this.props.selectedPipeline}
                                    />)
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


