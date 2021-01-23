export const SERVER_ADDRESS = document.location.href.includes('localhost') ? "http://localhost:8080" : ""

export const LIST_OPERATOR_ADDRESS = SERVER_ADDRESS + "/api/v1/operators";
export const POST_OPERATOR_ADDRESS = SERVER_ADDRESS + "/api/v1/operators";
export const GET_OPERATOR_ADDRESS = SERVER_ADDRESS + "/api/v1/operators/{}";
export const DELETE_OPERATOR_ADDRESS = SERVER_ADDRESS + "/api/v1/operators/{}";


export const LIST_PIPELINE_ADDRESS = SERVER_ADDRESS + "/api/v1/pipelines";
export const POST_PIPELINE_ADDRESS = SERVER_ADDRESS + "/api/v1/pipelines";
export const GET_PIPELINE_ADDRESS = SERVER_ADDRESS + "/api/v1/pipelines/{}";
export const DELETE_PIPELINE_ADDRESS = SERVER_ADDRESS + "/api/v1/pipelines/{}";

export const LIST_EXECUTION_ADDRESS = SERVER_ADDRESS + "/api/v1/executions/{}/diagram";
export const POST_EXECUTION_ADDRESS = SERVER_ADDRESS + "/api/v1/executions";
export const GET_EXECUTION_OVERVIEW_ADDRESS = SERVER_ADDRESS + "/api/v1/executions/{}/overview";
export const GET_EXECUTION_ADDRESS = SERVER_ADDRESS + "/api/v1/executions/{0}/diagram/{1}";
export const DELETE_EXECUTION_ADDRESS = SERVER_ADDRESS + "/api/v1/executions/{}";
