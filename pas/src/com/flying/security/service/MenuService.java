package com.flying.security.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flying.common.annotation.CacheEvict;
import com.flying.common.annotation.Cacheable;
import com.flying.common.annotation.DaoCreate;
import com.flying.common.annotation.DaoQuery;
import com.flying.common.annotation.DaoRemove;
import com.flying.common.annotation.DaoUpdate;
import com.flying.common.util.Codes;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;
import com.flying.framework.service.AbstractService;

@Service("MenuService")
public class MenuService  extends AbstractService{
	private PermService permService;

	public void setPermService(PermService permService) {
		this.permService = permService;
	}

	//	@CacheEvict(tag="pas.Menu")
	@DaoCreate(entity="security.menu")
	public Data create(
			@Param(value="name", required=true)String name,
			@Param(value="url",required=false)String url,
			@Param(value="remarks",required=false)String remarks,
			@Param(value="perm_id",required=true)long perm_id,
			@Param(value="sort_id",required=true)long sort_id,
			@Param(value="parent_id",required=true)long parent_id) throws Exception {
		return null;
	}

//	@CacheEvict(tag="pas.Menu")
	@DaoUpdate(entity="security.menu")
	public Data update(
			@Param(value="name",required=true)String name,
			@Param(value="url",required=false)String url,
			@Param(value="remarks",required=false)String remarks,
			@Param(value="perm_id",required=true)long perm_id,
			@Param(value="sort_id",required=true)long sort_id,
			@Param(value="parent_id",required=true)long parent_id,
			@Param(value="menu_id",required=true)long menu_id) throws Exception {
		return null;
	}

//	@CacheEvict(tag="pas.Menu")
	@DaoRemove(entity="security.menu")
	public Data remove(
			@Param(value="menu_id",required=true)long menu_id) throws Exception {
		return null;
	}

	@DaoQuery(entity="security.menu")
	public Data findAll(Data request) throws Exception {
		return request;
	}

	@DaoQuery(entity="security.menu",osql="order by sort_id")
	public Data findByParent(@Param(value="parent_id",required=true)long parent_id) throws Exception {
		return null;
	}

//	@CachePut(tag="pas.Menu", keys="url")
	@DaoQuery(single=true, entity="security.menu")
	public Data findByUrl(@Param(value="url",required=true)String url) throws Exception {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Data findTreeByParent(@Param(value="parent_id",required=true)long parent_id) throws Exception {
		final List<Map> rows = findByParent(parent_id).get("rows");
		for(Iterator<Map> it = rows.iterator(); it.hasNext(); ) {
			Map map = it.next();
			long menu_id = (Long)map.get("menu_id");
			map.put("id", String.valueOf(menu_id));
			map.put("text", map.get("name"));
			
			if(parent_id == menu_id) map.put("selected", true);
			List<Data> children = findTreeByParent(menu_id).get("rows");
			if(children != null && !children.isEmpty())
				map.put("children", children);
		}
		return new Data(Codes.CODE, Codes.SUCCESS, "rows", rows, "$value", "rows");
	}

//	@CachePut(tag="pas.Menu", keys="user_id")
	@SuppressWarnings("rawtypes")
	public Data findTreeByUser(
			@Param(value="user_id",required=true)long user_id) throws Exception {
		final List<Map> permList = permService.findByUser(user_id).get("rows");
		final Set<Long> permIds = Utils.newHashSet();
		for(Map m: permList) {
			permIds.add((Long) m.get("perm_id"));
		}
		return findTreeByPerms(permIds, 0);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Data findTreeByPerms(Set<Long> permIds, long parent_id) throws Exception {
		final List<Map> rows = findByParent(parent_id).get("rows");
		for(Iterator<Map> it = rows.iterator(); it.hasNext(); ) {
			Map map = it.next();
			long menu_id = (Long)map.get("menu_id");
			long perm_id = (Long)map.get("perm_id");
			if(!permIds.contains(perm_id)) {
				it.remove();
				continue;
			}
			map.put("id", String.valueOf(menu_id));
			map.put("text", map.get("name"));
			
			List<Data> children = findTreeByPerms(permIds, menu_id).get("rows");
			if(children != null && !children.isEmpty())
				map.put("children", children);
		}
		return new Data(Codes.CODE, Codes.SUCCESS, "rows", rows, "$value", "rows");
	}
	//------------------------------------------------------------------------------------------------------


	@DaoQuery(entity="security.menu2",osql="order by sort_id")
	public Data find2ByParentId(@Param(value="parent_id",required=true)long parent_id) throws Exception {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Data findMyMenu(@Param(value="parent_id",required=true)long parent_id) throws Exception {
		Principal p = ServiceContext.getContext().getPrincipal();
		long user_id = p == null?0:Long.parseLong(p.getId());
		final List<Map> permList = permService.findByUser(user_id).get("rows");
		final Set<Long> permIds = Utils.newHashSet();
		for(Map m: permList) {
			permIds.add((Long) m.get("perm_id"));
		}
		//
		List<Map<String, Object>> rows = this.find2ByParentId(parent_id).getRows(Codes.ROWS);
		for(Iterator<Map<String, Object>> it = rows.iterator(); it.hasNext(); ) {
			Map<String, Object> map = it.next();
			long menu_id = (Long)map.get("menu_id");
			long perm_id = (Long)map.get("perm_id");
			//鉴权
			if(!permIds.contains(perm_id)) {
				it.remove();
				continue;
			}
			//搜索子对象
			List<Map<String, Object>> children = this.find2ByParentId(menu_id).getRows(Codes.ROWS);
			for(Iterator<Map<String, Object>> cit = children.iterator(); cit.hasNext(); ) {
				Map<String, Object> cmap = cit.next();
				long cperm_id = (Long)cmap.get("perm_id");
				//鉴权
				if(!permIds.contains(cperm_id)) {
					cit.remove();
					continue;
				}
			}
			//如果非叶子
			if(!children.isEmpty()) {
				map.put("children", children);
			}
		}
		return new Data(Codes.CODE, Codes.SUCCESS, "rows", rows, "$value", "rows");
	}
}
