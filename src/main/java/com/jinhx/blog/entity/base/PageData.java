package com.jinhx.blog.entity.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页实体类
 *
 * @author jinhx
 * @since 2018-10-17
 */
@Data
@NoArgsConstructor
public class PageData implements Serializable {

	private static final long serialVersionUID = 9199167140939378054L;

	/**
	 * 总记录数
	 */
	private long totalCount;

	/**
	 * 每页记录数
	 */
	private long pageSize;

	/**
	 * 总页数
	 */
	private long totalPage;

	/**
	 * 当前页数
	 */
	private long currPage;

	/**
	 * 列表数据
	 */
	private List<?> list;
	
	/**
	 * 分页
	 * @param list        列表数据
	 * @param totalCount  总记录数
	 * @param pageSize    每页记录数
	 * @param currPage    当前页数
	 */
	public PageData(List<?> list, int totalCount, int pageSize, int currPage) {
		this.list = list;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		totalPage = (int) Math.ceil((double)totalCount/pageSize);
	}

	/**
	 * 分页
	 *
	 * @param page page
	 */
	public PageData(IPage<?> page) {
		list = page.getRecords();
		totalCount = (int) page.getTotal();
		pageSize = page.getSize();
		currPage = page.getCurrent();
		totalPage = (int) page.getPages();
	}
	
}
