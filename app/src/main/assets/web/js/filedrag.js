/*
filedrag.js - HTML5 File Drag & Drop demonstration
Featured on SitePoint.com
Developed by Craig Buckler (@craigbuckler) of OptimalWorks.net
*/
var filelist = new Array();
var count = 0;
(function() {

	function $id(id) {
		return document.getElementById(id);
	}

	function Output(msg) {
		var m = $id("filelist");
		m.innerHTML = m.innerHTML + msg;
	}

	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : "");
	}

	function FileSelectHandler(e) {
		FileDragHover(e);
		var files = e.target.files || e.dataTransfer.files;

		for (var i=0, f; f = files[i]; i++) {
			filelist[filelist.length] = f;
			ParseFile(f,filelist.length-1);
		}
		funBindHoverEvent();
		submitbutton.style.display = "";

	}


	var funBindHoverEvent = function(){
		$("#filelist").find("li").on("mouseenter",function (e) {
				$(this).find("div[class='file-panel']").css("height","30px");
		});
		$("#filelist").find("li").on("mouseleave",function (e) {
				$(this).find("div[class='file-panel']").css("height","0px");
		});
	};

	function ParseFile(file,i) {
		Output(
			'<li id="uploadfile_'+i+'">'+
				'<p class="filename">'+file.name+'</p>'+
				'<div class="statusBar">'+
				    '<div class="progress">'+
				        '<span class="text" id="uploadtxt_'+i+'">0/'+file.size+'</span>'+
				        '<span class="percentage" id="uploadper_'+i+'" style="width: 0%;"></span>'+
				    '</div>'+
				'</div>'+
				'<div class="file-panel" height="0px">'+
					'<span class="cancel" onclick="deleteFile('+'uploadfile_'+i+')">删除</span>'+
				'</div>'+
			'</li>'
		);
	}
	
	function UploadFile(e){
		if(filelist.length != 0){
			for(var i =0,f,count=0;f=filelist[i];i++,count++){
				if($("#uploadfile_"+i).css('display') == "none"){
					continue;
				}
				startFileUpload(f,i);
			}
		}
	}



	function Init() {

		var fileselect = $id("fileselect"),
			filedrag = $id("filedrag"),
			submitbutton = $id("submitbutton");

		fileselect.addEventListener("change", FileSelectHandler, false);
		submitbutton.addEventListener("click",UploadFile,false);

		var xhr = new XMLHttpRequest();
		if (xhr.upload) {

			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";

			submitbutton.style.display = "none";
		}

	}


	if (window.File && window.FileList && window.FileReader) {
		Init();
	}
	

    function startFileUpload(file,i)
    {
    	var uploadURL = "/upload/put.action";
    	

    	var formData = new FormData();
    	formData.append("file" , file);

    	var request = new XMLHttpRequest();
    	request.upload.addEventListener("progress" , function (event) {window.evt_upload_progress(event,i)} , false);
	    request.addEventListener("load", function (event) { window.evt_upload_complete(event,i)}, false);
	    request.addEventListener("error", function (event) { window.evt_upload_failed(event,i)}, false);
	    request.addEventListener("abort", function (event) { window.evt_upload_cancel(event,i)}, false);
		request.open("POST", uploadURL );
	    request.send(formData);
    }
    window.evt_upload_progress = function(evt,i)
    {
    	if(evt.lengthComputable)
    	{
    		var progress = Math.round(evt.loaded * 100 / evt.total);
    		
    		$("#uploadtxt_"+i).text(evt.loaded+"/"+evt.total);
    		$("#uploadper_"+i).css("width",progress+"%");
    		
    		console.log("上传进度" + progress);
    	}
    };
	window.evt_upload_complete = function (evt,i)
	{
		if(evt.loaded == 0)
		{
			console.log ("上传失败!");
			$("#uploadtxt_"+i).text("failed");
			$("#uploadper_"+i).css("width","100%");
            $("#uploadper_"+i).css("background","red");
		}
		else
		{
			console.log ("上传完成!");
	    	var response = evt.target.responseText;
	    	console.log (response);
	    	$("#uploadtxt_"+i).text(response);
	    	$("#uploadper_"+i).css("width","100%");
		}			
	};		 
	window.evt_upload_failed = function (evt,i) 
	{
	    $("#uploadtxt_"+i).text("failed");
		$("#uploadper_"+i).css("width","100%");
		$("#uploadper_"+i).css("background","red");
		console.log ("上传出错");
	};
	window.evt_upload_cancel = function (evt,i)
	{
		console.log( "上传中止!");	
	};
})();