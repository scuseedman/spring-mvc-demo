package product.service;
import java.util.Map;

import org.junit.Test;

import cn.tedu.ttms.product.service.TeamService;
public class TestTeamService extends TestBase{
	 @Test
	 public void testFindPageObjects(){
		 //1.获得TeamService对象
		 TeamService teamService=
		 ctx.getBean("teamServiceImpl",TeamService.class);
		 //2.调用TeamService分页查询的方法
		 Map<String,Object> map=
		 teamService.findPageObjects("环球",1);
		 //3.对返回结果进行验证
		 System.out.println(map);
	 }
}
