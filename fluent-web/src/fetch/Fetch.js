import axios from 'axios'
import {notification} from "antd";
import CloseCircleOutlined from "@ant-design/icons/lib/icons/CloseCircleOutlined";
import React from "react";


let config = {
    // baseURL: '',
    // transformRequest: [
    //     // function (data) {
    //     //     let ret = '';
    //     //     for (let it in data) {
    //     //         ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
    //     //     }
    //     //     return ret
    //     // }
    // ],
    // transformResponse: [
    //     function (data) {
    //         return data
    //     }
    // ],
    headers: {
        'Content-Type': 'application/json;charset=UTF-8',
    },
    timeout: 10000,
    responseType: 'json',
    withCredentials: true
};

function getCookie(c_name) {
    if (document.cookie.length > 0) {
        let c_start = document.cookie.indexOf(c_name + "=")
        if (c_start !== -1) {
            c_start = c_start + c_name.length + 1
            let c_end = document.cookie.indexOf(";", c_start)
            if (c_end === -1) c_end = document.cookie.length
            return unescape(document.cookie.substring(c_start, c_end))
        }
    }
    return ""
}

axios.interceptors.request.use(function (res) {
    let index = document.cookie.indexOf('x-token=');
    if (index !== -1) {
        if (getCookie('x-token').indexOf('.xx.') === -1) {
            res.headers['x-token'] = getCookie('x-token');
        }
    }
    res.headers['Content-Type'] = 'application/json;charset=UTF-8'
    return res;
});

export function errorHandler(error) {
    if (Object.keys(error).length !== 0) {
        notification.error({
            message: 'Failed',
            description: error.response.data.message,
            icon: <CloseCircleOutlined style={{color: 'red'}}/>,
        });
    }
}

export function get(url, resHandler) {
    return axios.get(url, config)
}

export function post(url, data, resHandler) {
    return axios.post(url, data, config)
}

export function deleteRequest(url, resHandler) {
    return axios.delete(url, config)
}
