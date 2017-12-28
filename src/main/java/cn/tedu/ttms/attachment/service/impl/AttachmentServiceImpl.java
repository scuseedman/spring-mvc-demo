package cn.tedu.ttms.attachment.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import cn.tedu.ttms.attachment.dao.AttachmentDao;
import cn.tedu.ttms.attachment.entity.Attachment;
import cn.tedu.ttms.attachment.service.AttachmentService;
import cn.tedu.ttms.common.exception.ServiceException;

@Service
public class AttachmentServiceImpl implements AttachmentService {
	@Autowired
	private AttachmentDao attachmentDao;
	/**实现文件上传
	 * 1)将文件存储到服务器
	 * 2)将文件信息存储到数据库
	 * */
	@Override
	public Attachment findObjectById(Integer id) {
		if(id==null)
		throw new ServiceException("id不能为空");
		Attachment a=attachmentDao.findObjectById(id);
		if(a==null)
		throw new ServiceException("对象已经不存在");
		return a;
	}
	@Override
	public void saveOject(String title,
			MultipartFile mFile) {
		//1.验证参数的有效性
		if(StringUtils.isEmpty(title))
		throw new ServiceException("title 不能为空");
		if(mFile==null)
		throw new ServiceException("请选择上传文件");
		if(mFile.isEmpty())
		throw new ServiceException("不允许上传空文件");
		//2.判定文件是否已上传(根据摘要信息)
		//2.1)根据mFile内容生成摘要信息(MD5)
		String digest=null;
		try{
		byte []bytes=mFile.getBytes();
		digest=//摘要字符串
		DigestUtils.md5DigestAsHex(bytes);
		}catch(Exception e){
		e.printStackTrace();
		throw new ServiceException("文件上传失败");
		}
		//2.2)根据摘要信息进行数据库查询
		int count=
		attachmentDao.getRowCountByDigest(digest);
		//2.3)根据查询的结果判定文件是否已上传
		if(count>0)
		throw new ServiceException("文件已经上传");
		//3.假如文件不在则上传文件
		SimpleDateFormat sdf=
		new SimpleDateFormat("yyyy/MM/dd");
		String dateDir=sdf.format(new Date());
		File fileDir=new File("e:/uploads/"+dateDir);
		//判定目录是否存在,不存在则创建
		if(!fileDir.exists())fileDir.mkdirs();
		//构建文件对象(fileDir为目录,mFile.getOriginalFilename()文件名)
		File dest=new File(fileDir,
				           mFile.getOriginalFilename());
		try{
		//上传文件
		mFile.transferTo(dest);
		}catch(Exception e){
		e.printStackTrace();
		throw new ServiceException("文件上传失败");
		}
		//4.将文件信息保存到数据库
		Attachment a=new Attachment();
		a.setTitle(title);
		a.setFileName(mFile.getOriginalFilename());
		a.setFileDisgest(digest);
        a.setFilePath(dest.getPath());	
        a.setContentType(mFile.getContentType());
        int rows=attachmentDao.insertObject(a);
		//5.验证保存结果
        if(rows<=0)
        throw new ServiceException("数据保存失败");
	}
	@Override
	public List<Attachment> findObjects() {
		return attachmentDao.findObjects();
	}

}
