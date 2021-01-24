import { message } from "antd";
import {get, post, deleteRequest} from "../fetch/Fetch";
import { DELETE_ENV_ADDRESS, DELETE_SECRET_ADDRESS, GET_ENV_ADDRESS, GET_GLOBAL_ADDRESS, LIST_ENV_ADDRESS, LIST_SECRET_ADDRESS, POST_ENV_ADDRESS, POST_GLOBAL_ADDRESS, POST_SECRET_ADDRESS } from "../interfaces/Constant";
import { fetchEnvList, fetchGlobal, fetchSecretsList, fetchSpecifiedEnv, loadingGlobal, loadingSpecifiedEnv, updateVariablePageState } from "../pages/envs/VariablePageReducer";

export function listEnv() {
    return dispatch => {
        get(LIST_ENV_ADDRESS).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(fetchEnvList(response.data.data))
                }
            }
        )
    }
}

export function getSpecifiedEnv(name) {
    return dispatch => {
        get(GET_ENV_ADDRESS.replace('{}', name)).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(loadingSpecifiedEnv())
                    setTimeout(function () {
                    }, 1000);
                    dispatch(fetchSpecifiedEnv(response.data.data))
                }
            }
        )
    }
}

export function postEnv(env) {
    return dispatch => {
        post(POST_ENV_ADDRESS, env).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Create environment successfully.")
                }
            }
        ).then(
            () => {
                dispatch(listEnv())
                dispatch(getSpecifiedEnv(env.name))
            }
        )
    }
}

export function updateEnv(name) {
    return dispatch => {
        get(POST_ENV_ADDRESS).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Update environment successfully.")
                }
            }
        )
    }
}

export function deleteEnv(name) {
    return dispatch => {
        deleteRequest(DELETE_ENV_ADDRESS.replace('{}', name)).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Delete environment successfully.")
                    dispatch(updateVariablePageState(['selectedEnv', null]))
                }
            }
        ).then(
            () => {
                dispatch(listEnv())
            }
        )
    }
}

export function getGlobal(global) {
    return dispatch => {
        get(GET_GLOBAL_ADDRESS, global).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(loadingGlobal())
                    dispatch(fetchGlobal(response.data.data))
                }
            }
        )
    }
}

export function updateGlobal(global) {
    return dispatch => {
        post(POST_GLOBAL_ADDRESS, global).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Update global variables successfully.")
                    fetchGlobal(response.data.data)
                }
            }
        ).then(
            () => {
                dispatch(getGlobal())
            }
        )
    }
}

export function listSecrets() {
    return dispatch => {
        get(LIST_SECRET_ADDRESS).then(
            response => {
                if (response.data.code === 200) {
                    dispatch(fetchSecretsList(response.data.data))
                }
            }
        )
    }
}

export function postSecret(secret) {
    return dispatch => {
        post(POST_SECRET_ADDRESS, secret).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Create secrets successfully.")
                }
            }
        ).then(
            () => {
                dispatch(listSecrets())
            }
        )
    }
}

export function updateSecret(secret) {
    return dispatch => {
        post(POST_SECRET_ADDRESS).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Update environment successfully.")
                }
            }
        )
    }
}


export function deleteSecret(name) {
    return dispatch => {
        deleteRequest(DELETE_SECRET_ADDRESS.replace('{}', name)).then(
            response => {
                if (response.data.code === 200) {
                    message.success("Delete secret successfully.")
                }
            }
        ).then(
            () => {
                dispatch(listSecrets())
            }
        )
    }
}
