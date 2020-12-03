import {deleteRequest, get, post} from "../fetch/Fetch";
import {
    DELETE_OPERATOR_ADDRESS,
    GET_OPERATOR_ADDRESS,
    LIST_OPERATOR_ADDRESS,
    POST_OPERATOR_ADDRESS
} from "../interfaces/Constant";
import {addOperatorToFlowGraph, editOperators, freshOperatorList} from "../pages/pipelines/PipelinesPageReducer";
import {message} from 'antd';
import {updatePipelinePageState} from "../pages/pipelines/PipelinesPageReduxContainer";

export function listOperators() {
    return dispatch => {
        get(LIST_OPERATOR_ADDRESS).then(response => {
            if (response.data.code === 200) {
                dispatch(freshOperatorList(response.data.data))
            }
        })
    }
}

export function addOperatorToPipelineGraph(name) {
    return dispatch => {
        get(GET_OPERATOR_ADDRESS.replace("{}", name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(addOperatorToFlowGraph(response.data.data))
                }
            }
        )
    }
}

export function editOperator(name) {
    return dispatch => {
        get(GET_OPERATOR_ADDRESS.replace("{}", name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(editOperators(response.data.data));
                    dispatch(updatePipelinePageState(['operatorDetailsVisible', true]))
                }
            }
        )
    }
}

export function postOperator(values) {
    return dispatch => {
        post(POST_OPERATOR_ADDRESS, values).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(listOperators())
                }
            }
        )
    }
}

export function deleteOperator(name) {
    return dispatch => {
        deleteRequest(DELETE_OPERATOR_ADDRESS.replace("{}", name)).then(
            response => {
                if (response.data.code === 200) {
                    message.success('Delete successfully');
                    get(LIST_OPERATOR_ADDRESS).then(response => {
                        if (response.data.code === 200) {
                            dispatch(freshOperatorList(response.data.data))
                        }
                    })
                }
            }
        )
    }
}
