import {deleteRequest, get, post} from "../fetch/Fetch";
import {
    DELETE_PIPELINE_ADDRESS,
    GET_PIPELINE_ADDRESS,
    LIST_PIPELINE_ADDRESS,
    POST_PIPELINE_ADDRESS
} from "../interfaces/Constant";
import {message} from 'antd';
import {listExecutionDiagram} from "./ExecutionService";
import {
    editPipelineAction,
    freshPipelineListAction,
    updatePipelineGraph
} from "../pages/pipelines/PipelinesPageReducer";

export function listPipelines() {
    return dispatch => {
        get(LIST_PIPELINE_ADDRESS).then(response => {
            if (response.data.code === 200) {
                dispatch(freshPipelineListAction(response.data.data))
            }
        })
    }
}

export function selectPipeline(name) {
    return dispatch => {
        get(GET_PIPELINE_ADDRESS.replace("{}", name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(editPipelineAction(response.data.data));

                    if (response.data.data && response.data.data.operators) {
                        const pipeline = response.data.data
                        let res = []
                        let i = 80;
                        const ids = Object.keys(pipeline.operators)
                        ids.map(v => {
                                res.push({
                                    id: v,
                                    data: {label: v + '-' + pipeline.operators[v], name: pipeline.operators[v]},
                                    type: 'pipelineNode',
                                    position: {x: 200 + i, y: i}
                                });
                                i = i + 100
                            }
                        )
                        pipeline.connections.map(v => {
                            const ns = v.split('->');
                            res.push({
                                id: ns[0] + '->' + ns[1],
                                source: ns[0],
                                target: ns[1],
                                sourceHandler: null,
                                targetHandler: null,
                                type: 'custom',
                                arrowHeadType: 'arrowclosed',
                            })
                        })

                        console.log(res)

                        dispatch(updatePipelineGraph(res))
                    } else {
                        dispatch(updatePipelineGraph([]))
                    }

                }
            }
        ).then(
            () => {
                dispatch(listExecutionDiagram(name));
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
                }
            }
        )
    }
}

export function updatePipeline(name, values) {
    return dispatch => {
        post(POST_PIPELINE_ADDRESS, values).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Update successfully.")
                }
            }
        )
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
