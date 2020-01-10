
{foreach from=$rows item=r}
{if !$r.children}
	<li><a href="javascript:window.parent.openTab('{$r.name}','/cms/cms/contents.shtml?category_id={$r.category_id}')" style="text-decoration:none;"><span>{$r.name}</span></a></li>
{else}
	<li data-options="state:'closed'">
		<span>{$r.name}</span>
		<ul>
		{widget template="statics/include/category.tpl" moduleId="cms" serviceId="CategoryService:findChildrenTree" params="parent_id=`$r.category_id`"}
		</ul>
	</li>
{/if}
{/foreach}
