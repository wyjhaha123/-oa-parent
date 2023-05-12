package com.wyj.auth;

import com.wyj.auth.mapper.SysRoleMapper;
import com.wyj.model.system.SysRole;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestDemo1 {



    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void getAll(){

        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        System.out.println(sysRoles);
    }

    @Test
    public  void  add(){
        SysRole sysRole=new SysRole();
        sysRole.setRoleName("角色管理员1");
        sysRole.setRoleCode("role1");
        sysRole.setDescription("角色管理员1");

        int result = sysRoleMapper.insert(sysRole);
        System.out.println(result); //影响的行数
        System.out.println(sysRole); //id自动回填
    }

    @Test
    public void update(){
        SysRole sysRole = sysRoleMapper.selectById(10);
        sysRole.setRoleName("atguigu角色管理");
        int i = sysRoleMapper.updateById(sysRole);
        System.out.println(i); //影响的行数
        System.out.println(sysRole+"xxxxxxxxxxx"+sysRole.getId()); //id自动回填
    }

    @Test
    public void test(){
        SFunction<SysRole, String> getRoleName = SysRole::getRoleName;


        SysRole sysRole =new SysRole();
        String apply = getRoleName.apply(sysRole);
        System.out.println(apply);

    }

    @Test
    public  void testlist(){
        Users users1 = new Users(5L,1L);
        Users users2 = new Users(4L,2L);
        Users users3 = new Users(3L,4L);
        Users users4 = new Users(1L,3L);
        Users users5 = new Users(2L,5L);

        List<Users> list=new ArrayList<>();
        list.add(users2);
        list.add(users1);
        list.add(users3);
        list.add(users4);
        list.add(users5);
        for (Users users:list){
            System.out.println(users.id+"----"+users.id2);
        }
    }

@Data
public class Users{
        public Long id;
        public Long id2;

        public Users(Long id,Long id2){
            this.id=id;
            this.id2=id2;
        }

    public Users() {

    }
}

@Test
public void test1(){
        int[] arry={1,2,3,4,6,7};
    int i = binarySearch(arry, 6);
    System.out.println(i);
}


    public int binarySearch(int[] nums, int target) {
        if (nums == null || nums.length == 0) { //数组为空
            return -1;
        }
        int l = 0, r = nums.length - 1; //设置左右边界
        System.out.println(" nums.length:"+nums.length );
        while (l <= r) {
            int mid = l + (r - l) / 2; // 等同于mid=(l+r)/2,这种写法是为了防止数组越界,也可以写为(l+r) >>> 1

            if (nums[mid] == target) { //最终target=mid,输出mid
                System.out.println(" mid :"+mid );
                System.out.println(" target :"+target );
                System.out.println(" nums[mid] :"+nums[mid] );
                return mid;
            } else if (nums[mid] < target) { //目标值在(mid,r]之间
                l = mid + 1;
            } else {  //目标值在[l,mid)之间
                r = mid - 1;
            }
        }
        // 最后判断: l>r 即数组不存在
        return -1;

    }


}
