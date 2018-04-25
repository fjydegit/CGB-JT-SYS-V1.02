package com.jt.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jt.common.vo.JsonResult;
import com.jt.common.vo.PageObject;
import com.jt.sys.entity.SysUser;
import com.jt.sys.service.SysUserService;

@RequestMapping("/user/")
@Controller
public class SysUserController {
	@Autowired
    private SysUserService sysUserService;
	
	
	@RequestMapping("downloadAllUsersExcel")
	public void downloadAllUsersExcel(HttpServletRequest request, HttpServletResponse response){
		// 第一步，创建一个workbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("用户表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow(0);
		//设置表头行的高度
		row.setHeightInPoints(20);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 创建一个居中格式 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		//设置表头行的内容
		HSSFCell hssfCell = null;
		String[] titles={"id","username","password","salt","email","mobile",
				"valid","created Time","modified Time","created User","modified User"};
		for (int i = 0; i < titles.length; i++) {
			hssfCell = row.createCell(i);//列索引从0开始
			hssfCell.setCellValue(titles[i]);//列名
			hssfCell.setCellStyle(style);//列居中显示                
		}
		
		//设置指定列(索引从0开始)的宽度，第二个参数的单位是1/256个字符宽度，也就是说，这里是把该列的宽度设置为了n个字符
		sheet.setColumnWidth(4, 18 * 256);//email列宽
		sheet.setColumnWidth(5, 15 * 256);//mobile列宽
		sheet.setColumnWidth(7, 20 * 256);//createdTime列宽
		sheet.setColumnWidth(8, 20 * 256);//modifiedTime列宽
		sheet.setColumnWidth(9, 13 * 256);//createdUser列宽
		sheet.setColumnWidth(10, 13 * 256);//modifiedUse的列宽
		
		// 第五步，写入实体数据
		List<SysUser> list = sysUserService.findAllUsers();
		//转换时间数据
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (int i = 0; i < list.size(); i++){
			System.out.println(list.get(i));
			row = sheet.createRow(i + 1);
			 // 第六步，创建单元格，并设置值
			 row.createCell(0).setCellValue(list.get(i).getId());
			 row.createCell(1).setCellValue(list.get(i).getUsername());
			 row.createCell(2).setCellValue(list.get(i).getPassword());
			 row.createCell(3).setCellValue(list.get(i).getSalt());
			 row.createCell(4).setCellValue(list.get(i).getEmail());
			 row.createCell(5).setCellValue(list.get(i).getMobile());
			 row.createCell(6).setCellValue(list.get(i).getValid());
			 
			//转换时间数据
			 String createdTimeStr="";
			 if(list.get(i).getCreatedTime()!=null){
				 createdTimeStr=sdf.format(list.get(i).getCreatedTime());
			 }
			 row.createCell(7).setCellValue(createdTimeStr);
			//转换时间数据
			 String modifiedTimeStr="";
			 if(list.get(i).getModifiedTime()!=null){
				 modifiedTimeStr=sdf.format(list.get(i).getModifiedTime());
			 }
			 row.createCell(8).setCellValue(modifiedTimeStr);
			 
			 row.createCell(9).setCellValue(list.get(i).getCreatedUser());
			 row.createCell(10).setCellValue(list.get(i).getModifiedUser());
			
		}
		
		 // 第七步，将文件存到指定位置
		try{
			//设置文件名
            String fileName = "allUsers.xls";  
            fileName = URLEncoder.encode(fileName, "UTF-8");  
            //指示回复的内容该以何种形式展示，是以内联的形式（即网页或者页面的一部分），还是以附件的形式下载并保存到本地
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            //设置类型
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");    
            OutputStream fout =  response.getOutputStream();  
            wb.write(fout);    
            fout.flush();         
            fout.close();    
        }catch (Exception e){    
            e.printStackTrace();    
        }
		
	}
	
	
	
	
	
	@RequestMapping("downloadAllUsersPDF")
	public void downloadAllUsersPDF(HttpServletRequest request, HttpServletResponse response) throws DocumentException, IOException{
		
		//1创建document对象
		PdfWriter pdfWriter=null;
		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream bos= new ByteArrayOutputStream();
		//2、建立书写器（Writer）与文档对象（document）关联，通过书写器将文档写入磁盘
		PdfWriter.getInstance(document,bos);
		
		Font fontTitle = new Font(FontFamily.TIMES_ROMAN, 13);
		Font font = new Font(FontFamily.TIMES_ROMAN, 10);
		
		document.open();
		//创建表格
		
		String[] titles={"id","username","password","salt","email","mobile",
				"valid","created Time","modified Time","created User","modified User"};
		PdfPTable table = new PdfPTable(titles.length);
		for (int i = 0; i < titles.length; i++) {
			table.addCell(new Paragraph(titles[i],fontTitle));
		}
		
		// 写入实体数据
		List<SysUser> list = sysUserService.findAllUsers();
		
		
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for(int i = 0 ; i < list.size();i++){
			table.addCell(new Paragraph(list.get(i).getId().toString(),font));
			table.addCell(new Paragraph(list.get(i).getUsername(),font));
			table.addCell(new Paragraph(list.get(i).getPassword(),font));
			table.addCell(new Paragraph(list.get(i).getSalt(),font));
			table.addCell(new Paragraph(list.get(i).getEmail(),font));
			table.addCell(new Paragraph(list.get(i).getMobile(),font));
			table.addCell(new Paragraph(list.get(i).getValid().toString(),font));
			
			String createdTimeStr="";
			 if(list.get(i).getCreatedTime()!=null){
				 createdTimeStr=sdf.format(list.get(i).getCreatedTime());
			 }
			table.addCell(new Paragraph(createdTimeStr,font));
			
			 String modifiedTimeStr="";
			 if(list.get(i).getModifiedTime()!=null){
				 modifiedTimeStr=sdf.format(list.get(i).getModifiedTime());
			 }
			table.addCell(new Paragraph(modifiedTimeStr,font));
			
			
			table.addCell(new Paragraph(list.get(i).getCreatedUser(),font));
			table.addCell(new Paragraph(list.get(i).getModifiedUser(),font));
			
			
			
			
			
		}
		
		document.add(table);
		document.close();
		
		String fileName ="allUserPDF.pdf";
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
		response.setContentType("application/pdf");
		response.setContentLength(bos.size());
						
		OutputStream out1=response.getOutputStream();
		bos.writeTo(out1);
		out1.flush();
		out1.close();
	}
	
	
	
	
	
	
	
	
	@RequestMapping("listUI")
	public String listUI(){
		return "sys/user_list";
	}
	
	
	//@RequiresPermissions("sys:user:update")
	@RequestMapping("editUI")
	public String editUI(){
		return "sys/user_edit";
	}
	//@RequiresPermissions("sys:user:update")
	@RequestMapping("doUpdateObject")
	@ResponseBody
	public JsonResult doUpdateObject(SysUser entity,String roleIds){
		sysUserService.updateObject(entity,
				roleIds);
		return new JsonResult();
	}
	
	@RequestMapping("doFindObjectById")
	@ResponseBody
	public JsonResult doFindObjectById(
			Integer id){
		Map<String,Object> map=
		sysUserService.findObjectById(id);
		return new JsonResult(map);
	}
	
	
	@RequestMapping("doSaveObject")
	@ResponseBody
	public JsonResult doSaveObject(
			SysUser entity,String roleIds){
		sysUserService.saveObject(entity,
				roleIds);
		JsonResult r=new JsonResult();
		r.setMessage("save ok");
		return r;
	}
	
	@RequestMapping("doFindRoles")
	@ResponseBody
	public JsonResult doFindRoles(){
		return new JsonResult(
				sysUserService.findRoles());
	}
	
	@RequestMapping("doValidById")
	@ResponseBody
	public JsonResult doValidById(
		Integer id,Integer valid){
		sysUserService.validById(id, valid);
		return new JsonResult();
	}
	@RequestMapping("doFindPageObjects")
	@ResponseBody
	public JsonResult doFindPageObjects(
			Integer pageCurrent,String username){
		PageObject<SysUser> pageObject=
		sysUserService.findPageObjects(pageCurrent, username);
		return new JsonResult(pageObject);
	}
	
	
	
}
