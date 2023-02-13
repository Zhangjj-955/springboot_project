// 查询列表数据
const getSetmealPage = (params) => {
    return $axios({
        url: '/setmeal/page',
        method: 'get',
        params
    })
}
// 查询饭堂列表
const getCanteenPage = (params) => {
    return $axios({
        url: 'canteen/page',
        method: 'get',
        params
    })
}
// 删除数据接口
const deleteCanteen = (ids) => {
    return $axios({
        url: '/canteen',
        method: 'delete',
        params: {ids}
    })
}

// 修改数据接口
const editCanteen = (params) => {
    return $axios({
        url: '/canteen',
        method: 'put',
        data: {...params}
    })
}

// 新增数据接口
const addSetmeal = (params) => {
    return $axios({
        url: '/setmeal',
        method: 'post',
        data: {...params}
    })
}

// 新增饭堂
const addCanteen = (params) => {
    return $axios({
        url: '/canteen',
        method: 'post',
        data: {...params}
    })
}

// 查询详情接口
const queryCanteenById = (id) => {
    return $axios({
        url: `/canteen/${id}`,
        method: 'get'
    })
}

// 批量起售禁售
const setmealStatusByStatus = (params) => {
    return $axios({
        url: `/setmeal/status/${params.status}`,
        method: 'post',
        params: {ids: params.ids}
    })
}

// 批量开业停业
const canteenStatusByStatus = (params) => {
    return $axios({
        url: `/canteen/status/${params.status}`,
        method: 'post',
        params: {ids: params.ids}
    })
}

//``是模板字符串不同于''