<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/resource/static/static.jsp"></jsp:include>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=0.5">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta name="renderer" content="webkit">
    <title>User Manage</title>
    <link rel="stylesheet" href="${ctx}/resource/common/font-awesome.css"/>
    <link rel="stylesheet" href="${ctx}/resource/common/element.css">
    <link rel="stylesheet" href="${ctx}/resource/css/base.css"/>
    <link rel="stylesheet" href="${ctx}/resource/css/queryPanel.css"/>
    <script src="${ctx}/resource/common/jquery-1.12.4.min.js"></script>
    <script src="${ctx}/resource/common/vue.js"></script>
    <script src="${ctx}/resource/common/vue-resource.min.js"></script>
    <script src="${ctx}/resource/common/element.js"></script>
    <script src="${ctx}/resource/common/array.js"></script>
	<script src="${ctx}/resource/common/store.modern.min.js"></script>
    <script src="${ctx}/resource/js/base.js"></script>
    <style type="text/css">
    *{margin:0;} 
	label{vertical-align:middle}
    	.widthcss{
    		width:168px;
    	}
     .el-checkbox{
		width: 180px;
		margin-left:15px;
		margin-top:5px;
	  }
	  .el-form-item__content{
	  	/* margin-left:10px!important; */
	  }
    </style>
</head>
<body>
<div id="app" class="v__addAnimationBg">
      <!-- 查询条件begin -->
	      <el-row style="padding:0px 0px;width: 100%;top: 8px;text-align:right;background-color:#FAFAFA;">
		        <el-col :span="24" >
		           	<el-form :model="queryForm" :inline="true"  >
					   <!--  <el-form-item label="username"  label-width="40px"  >
					    	<el-input clearable v-model="queryForm.username" ></el-input>
					    </el-form-item> -->
					    <el-button-group>
				 			<el-button type="primary" size="small" style="margin-top:8px;margin-right:20px;border-radius:4px;" @click="HandleDownFile" >Download File</el-button>
				 			<el-button type="primary" size="small" style="margin-top:8px;margin-right:20px;border-radius:4px;" @click="HandleSignOut" >Sign out</el-button>
						</el-button-group>
				  </el-form>
		        </el-col>
		     
		  </el-row>
	  <!-- 查询条件 end -->	
      <el-row style="position: absolute;top: 55px;bottom: 0;width: 100%">
        
        <!-- 列表 begin -->
         <el-col :span="24" style="height: 100%; position: relative;">
	   		<el-row class="v__tableDock" style="position: absolute;top: 0px;bottom: 30px;width: 100%">
		        <el-col :span="24">
		            <el-table :data="tableData" :height="v__tableDockHeight" stripe border style="width: 100%;" 
		            	v-loading.fullscreen.lock="tableLoading"  highlight-current-row 
				              @row-click="handleCurrentRow">
		                  <el-table-column  type="index" label="index" width="85" align="center"></el-table-column>
						  <el-table-column prop="username" label="username" min-width="100" align="center"></el-table-column>
						  <el-table-column prop="email" label="email" min-width="80" align="center"> </el-table-column>
						  <el-table-column prop="path" label="file path" min-width="150" align="center"> </el-table-column>
						  <el-table-column prop="operation" label="operation" min-width="80" align="center">
						  <template slot-scope="scope">
		                            <span class="el-tag el-tag--info el-tag--mini" style="cursor: pointer;" @click="HandleEdit(scope.row.id)">
		                               edit 	
		                            </span>
		                            <span class="el-tag el-tag--info el-tag--mini" style="cursor: pointer;" @click="HandleDelete(scope.row.id)">
		                               delete 	
		                            </span>
		                    </template>
						  </el-table-column>
		            </el-table>
		        </el-col>
		        
		    </el-row>
		    <el-row style="position: absolute;bottom: 0;">
		        <el-col :span="24">
					<el-pagination
				       @size-change="handleSizeChange"
		       		   @current-change="handleCurrentChange"
				      :page-sizes="[20, 50, 100]"
				      :page-size="page.pagesize"
				      layout="total, sizes, prev, pager, next, jumper"
				      :total="page.total" style="margin-top:10px;">
				    </el-pagination>
		        </el-col>
		    </el-row>
        </el-col> 
      <!-- 列表end -->
      
      
    <!-- 编辑 begin -->
    <el-dialog title="edit File Path" size="small" :visible.sync="dialogEditFormVisible"
               :modal-append-to-body="false" :close-on-click-modal="false" top="5%">
        <el-form :model="editform" :rules="rules" ref="editform" >

            <el-form-item>
                <el-col :span="24">
                    <el-form-item label="FilePath" :label-width="formLabelWidth"  prop="path" style="margin-top:5px;">
                        <el-input v-model="editform.path" auto-complete="off" ></el-input>
                    </el-form-item>
                </el-col>
            </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="dialogEditFormVisible = false">cancel</el-button>
            <el-button type="primary" size="small" :disabled="doclickEdit" @click="saveEdit('editform')">confirm</el-button>
        </div>
    </el-dialog>
    <!-- 编辑 end -->
      
      
  </el-row> 		
</div>
</body>
<script type="text/javascript">
	Vue.http.options.emulateJSON = true;
	var vo = new Vue({
		el:"#app",
		data:function(){
			return{
				token:store.get('token'),
				fileFormat:'.jpg,.png,.jpeg',//只能上传这2中格式的图片
				action:'',
				id:'',
				path:'',
				dialogImageUrl:'',
				fileList:[],
				tableData:[],
				depOptions:[],
				natureOptions:[],
				dutyOptions:[],
				postOptions:[],
				v__tableDockHeight:0,
				formLabelWidth:'110px',
				dialogAddFormVisible: false,
				dialogAddLwFormVisible: false,
				dialogEditFormVisible:false,
				doclickEdit:false,
				tableLoading: false,
				checkLoading: false,
				dlgAddRoleVisible:false,
				fullscreenLoading1: false,
				currentRow:null,//表格当前行
				defaultCheckedKeys:[],//默认选中角色
				disabledCheckbox:true,//角色列表是否只读
				disabledOrg:false,
				disabledLw:false,
				addUploadDialog:false,
				dialogVisible:false,
				doclickAdd:false,
		        page:{
		        	total:0 ,
		        	pagesize:20,
		        	currentpage:1
		        },
		        rolelist:[],
		        addRoleForm:{
		        	checkList: [],
		        },
		        queryForm:{},
		        editform:{
		        	path:''
		        },
   		     	rules: {
   		     		path: [ { required:true,message: 'Please enter the file path'}]
		        }
			}
		},
		mounted:function(){
            this.$nextTick(function() {
                vo.bindTableData();
			  })
		},
		methods:{
	 		 bindTableData: function(){//表格数据
	 			//vo.tableLoading=true;
	 			vo.currentRow=null;
				vo.disabledCheckbox=true;
			    vo.$http.get("${ctx}/userlist",
						 {   params: {  
						  		"pagesize":vo.page.pagesize,
						        "currentpage":vo.page.currentpage
						    }}).then(function(res){ 
						    	
						 vo.page.currentpage=res.body.currentpage;
						 vo.path=res.body.path;
					 	 vo.page.total=res.body.total;
						 vo.tableData=res.body.data;  
						 vo.tableLoading=false;
				 });   
			},
			HandleEdit:function(id){
				vo.id=id;
				this.dialogEditFormVisible = true;
                this.resetForm();	// 重置表单
                vo.$http.get("${ctx}/getFilePathByid",
                    {   params: { "id":id}
                    }).then(function(res){
                    vo.editform=res.body;
                });
			},
			saveEdit:function(editform){
                vo.$refs[editform].validate(function(valid) {
                    if (valid) {
                        vo.doclickEdit = true;
                        vo.editform["id"] = vo.id;
                        var formData = JSON.stringify(vo.editform);
                        var url="${ctx}/updateFilePath";
                        vo.$http.post(url,formData ).then(function(res){
                            if(res.body.rt){
                                vo.$message({
                                    message: 'Editorial success！',
                                    duration:1000,
                                    type: 'success',
                                    onClose:function(){
                                        vo._data.dialogEditFormVisible = false;
                                        vo.path=vo.editform.path;
                                        vo.bindTableData();
                                        vo.doclickEdit = false;
                                    }
                                });
                            }else{
                                vo.$message({
                                    message: 'Editor failure！',
                                    duration:2000,
                                    type: 'error',
                                });
                                vo.doclickEdit = false;
                            }
                        });
                    } else {
                        return false;
                        vo.doclickEdit = false;
                    }
                });
                
			},
			HandleDelete:function(id){
				vo.$http.get("${ctx}/deleteUserById",
						 {   params: {"id":id}
						    }).then(function(res){ 
						    	
						    	if(res.body.rs){
						    		vo.$message({
				                        message: 'Delete Successful!',
				                        duration:2000,
				                        type: 'success' ,
				                        onClose:function(){
				                        	window.location.href = "${ctx}/logout";
	                                    }
				                    }); 
						    	}else{
						    		vo.$message({
				                        message: 'Delete Failed!',
				                        duration:2000,
				                        type: 'error' ,
				                    });
						    	}
						    	
		 		}); 
			},
			HandleDownFile:function(){
				//window.location.href="www.baidu.com";
				 vo.$confirm('Are you sure you want to download the file?', 'Tips', {
	                confirmButtonText: 'Sure',
	                cancelButtonText: 'cancel',
	                type: 'warning'
	              }).then(function() {
	            	  vo.$http.get("${ctx}/getFilePath",
	 						 {   params: {}
	 						    }).then(function(res){ 
	 						    	if(res.body.rs){
	 						    		window.open(res.body.path,"_blank");
	 						    	}else{
	 						    		vo.$message({
	 				                        message: res.body.msg,
	 				                        duration:2000,
	 				                        type: 'error' ,
	 				                    });
	 						    	}
	 						    	
	 		 			}); 
	            	  
	              }) 
			}, 
			HandleSignOut:function(){
				window.location.href = "${ctx}//logout";
			},
			handleCurrentRow: function(row){//点击表格行
				vo.currentRow=row;
			},
			handleCurrentChange: function(val){//当前页改变
				vo.page.currentpage=val;//当前页
				vo.bindTableData();
			 },
			handleSizeChange:function(val) {//每页显示多少条
				 vo.page.pagesize=val;
				 vo.page.currentpage=1;
				 vo.bindTableData();
		      },
			HandleAdd:function(){//新增
	 			this.dialogAddFormVisible = true;
	 			this.resetForm();	// 重置表单
			},
			 resetForm:function() {// 重置表单
	             if(this.$refs.hasOwnProperty('editform')){
	                 this.$refs.editform.resetFields();
	             }
	         },
		}
		});
		

	 Hp.createNew(vo)
</script>