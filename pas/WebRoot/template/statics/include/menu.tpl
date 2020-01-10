
{foreach from=$rows item=r}
{if !$r.children}
	<li><div onclick="window.parent.openTab('{$r.name}','{$r.url}')">{$r.name}</div></li>
{else}
	<li data-options="state:'closed'">
		<span>{$r.name}</span>
		<ul>
		{if $r.name eq '内容管理'}
		{widget template="statics/include/category.tpl" moduleId="cms" serviceId="CategoryService:findChildrenTree" params="parent_id=0"}
		{/if}
        {widget template="statics/include/menu.tpl" moduleId="pas" serviceId="MenuService:findMyMenu" params="parent_id=`$r.menu_id`"}
		</ul>
	</li>
{/if}
{/foreach}
