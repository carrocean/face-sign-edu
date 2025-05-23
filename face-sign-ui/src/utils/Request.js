import axios from "axios";

import Message from '../utils/Message';
import common from "@/libs/globalFunction/common.js";
import globalConfig from "@/config/index.js";

const contentTypeForm ='application/x-www-form-urlencoded;charset=UTF-8'
const contentTypeJson='application/json'

const responseTypeJson= "json"

let baseUrl = '';
switch (process.env.NODE_ENV) {
    case 'development':
        baseUrl = "http://localhost:30001"  //开发环境url
        break
    case 'production':
        baseUrl = "http://carrocean.top:30001"   //生产环境url
        break
}

const instance =axios.create({
    baseURL: baseUrl,
    timeout:10*1000,
})
//请求拦截
instance.interceptors.request.use(
    (config)=> {
        // 登录请求不需要token
        if (!config.url.includes('/login')) {
            config.headers[globalConfig.tokenKeyName] = common.getCookies(globalConfig.tokenKeyName)
        }
        return config;
    },
    (error)=> {
        Message.error("error");
        return Promise.reject("error");
    }
);
//请求后拦截器
instance.interceptors.response.use(
    (res) => {

        return res.data;
    },
    (error) => {
        return Promise.reject({showError:true,msg:"网络异常"});
    }
);

export function request(config) {
    return instance(config);
}