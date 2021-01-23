import {deleteRequest, errorHandler, get, post} from "../fetch/Fetch";
import {
    DELETE_PIPELINE_ADDRESS,
    GET_PIPELINE_ADDRESS,
    LIST_PIPELINE_ADDRESS,
    POST_PIPELINE_ADDRESS
} from "../interfaces/Constant";
import {message} from 'antd';
import {getExecutionOverview, listExecutionDiagram} from "./ExecutionService";
import {
    editPipelineAction,
    freshPipelineListAction,
    updatePipelineGraph
} from "../pages/pipelines/PipelinesPageReducer";
import React from "react";
import {drawPipelineGraph} from "./GraphService";

export function listPipelines() {
    return dispatch => {
        get(LIST_PIPELINE_ADDRESS).then(response => {
                if (response.data.code === 200) {
                    dispatch(freshPipelineListAction(response.data.data))
                }
            }
        )
    }
}

export function selectPipeline(name) {
    return dispatch => {
        get(GET_PIPELINE_ADDRESS.replace("{}", name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(editPipelineAction(response.data.data));

                    if (response.data.data && response.data.data.operators) {
                        const res = drawPipelineGraph(response.data.data)
                        dispatch(updatePipelineGraph(res))
                    } else {
                        dispatch(updatePipelineGraph([]))
                    }

                }
            }
        ).then(
            () => {
                dispatch(listExecutionDiagram(name));
                dispatch(getExecutionOverview(name))
            }
        )
    }
}

export function postPipeline(value) {
    return dispatch => {
        post(POST_PIPELINE_ADDRESS, value).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(listPipelines())
                    message.success('Save pipeline successfully.');
                }
            }
        ).catch(error => errorHandler(error))
    }
}

export function updatePipeline(name, values) {
    return dispatch => {
        post(POST_PIPELINE_ADDRESS, values).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Update pipeline successfully.")
                }
            }
        ).catch(error => errorHandler(error))
    }
}

export function deletePipeline(name) {
    return dispatch => {
        deleteRequest(DELETE_PIPELINE_ADDRESS.replace("{}", name)).then(
            response => {
                if (response.data.code === 200) {
                    message.success('Delete successfully');
                    get(LIST_PIPELINE_ADDRESS).then(response => {
                        if (response.data.code === 200) {
                            dispatch(freshPipelineListAction(response.data.data))
                        }
                    })
                }
            }
        )
    }
}
