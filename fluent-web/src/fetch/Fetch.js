import axios from 'axios'


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
    transformResponse: [
        function (data) {
            return data
        }
    ],
    headers: {
        'Content-Type': 'application/json;charset=UTF-8',
    },
    timeout: 10000,
    responseType: 'json',
    withCredentials: true
};

function getCookie(c_name)
{
    if (document.cookie.length>0)
    {
        let c_start=document.cookie.indexOf(c_name + "=")
        if (c_start!=-1)
        {
            c_start=c_start + c_name.length+1
            let c_end=document.cookie.indexOf(";",c_start)
            if (c_end==-1) c_end=document.cookie.length
            return unescape(document.cookie.substring(c_start,c_end))
        }
    }
    return ""
}

axios.interceptors.request.use(function(res) {
    //相应拦截器
    let index = document.cookie.indexOf('x-token=');
    if (index !== -1) {
        if(getCookie('x-token').indexOf('.xx.') === -1){
            res.headers['x-token'] = getCookie('x-token');
        }
    }
    res.headers['Content-Type'] = 'application/json;charset=UTF-8'
    return res;
});



export function get(url) {
    return axios.get(url, config)
}

export function post(url, data) {
    return axios.post(url, data, config)
}

export function deleteRequest(url) {
    return axios.delete(url, config)
}
