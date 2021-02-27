import { act } from "react-dom/test-utils";
import {isNode} from "react-flow-renderer";

const LIST_EXECUTION_GRAPH = "LIST_EXECUTION_GRAPH"
const GET_EXECUTION_GRAPH = "GET_EXECUTION_GRAPH"
const PIPELINE_EDIT_OPERATOR = 'PIPELINE_EDIT_OPERATOR';
const GET_EXECUTION_OVERVIEW = 'GET_EXECUTION_OVERVIEW';

export const listExecutionGraphListAction = (data) => ({
    type: LIST_EXECUTION_GRAPH,
    data: data
})
export const getExecutionGraphListAction = (data) => ({
    type: GET_EXECUTION_GRAPH,
    data: data
})
export const getExecutionOverviewAction = (data) => ({
    type: GET_EXECUTION_OVERVIEW,
    data: data
})

export const PipelinePageReducer = (state, action) => {
    switch (action.type) {
        case LIST_EXECUTION_GRAPH:
            return {
                ...state,
                executionGraphList: action.data
            }
        case GET_EXECUTION_GRAPH:
            return {
                ...state,
                selectedExecution: action.data,
                executionCurrentStage: 'execute'
            }
        case GET_EXECUTION_OVERVIEW:
            return {
                ...state,
                executionOverview: action.data
            }
        case PIPELINE_EDIT_OPERATOR:
            return {
                ...state,
                selectedOperator: action.data,
                operatorDetailsVisible: true
            }
        case 'UPDATE_EXECUTION_PAGE':
            return Object.assign({}, state, action.data)
        case ADD_OPERATORS_TO_FLOW_GRAPH:
            let temperatePipelineGraphData = Object.assign({}, state.pipelineGraphData)
            let arr = []
            let maxNodeId;
            if (temperatePipelineGraphData && temperatePipelineGraphData[state.pipelineCurrentStage]) {
                temperatePipelineGraphData[state.pipelineCurrentStage].filter(v => isNode(v)).map(v => arr.push(parseInt(v.id)))
                maxNodeId = arr.length === 0 ? 0 : Math.max.apply(null, arr)
            }
            let id = !temperatePipelineGraphData[state.pipelineCurrentStage] ? 1 : (maxNodeId + 1);
            temperatePipelineGraphData[state.pipelineCurrentStage] = Object.assign([],
                [{
                    id: id + '',
                    data: {
                        label: id + '-' + action.data.name,
                        name: action.data.name
                    },
                    type: 'pipelineNode',
                    position: {x: 100, y: 50}
                }].concat(temperatePipelineGraphData[state.pipelineCurrentStage] ? state.pipelineGraphData[state.pipelineCurrentStage] : []))
            return {
                ...state,
                pipelineGraphData: temperatePipelineGraphData
            }
        case UPDATE_FLOW_GRAPH:
            return {
                ...state,
                pipelineGraphData: action.data,
                envSelectLoading: false
            }
        case UPDATE_GRAPH_STAGE:
            return {
                ...state,
                pipelineCurrentStage: action.data.pipelineCurrentStage,
                pipelineGraphData: action.data.pipelineGraphData
            }
        case EDIT_OPERATORS:
            return {
                ...state,
                selectedOperator: action.data
            }
        case LIST_OPERATORS:
            return {
                ...state,
                operators: action.data
            }
        case LIST_PIPELINES:
            return {
                ...state,
                pipelines: action.data
            }
        case EDIT_PIPELINES:
            return {
                ...state,
                selectedPipeline: action.data,
                envSelectLoading: false,
                pipelineCurrentStage: 'execute'
            }
        default:
            return {
                ...state
            }
    }
}

export const UPDATE_PIPELINE_PAGE = (vs) => {
    const obj = {}
    for (let i = 0; i < vs.length; i++) {
        obj[vs[i]] = vs[i + 1]
        i = i + 1
    }
    return {
        type: 'UPDATE_EXECUTION_PAGE',
        data: Object.assign({}, obj)
    }
}


export const LIST_OPERATORS = 'LIST_OPERATORS';
export const freshOperatorList = (data) => (
    {
        type: LIST_OPERATORS,
        data: data
    }
)
export const PipelineOperatorListReducer = (state, action) => {
    switch (action.type) {

        default: {
            return {
                ...state
            }
        }
    }
}
/*******************************************************************************************/

export const ADD_OPERATORS_TO_FLOW_GRAPH = 'ADD_OPERATORS_TO_FLOW_GRAPH';
export const EDIT_OPERATORS = 'EDIT_OPERATORS';
export const UPDATE_FLOW_GRAPH = 'UPDATE_FLOW_GRAPH';
export const addOperatorToFlowGraph = (data) => (
    {
        type: ADD_OPERATORS_TO_FLOW_GRAPH,
        data: data
    }
)
export const editOperators = (data) => (
    {
        type: EDIT_OPERATORS,
        data: data
    }
)
export const updatePipelineGraph = (data) => (
    {
        type: UPDATE_FLOW_GRAPH,
        data: data
    }
)


/*******************************************************************************************/

export const LIST_PIPELINES = 'LIST_PIPELINES';
export const EDIT_PIPELINES = 'EDIT_PIPELINES';
export const freshPipelineListAction = (data) => (
    {
        type: LIST_PIPELINES,
        data: data
    }
)
export const selectPipelineAction = (data) => (
    {
        type: EDIT_PIPELINES,
        data: data
    }
)

/*******************************************************************************************/
export const UPDATE_GRAPH_STAGE = 'UPDATE_GRAPH_STAGE'
export const updateGraphStage = (stage, pipelineGraphData) => (
    {
        type: UPDATE_GRAPH_STAGE,
        data: {
            pipelineCurrentStage: stage,
            pipelineGraphData: pipelineGraphData
        }
    }
)


