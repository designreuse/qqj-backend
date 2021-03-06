
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}

$(document).ready(function(){
    var accessToken = '';
    var appId = '';
    $.ajax({
        url: "http://www.boruifangzhou.com/wechat?url="+window.location,
        type: "GET",
        success: function(data) {
            accessToken = data.accessToken;
            appId = data.appId;
            alert("signature:" + data.signature);

            wx.config({
                debug: false,
                appId: data.appId,
                timestamp: data.timestamp,
                nonceStr: data.noncestr,
                signature: data.signature,
                jsApiList: [
                    'checkJsApi',
                    'onMenuShareTimeline',
                    'onMenuShareAppMessage',
                    'onMenuShareQQ',
                    'onMenuShareWeibo',
                    'hideMenuItems',
                    'showMenuItems',
                    'hideAllNonBaseMenuItem',
                    'showAllNonBaseMenuItem',
                    'translateVoice',
                    'startRecord',
                    'stopRecord',
                    'onRecordEnd',
                    'playVoice',
                    'pauseVoice',
                    'stopVoice',
                    'uploadVoice',
                    'downloadVoice',
                    'previewImage',
                    'uploadImage',
                    'downloadImage',
                    'getNetworkType',
                    'openLocation',
                    'getLocation',
                    'hideOptionMenu',
                    'showOptionMenu',
                    'closeWindow',
                    'scanQRCode',
                    'chooseWXPay',
                    'openProductSpecificView',
                    'addCard',
                    'chooseCard',
                    'openCard',
		            'chooseImage',
                    'previewImage',
                    'uploadImage',
                    'downloadImage'
                ]
            });

	wx.ready(function(){

        //alert(222)
        $('#uploadBtn').click(function(){
                    var localIds = [],
                        serverId =[];
                    //alert(12211);
            wx.chooseImage({
                count: 2, // 默认9
                sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                    //alert(localIds+"////");
                    $.each( res.localIds, function(i, n){
                        //alert(n);
                        $(this).html('<img src="'+n.toString()+'" /> <br />')
                        $(this).find('img').css({
                             'height':'100%',
                             'width': '100%'
                         })
                    });
                },
                fail: function(res){
                //alert(112)
                }
            });

            $('#btn').click(function(){
                //alert(localIds);
                wx.uploadImage({
                    localId: localIds.toString(), // 需要上传的图片的本地ID，由chooseImage接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        serverId = res.serverId; // 返回图片的服务器端ID
                        alert('serverId:'+serverId);
                    },
                    fail: function(res){
                        alert(JSON.stringify(res))
                    }
                });
            });

            //调用保存后台的接口
            $('#saveBtn').click(function(){
                var user = {};
                user.serverIds = [serverId, serverId];
                user.name = 'tName';
                user.telephone = '13756648000';
                user.birthday = new Date(1986,3,7);
                user.code = code;
                alert(JSON.stringify(user))
                $.ajax({
                    url: "http://www.boruifangzhou.com/api/weixin/user/add",
                    type: "post",
                    data: JSON.stringify(user),
                    contentType: "application/json",
                    dataType: "json",
                    success: function(data) {
                        alert(JSON.stringify(data))
                        $(this).css('background','green');
                    },
                    error: function(res) {
                        alert(JSON.stringify(res));
                    }
                 })
            })
        })
	});
	wx.error(function(res){
		alert("error:" + JSON.stringify(res));
	})
        },
        error: function() {

        }
    });
});
