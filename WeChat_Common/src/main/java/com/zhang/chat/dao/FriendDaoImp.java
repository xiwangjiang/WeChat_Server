package com.zhang.chat.dao;

import com.zhang.chat.base.BaseDao;
import com.zhang.chat.dao.interfaces.FriendDao;
import com.zhang.chat.entity.response.Friend;
import com.zhang.chat.entity.sql.UserFriend;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ZhangYan
 * @Description:
 * @Date Create In: 2017/9/24 22:06
 * @Modified By:
 */
@Transactional(rollbackFor = Exception.class)
//出现Exception异常回滚
@Repository("friendDao") //进行注入
public class FriendDaoImp extends BaseDao implements FriendDao {


    public List<Friend> getFiendList(long user_id) {

        ArrayList<Friend> friendsList = new ArrayList<Friend>();
//
        List<Object[]> name = sessionFactory.getCurrentSession().createSQLQuery("SELECT t.m_id ,t.user_name,t.user_sex " +
                ",t.user_desc,t.user_phone,t.user_img_face_path, f.f_friend_type_id,f.f_friend_groups_id ,t.u_NationID,t.U_Province,t.U_City from user_friend_t f" +
                " LEFT JOIN  user_info_t t ON  f.f_firend_id=t.m_id" +
                "  where f.f_user_id = ?")
                .setParameter(0, user_id).list();
        for (Object[] obj : name) {
            Friend friend = new Friend(Long.parseLong(obj[0] + ""), (String) obj[1], (Integer) obj[2], (String) obj[3],
                    (String) obj[4], (String) obj[5], (String) obj[6], (Integer) obj[7],(String)obj[8],(String)obj[9],(String)obj[10]);
            friendsList.add(friend);
        }
        return friendsList;
    }

    public void addFriend(UserFriend friend) {
    }
}
