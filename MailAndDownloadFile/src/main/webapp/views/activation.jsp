<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/resource/static/static.jsp"></jsp:include>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=0.5">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta name="renderer" content="webkit">
    <title>Activation</title>
    <link rel="stylesheet" href="${ctx}/resource/common/font-awesome.css"/>
    <link rel="stylesheet" href="${ctx}/resource/common/element.css"/>
    <link rel="stylesheet" href="${ctx}/resource/css/base.css"/>
    <script src="${ctx}/resource/common/vue.js"></script>
    <script src="${ctx}/resource/common/vue-resource.min.js"></script>
    <script src="${ctx}/resource/common/element.js"></script>
    <script src="${ctx}/resource/common/store.modern.min.js"></script>
    <script src="${ctx}/resource/js/base.js"></script>
    <link rel="stylesheet" href="${ctx}/resource/layui/css/layui.css"/>
    <script src="${ctx}/resource/layui/layui.all.js"></script>

    <style>
        body {
            padding: 0;
            margin: 0;
        }

        body, input {
            font-family: sans-serif;
        }

        .pad {
            padding-left: 15%;
            padding-right: 15%;
        }

        #login_main .el-tabs__nav {
            width: 100%;
        }

        #login_main .el-tabs__item {
            width: 101%; /*51%;*/
            text-align: center;
            font-size: 14pt;
        }

        #login_bottom {
            text-align: center;
            margin: 1em;
            font-size: 12pt;
        }

        .el-input {
            font-size: 11pt;
        }

        .el-input .el-input__inner {
            height: 40px;
        }

        .el-button {
            padding: 9pt 11pt;
            font-size: 12pt;
            border-radius: 4pt;
        }
    </style>
</head>
<body>
<div id="app" class="v__addAnimationBg" style="margin-top: 50px;">
    <div style="margin-left:20%;margin-top:50px;font-size:28px;"><pre v-html="content.txt"></pre></div>
</div>
</body>
<script>
    Vue.http.options.emulateJSON = true;
    var vo = new Vue({
        el: "#app",
        data: function () {
            return {
            	msg:'',
           	  	content:{
                     txt:"Activating......"
                }
            }
        },
        mounted: function () {
            this.$nextTick(function () {
            	this.toActivating();
            })
        },
        methods: {
        	toActivating:function(){
        		var code='${code}';
        		var userid='${userid}';
        		vo.$http.get("${ctx}/ActiveController/activation",
                        {   params: { code:code,userid:userid}
                        }).then(function(res){
                        	vo.content.txt=res.body.msg;
                        	setTimeout( "window.opener=null;window.close() ",10000);
                        	if(res.body.rs){
                                location.href='${ctx}/toLogin';
                            }                      	
                    	});
        		
        		
        	}
        }
    });

    Hp.createNew(vo);
</script>
</html>
