import {get, post} from "../fetch/Fetch";
import {GET_EXECUTION_ADDRESS, LIST_EXECUTION_ADDRESS, POST_EXECUTION_ADDRESS} from "../interfaces/Constant";
import {message} from "antd";
import {getExecutionDiagramListAction, listExecutionDiagramListAction} from "../pages/pipelines/PipelinesPageReducer";
import {updatePipelinePageState} from "../pages/pipelines/PipelinesPageReduxContainer";


export function createExecutionDiagram(value) {
    return dispatch => {
        post(POST_EXECUTION_ADDRESS, value).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Create PipelinesPage diagram successfully.")
                }
            }
        )
    }
}

export function listExecutionDiagram(value) {
    return dispatch => {
        get(LIST_EXECUTION_ADDRESS.replace('{}', value)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(listExecutionDiagramListAction(response.data.data));
                }
            }
        )
    }
}

export function getExecutionDiagram(pipelineName, name) {
    return dispatch => {
        get(GET_EXECUTION_ADDRESS
            .replace('{0}', pipelineName)
            .replace('{1}', name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(getExecutionDiagramListAction(response.data.data));
                    let res = []
                    const nodes = response.data.data.nodes
                    const ids = Object.keys(nodes);
                    for (let i = 0; i < ids.length; i++) {
                        let obj = {}
                        Object.assign(obj, {
                            id: ids[i],
                            data: {
                                label: ids[i] + '-' + nodes[ids[i]].operator.name,
                                name: nodes[ids[i]].operator.name,
                                usedTime: nodes[ids[i]].usedTime
                            },
                            position: {x: 200 + 100 * i, y: 70 * i}
                        })
                        if (nodes[ids[i]].status === 'RUNNING') {
                            obj['type'] = 'runningNode'
                        } else if (nodes[ids[i]].status === 'FAILED') {
                            obj['type'] = 'errorNode'
                        } else if (nodes[ids[i]].status === 'FINISHED') {
                            obj['type'] = 'finishedNode'
                        } else {
                            obj['type'] = 'todoNode'
                        }
                        res.push(obj)
                        for (let j = 0; j < nodes[ids[i]].next.length; j++) {
                            res.push(
                                {
                                    id: ids[i] + '->' + nodes[ids[i]].next[j],
                                    source: ids[i],
                                    target: nodes[ids[i]].next[j],
                                    sourceHandler: null,
                                    targetHandler: null,
                                    arrowHeadType: 'arrowclosed',
                                    type: 'custom'
                                }
                            )
                        }
                    }
                    console.log(res)
                    dispatch(updatePipelinePageState(['executionData', res]))
                }
            }
        )
    }
}


