//提交订单
function  addOrderApi(data){
    return $axios({
        'url': '/order/submit',
        'method': 'post',
        data
      })
}

//查询所有订单
function orderListApi() {
  return $axios({
    'url': '/order/list',
    'method': 'get',
  })
}

//分页查询订单
function orderPagingApi(data) {
  return $axios({
      'url': '/order/userPage',
      'method': 'get',
      params:{...data}
  })
}

//再来一单
function orderAgainApi(data) {
  return $axios({
      'url': '/order/again',
      'method': 'post',
      data
  })
}

// 根据id查询一个orderDto
const queryOrderDtoById = (id) => {
    return $axios({
        url: '/order/getOrderById',
        method: 'get',
        params: {...id}
    })
}

changeDishRateApi = (data) => {
  return $axios({
      'url': '/order/changeDishRate',
      'method': 'post',
      data: {...data}
  })
}

const submitOrderComment = (data) => {
    return $axios({
        'url': '/order/submitComment',
        'method': 'post',
        data: {...data}
    })
}