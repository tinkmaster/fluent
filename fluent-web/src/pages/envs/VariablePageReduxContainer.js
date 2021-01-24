import {connect} from "react-redux";
import { getSpecifiedEnv, listEnv, postEnv, deleteEnv, updateGlobal, getGlobal, postSecret, deleteSecret, listSecrets } from "../../services/VariableService";
import { updateVariablePageState } from "./VariablePageReducer";
import { VariablePage } from "./VariablePage"

const mapStateToProps = (state) => {
    return {
        envsList: state.VariablePageReducer.envsList,
        selectedEnv: state.VariablePageReducer.selectedEnv,
        envFormLoading: state.VariablePageReducer.envFormLoading,
        globalVariable: state.VariablePageReducer.globalVariable,
        globalFormLoading: state.VariablePageReducer.globalFormLoading,
        secretsList: state.VariablePageReducer.secretsList,
        environmentDetailsVisible: state.VariablePageReducer.environmentDetailsVisible,
        secretDetailsVisible: state.VariablePageReducer.secretDetailsVisible
    }
}

const mapDispatchToProps = (dispatch) => ({
    fetchEnvsList: () => {
        dispatch(listEnv());
    },
    fetchSpecifiedEnv: (name) => {
        dispatch(getSpecifiedEnv(name));
    },
    postEnv: (env) => {
        dispatch(postEnv(env))
    },
    deleteEnv: (name) => {
        dispatch(deleteEnv(name))
    },
    fetchSecretsList: () => {
        dispatch(listSecrets())
    },
    postSecret: (secret) => {
        dispatch(postSecret(secret))
    },
    deleteSecret: (name) => {
        dispatch(deleteSecret(name))
    },
    fetchGlobal: () => {
        dispatch(getGlobal());
    },
    postGlobal: (global) => {
        dispatch(updateGlobal(global));
    },
    updateVariablePageState: (...key) => {
        dispatch(updateVariablePageState(key))
    }
})


const VariablePageContainer = connect(mapStateToProps, mapDispatchToProps)(VariablePage)
export default VariablePageContainer