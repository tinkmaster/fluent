import {combineReducers} from "redux";
import {PipelinePageReducer} from "../pages/pipelines/PipelinesPageReducer";
import {VariablePageReducer} from '../pages/envs/VariablePageReducer';

const rootReducer = combineReducers({
    PipelinePageReducer,
    VariablePageReducer
});

export default rootReducer;
