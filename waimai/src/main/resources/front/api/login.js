function loginApi(data) {
    return $axios({
        'url': '/user/login',
        'method': 'post',
        data
    })
}

function getCodeApi(data) {
    return $axios({
        'url': '/user/sendMsg',
        'method': 'post',
        data
    })
}

function loginoutApi() {
    return $axios({
        'url': '/user/loginout',
        'method': 'post',
    })
}

//更新用户余额
function rechargeApi(data) {
    return $axios({
        'url': 'user/recharge',
        'method': 'post',
        data
    })
}

//注册用户
function registerApi(data) {
    return $axios({
        'url': 'user/register',
        'method': 'post',
        data
    })
}