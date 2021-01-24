import {isNode} from "react-flow-renderer";

const LIST_EXECUTION_DIAGRAM = "LIST_EXECUTION_DIAGRAM"
const GET_EXECUTION_DIAGRAM = "GET_EXECUTION_DIAGRAM"
const PIPELINE_EDIT_OPERATOR = 'PIPELINE_EDIT_OPERATOR';
const GET_EXECUTION_OVERVIEW = 'GET_EXECUTION_OVERVIEW';

export const listExecutionDiagramListAction = (data) => ({
    type: LIST_EXECUTION_DIAGRAM,
    data: data
})
export const getExecutionDiagramListAction = (data) => ({
    type: GET_EXECUTION_DIAGRAM,
    data: data
})
export const getExecutionOverviewAction = (data) => ({
    type: GET_EXECUTION_OVERVIEW,
    data: data
})

export const PipelinePageReducer = (state, action) => {
    switch (action.type) {
        case LIST_EXECUTION_DIAGRAM:
            return {
                ...state,
                executionDiagramList: action.data
            }
        case GET_EXECUTION_DIAGRAM:
            return {
                ...state,
                selectedHistory: action.data
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
            let arr = []
            let maxNodeId;
            if (state.pipelineData) {
                state.pipelineData.filter(v => isNode(v)).map(v => arr.push(parseInt(v.id)))
                maxNodeId = arr.length === 0 ? 0 : Math.max.apply(null, arr)
            }
            let id = !state.pipelineData ? 1 : (maxNodeId + 1);
            return {
                ...state,
                pipelineData: Object.assign([],
                    [{
                        id: id + '',
                        data: {
                            label: id + '-' + action.data.name,
                            name: action.data.name
                        },
                        type: 'pipelineNode',
                        position: {x: 200, y: 600}
                    }].concat(state.pipelineData ? state.pipelineData : []),
                )
            }
        case UPDATE_FLOW_GRAPH:
            return {
                ...state,
                pipelineData: action.data,
                envSelectLoading: false
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
                selectedPipeline: action.data
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
export const editPipelineAction = (data) => (
    {
        type: EDIT_PIPELINES,
        data: data
    }
)




