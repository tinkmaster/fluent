import {get, post} from "../fetch/Fetch";
import {GET_EXECUTION_ADDRESS, GET_EXECUTION_OVERVIEW_ADDRESS, LIST_EXECUTION_ADDRESS, POST_EXECUTION_ADDRESS} from "../interfaces/Constant";
import {message} from "antd";
import {getExecutionGraphListAction, getExecutionOverviewAction, listExecutionGraphListAction} from "../pages/pipelines/PipelinesPageReducer";
import {updatePipelinePageState} from "../pages/pipelines/PipelinesPageReduxContainer";
import {drawExecutionGraph} from "./GraphService";


export function createExecutionGraph(value) {
    return dispatch => {
        post(POST_EXECUTION_ADDRESS, value).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Create PipelinesPage graph successfully.")
                }
            }
        )
    }
}

export function listExecutionGraph(value) {
    return dispatch => {
        get(LIST_EXECUTION_ADDRESS.replace('{}', value)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(listExecutionGraphListAction(response.data.data));
                }
            }
        )
    }
}

export function getExecutionGraph(pipelineName, name) {
    return dispatch => {
        get(GET_EXECUTION_ADDRESS
            .replace('{0}', pipelineName)
            .replace('{1}', name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(getExecutionGraphListAction(response.data.data));
                    const res = drawExecutionGraph(response.data.data)
                    dispatch(updatePipelinePageState(['executionData', res]))
                }
            }
        )
    }
}

export function getExecutionOverview(pipelineName) {
    return dispatch => {
        get(GET_EXECUTION_OVERVIEW_ADDRESS.replace('{}', pipelineName))
        .then(
            response => {
                if (response.data.code === 200) {
                    dispatch(getExecutionOverviewAction(response.data.data));
                }
            }
        )
    }
}
