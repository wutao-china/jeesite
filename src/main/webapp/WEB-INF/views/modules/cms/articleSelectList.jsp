<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>选择文章</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("input[name=id]").each(function(){
				var articleSelect = null;
				if (top.mainFrame.cmsMainFrame){
					articleSelect = top.mainFrame.cmsMainFrame.articleSelect;
				}else{
					articleSelect = top.mainFrame.articleSelect;
				}
				for (var i=0; i<articleSelect.length; i++){
					if (articleSelect[i][0]==$(this).val()){
						this.checked = true;
					}
				}
				$(this).click(function(){
					alert(11);
					var id = $(this).val(), title = $(this).attr("title");
					if (top.mainFrame.cmsMainFrame){
						top.mainFrame.cmsMainFrame.articleSelectAddOrDel(id, title);
					}else{
						top.mainFrame.articleSelectAddOrDel(id, title);
					}
				});
			});
		});
		function view(href){
			$.jBox.open('iframe:'+href,'查看文章',$(top.document).width()-220,$(top.document).height()-120,{
				buttons:{"关闭":true},
				loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
					$(".nav,.form-actions,[class=btn]", h.find("iframe").contents()).hide();
				}
			});
			return false;
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<div style="margin:10px;">
	<section class="content" style="background: redl;padding: 0px">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
			<form:form id="searchForm" modelAttribute="article" action="${ctx}/cms/article/selectList" method="post" class="form-horizontal">
				<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
				<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
				<div class="box-body">
				<div class="col-md-4">
					<div class="form-group">
						<label class="col-sm-4 control-label">栏目：</label>
						<div class="col-sm-8">
						<sys:treeselect id="category" name="category.id" value="${article.category.id}" labelName="category.name" labelValue="${article.category.name}"
							title="栏目" url="/cms/category/treeData" module="article" cssClass="form-control"/>
		                </div>
					</div>
				</div>
				<div class="col-md-4">
					<div class="form-group">
						<label class="col-sm-4 control-label">标题：</label>
						<div class="col-sm-6">
						<form:input path="title" htmlEscape="false" maxlength="50" class="form-control"/>&nbsp;
		                </div>
		                <div class="col-sm-5">
		                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
		                </div>
					</div>
				</div>
				
				</div>
			</form:form>
			<table id="contentTable" class="table table-bordered table-striped table-hover dataTable no-footer">
				<tr><th style="text-align:center;">选择</th><th>栏目</th><th>标题</th><th>权重</th><th>点击数</th><th>发布者</th><th>更新时间</th></tr>
				<c:forEach items="${page.list}" var="article">
					<tr>
						<td style="text-align:center;"><input type="checkbox" name="id" value="${article.id}" title="${fns:abbr(article.title,40)}" /></td>
						<td><a href="javascript:" onclick="$('#categoryId').val('${article.category.id}');$('#categoryName').val('${article.category.name}');$('#searchForm').submit();return false;">${article.category.name}</a></td>
						<td><a href="${ctx}/cms/article/form?id=${article.id}" title="${article.title}" onclick="return view(this.href);">${fns:abbr(article.title,40)}</a></td>
						<td>${article.weight}</td>
						<td>${article.hits}</td>
						<td>${article.createBy.name}</td>
						<td><fmt:formatDate value="${article.updateDate}" type="both"/></td>
					</tr>
				</c:forEach>
			</table>
					${page}					 
				</div>
			</div>
		</div>
	</section>
	</div>
</body>
</html>