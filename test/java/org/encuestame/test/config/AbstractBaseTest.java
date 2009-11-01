/**
 * encuestame: system online surveys Copyright (C) 2009 encuestame Development
 * Team
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of version 3 of the GNU General Public License as published by the
 * Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.encuestame.test.config;

import java.util.Date;

import org.encuestame.core.persistence.dao.imp.ICatState;
import org.encuestame.core.persistence.dao.imp.ISecGroups;
import org.encuestame.core.persistence.dao.imp.ISecPermissionDao;
import org.encuestame.core.persistence.dao.imp.ISecUserDao;
import org.encuestame.core.persistence.pojo.CatState;
import org.encuestame.core.persistence.pojo.SecGroups;
import org.encuestame.core.persistence.pojo.SecPermission;
import org.encuestame.core.persistence.pojo.SecUserPermission;
import org.encuestame.core.persistence.pojo.SecUserPermissionId;
import org.encuestame.core.persistence.pojo.SecUsers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Class to Test Cases.
 *
 * @author Picado, Juan juan@encuestame.org
 * @since October 15, 2009
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager" ,defaultRollback = true)
@Transactional
@Scope("singleton")
@ContextConfiguration(locations = {
        "classpath:encuestame-application-context.xml",
        "classpath:encuestame-beans-jsf-context.xml",
        "classpath:encuestame-hibernate-context.xml",
        "classpath:encuestame-email-context.xml",
        "classpath:encuestame-param-context.xml"
         })
public class AbstractBaseTest extends AbstractTransactionalDataSourceSpringContextTests {

    /** State Catalog Dao. **/
    @Autowired
    private ICatState catStateDaoImp;

    /** User Security Dao. **/
    @Autowired
    private ISecUserDao secUserDao;

    /**Group Security Dao*/
    @Autowired
    private ISecGroups groupDao;

    /** Security Permissions Dao. **/
    @Autowired
    private ISecPermissionDao secPermissionDaoImp;
    /**
     * @return the catStateDaoImp
     */
    public ICatState getCatStateDaoImp() {
        return catStateDaoImp;
    }

    /**
     * @param catStateDaoImp the catStateDaoImp to set
     */
    public void setCatStateDaoImp(final ICatState catStateDaoImp) {
        this.catStateDaoImp = catStateDaoImp;
    }

    /**
     * @return the userDao
     */
    public ISecUserDao getSecUserDao() {
        return secUserDao;
    }

    /**
     * @param userDao the userDao to set
     */
    public void setSecUserDao(final ISecUserDao userDao) {
        this.secUserDao = userDao;
    }

    /**
     * @return
     */
    public ISecGroups getSecGroup(){
        return groupDao;
    }

    /**
     * @param groupDao
     */
    public void setgroupDao(final ISecGroups groupDao){
        this.groupDao = groupDao;
    }

    /**
     * @return the secPermissionDaoImp
     */
    public ISecPermissionDao getSecPermissionDaoImp() {
        return secPermissionDaoImp;
    }

    /**
     * @param secPermissionDaoImp the secPermissionDaoImp to set
     */
    public void setSecPermissionDaoImp(ISecPermissionDao secPermissionDaoImp) {
        this.secPermissionDaoImp = secPermissionDaoImp;
    }

    /**
     * Helper to create state.
     * @param name name of state
     * @return state
     */
    public CatState createState(final String name){
        final CatState state = new CatState();
        state.setDescState(name);
        state.setImage("image.jpg");
        catStateDaoImp.saveOrUpdate(state);
        return state;
    }
    /**
     * Helper to create User.
     * @param name user name
     * @return state
     */
    public SecUsers createUsers(final String name){
        final SecUsers user= new SecUsers();
        user.setName(name);
        user.setUsername(name+"_encuestame_test");
        user.setPassword("12345");
        user.setEmail(name+"@users.com");
        user.setDateNew(new Date());
        user.setStatus(true);
        getSecUserDao().saveOrUpdate(user);
        return user;
    }
    /**
     * Helper to create Group.
     * @param name user name
     * @return state
     */
    public SecGroups createGroups(final String groupname){
        final SecGroups group = new SecGroups();
        group.setName(groupname);
        group.setIdState(1);
        group.setDesInfo("Primer Grupo");
        getSecGroup().saveOrUpdate(group);
        return group;
    }

    /**
     * Helper to create Permission.
     * @param permissionName name
     * @return Permission
     */
    public SecPermission createPermission(final String permissionName){
        final SecPermission permission = new SecPermission();
        permission.setDescription(permissionName+" description");
        permission.setPermission(permissionName);
        getSecPermissionDaoImp().saveOrUpdate(permission);
        return permission;
    }

    /**
     * Helper to add permission to user.
     * @param user user
     * @param permission permission
     */
    public void addPermissionToUser(final SecUsers user, final SecPermission permission){
            final SecUserPermission userPerId = new SecUserPermission();
            final SecUserPermissionId id = new SecUserPermissionId();
            id.setIdPermission(permission.getIdPermission());
            id.setUid(user.getUid());
            userPerId.setId(id);
            userPerId.setState(true);
            getSecUserDao().saveOrUpdate(userPerId);
    }
}