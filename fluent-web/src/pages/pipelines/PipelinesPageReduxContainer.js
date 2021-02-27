import {connect} from "react-redux";
import {PipelinesPage} from "./PipelinesPage";
import {
    deletePipeline,
    listPipelines,
    postPipeline,
    selectPipeline,
    updatePipeline,
    handleStageChange
} from "../../services/PipelineService";
import {createExecutionGraph, getExecutionGraph, getExecutionOverview, listExecutionGraph} from "../../services/ExecutionService";
import {UPDATE_PIPELINE_PAGE, updatePipelineGraph} from "./PipelinesPageReducer";
import {
    addOperatorToPipelineGraph,
    deleteOperator,
    editOperator,
    listOperators,
    postOperator
} from "../../services/OperatorService";
import {listEnv} from '../../services/VariableService'

const mapStateToProps = (state) => {
    return {
        pipelines: state.PipelinePageReducer.pipelines,
        selectedPipeline: state.PipelinePageReducer.selectedPipeline,
        pipelineGraphData: state.PipelinePageReducer.pipelineGraphData,
        pipelineParamsFormVisible: state.PipelinePageReducer.pipelineParamsFormVisible,
        pipelineCurrentStage: state.PipelinePageReducer.pipelineCurrentStage,
        executionCurrentStage: state.PipelinePageReducer.executionCurrentStage,

        executionGraphList: state.PipelinePageReducer.executionGraphList,
        selectedExecution: state.PipelinePageReducer.selectedExecution,
        executionData: state.PipelinePageReducer.executionData,
        selectedExecutionNode: state.PipelinePageReducer.selectedExecutionNode,
        operators: state.PipelinePageReducer.operators,
        selectedOperator: state.PipelinePageReducer.selectedOperator,
        operatorDetailsVisible: state.PipelinePageReducer.operatorDetailsVisible,
        pipelineDetailsVisible: state.PipelinePageReducer.pipelineDetailsVisible,
        executionOverview: state.PipelinePageReducer.executionOverview,

        envsList: state.VariablePageReducer.envsList,
        pipelineSelectedEnv: state.PipelinePageReducer.pipelineSelectedEnv,
        envSelectLoading: state.PipelinePageReducer.envSelectLoading
    }
}

const mapDispatchToProps = (dispatch) => ({
    listOperators: () => {
        dispatch(listOperators());
    },
    addOperatorToPipelineGraph: (name) => {
        dispatch(addOperatorToPipelineGraph(name));
    },
    handleStageChange: (stageName, pipelineGraphData) => {
        dispatch(handleStageChange(stageName, pipelineGraphData))
    },
    updatePipelineGraph: (nodes) => {
        dispatch(updatePipelineGraph(nodes));
    },
    deleteOperator: (name) => {
        dispatch(deleteOperator(name));
    },
    postOperator: (values) => {
        dispatch(postOperator(values));
    },
    editOperator: (name) => {
        dispatch(editOperator(name))
    },
    /** pipelines **/
    listPipelines: () => {
        dispatch(listPipelines())
    },
    postPipeline: (value) => {
        dispatch(postPipeline(value))
    },
    updatePipeline: (name, value) => {
        dispatch(updatePipeline(name, value))
    },
    selectPipeline: (name) => {
        dispatch(selectPipeline(name))
    },
    deletePipeline: (name) => {
        dispatch(deletePipeline(name))
    },
    runPipeline: (value) => {
        dispatch(createExecutionGraph(value))
    },

    updatePipelinePageState: (...key) => {
        dispatch(updatePipelinePageState(key))
    },
    getExactExecutionHistory: (pipelineName, name) => {
        dispatch(getExecutionGraph(pipelineName, name))
    },
    listExecutionGraph: (name) => {
        dispatch(listExecutionGraph(name))
    },
    getExecutionOverview: (name) => {
        dispatch(getExecutionOverview(name))
    },

    listEnv: () => {
        dispatch(listEnv())
    }
})

export function updatePipelinePageState(vs) {
    return dispatch => dispatch(UPDATE_PIPELINE_PAGE(vs));
}


const ExecutionContainer = connect(mapStateToProps, mapDispatchToProps)(PipelinesPage)
export default ExecutionContainer
