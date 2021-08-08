package com.jinhx.blog.entity.sys.vo;

import com.jinhx.blog.entity.sys.SysMenu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * SysMenuVO
 *
 * @author jinhx
 * @since 2018-10-26
 */
@Data
public class SysMenuVO implements Serializable {

    private static final long serialVersionUID = 4714713612225824068L;

    List<SysMenu> menuList;

    Set<String> permissions;

}
