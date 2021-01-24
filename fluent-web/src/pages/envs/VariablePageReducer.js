const FETCH_ENV_LIST='FETCH_ENV_LIST'
const FETCH_SPECIFIED_ENV='FETCH_SPECIFIED_ENV'
const LOADING_ENV='LOADING_ENV'
const LOADING_GLOBAL='LOADING_GLOBAL'
const FETCH_GLOBAL='FETCH_GLOBAL'
const FETCH_SECRET_LIST='FETCH_SECRET_LIST'

export const fetchEnvList = (data) => ({
    type: FETCH_ENV_LIST,
    data: data
})

export const loadingSpecifiedEnv = () => ({
    type: LOADING_ENV,
    data: {}
})

export const fetchSpecifiedEnv = (data) => ({
    type: FETCH_SPECIFIED_ENV,
    data: data
})

export const loadingGlobal = () => ({
    type: LOADING_GLOBAL,
    data: {}
})

export const fetchGlobal = (data) => ({
    type: FETCH_GLOBAL,
    data: data
})

export const fetchSecretsList = (data) => ({
    type: FETCH_SECRET_LIST,
    data: data
})

export const updateVariablePageState = (vs) => {
    const obj = {}
    for (let i = 0; i < vs.length; i++) {
        obj[vs[i]] = vs[i + 1]
        i = i + 1
    }
    return {
        type: 'UPDATE_VARIABLE_PAGE',
        data: Object.assign({}, obj)
    }
}

export const VariablePageReducer = (state, action) => {
    switch (action.type) {
        case 'UPDATE_VARIABLE_PAGE':
            return Object.assign({}, state, action.data)
        case FETCH_ENV_LIST: 
            return {
                ...state,
                envsList: action.data
            }
        case LOADING_ENV:
            return {
                ...state,
                envFormLoading: true
            }
        case LOADING_GLOBAL:
            return {
                ...state,
                globalFormLoading: true
            }
        case FETCH_SPECIFIED_ENV:
            return {
                ...state,
                selectedEnv: action.data,
                envFormLoading: false,
            }
        case FETCH_GLOBAL:
            return {
                ...state,
                globalVariable: action.data,
                globalFormLoading: false
            }
        case FETCH_SECRET_LIST:
            return {
                ...state,
                secretsList: action.data
            }
        default:
            return {
                ...state
            }
    }
}