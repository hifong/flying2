/**
 * The Patch for jQuery EasyUI 1.4.2
 */

(function($){
	$.extend($.fn.textbox.methods, {
	        setText: function(jq, value){
	                return jq.each(function(){
	                        var opts = $(this).textbox('options');
	                        var input = $(this).textbox('textbox');
	                        value = value == undefined ? '' : String(value);
	                        if ($(this).textbox('getText') != value){
	                                input.val(value);
	                        }
	                        opts.value = value;
	                        if (!input.is(':focus')){
	                                if (value){
	                                        input.removeClass('textbox-prompt');
	                                } else {
	                                        input.val(opts.prompt).addClass('textbox-prompt');
	                                }
	                        }
	                        $(this).textbox('validate');
	                });
	        }                
	})
})(jQuery);

(function($){
        function setValues(target, values, remainText){
                var state = $.data(target, 'combogrid');
                var opts = state.options;
                var grid = state.grid;
                
                var oldValues = $(target).combo('getValues');
                var cOpts = $(target).combo('options');
                var onChange = cOpts.onChange;
                cOpts.onChange = function(){};  // prevent from triggering onChange event
                var gOpts = grid.datagrid('options');
                var onSelect = gOpts.onSelect;
                var onUnselectAll = gOpts.onUnselectAll;
                gOpts.onSelect = gOpts.onUnselectAll = function(){};
                
                if (!$.isArray(values)){values = values.split(opts.separator)}
                var selectedRows = [];
                $.map(grid.datagrid('getSelections'), function(row){
                        if ($.inArray(row[opts.idField], values) >= 0){
                                selectedRows.push(row);
                        }
                });
                grid.datagrid('clearSelections');
                grid.data('datagrid').selectedRows = selectedRows;

                var ss = [];
                for(var i=0; i<values.length; i++){
                        var value = values[i];
                        var index = grid.datagrid('getRowIndex', value);
                        if (index >= 0){
                                grid.datagrid('selectRow', index);
                        }
                        ss.push(findText(value, grid.datagrid('getRows')) ||
                                        findText(value, grid.datagrid('getSelections')) ||
                                        findText(value, opts.mappingRows) ||
                                        value
                        );
                }

                opts.unselectedValues = [];
                var selectedValues = $.map(selectedRows, function(row){
                        return row[opts.idField];
                });
                $.map(values, function(value){
                        if ($.inArray(value, selectedValues) == -1){
                                opts.unselectedValues.push(value);
                        }
                });

                $(target).combo('setValues', oldValues);
                cOpts.onChange = onChange;      // restore to trigger onChange event
                gOpts.onSelect = onSelect;
                gOpts.onUnselectAll = onUnselectAll;
                
                if (!remainText){
                        var s = ss.join(opts.separator);
                        if ($(target).combo('getText') != s){
                                $(target).combo('setText', s);
                        }
                }
                $(target).combo('setValues', values);
                
                function findText(value, a){
                        for(var i=0; i<a.length; i++){
                                if (value == a[i][opts.idField]){
                                        return a[i][opts.textField];
                                }
                        }
                        return undefined;
                }
        }
        function setMe(target){
                var state = $.data(target, 'combogrid');
                var opts = state.options;
                var grid = state.grid;
                $.extend($(target).combogrid('grid').datagrid('options'), {
                        onLoadSuccess: function(data){
                                var values = $(target).combo('getValues');
                                // prevent from firing onSelect event.
                                var oldOnSelect = opts.onSelect;
                                opts.onSelect = function(){};
                                setValues(target, values, state.remainText);
                                opts.onSelect = oldOnSelect;
                                
                                opts.onLoadSuccess.apply(target, arguments);
                        },
                        onClickRow: onClickRow,
                        onSelect: function(index, row){retrieveValues(); opts.onSelect.call(this, index, row);},
                        onUnselect: function(index, row){retrieveValues(); opts.onUnselect.call(this, index, row);},
                        onSelectAll: function(rows){retrieveValues(); opts.onSelectAll.call(this, rows);},
                        onUnselectAll: function(rows){
                                if (opts.multiple) retrieveValues(); 
                                opts.onUnselectAll.call(this, rows);
                        }
                });
                function onClickRow(index, row){
                        state.remainText = false;
                        retrieveValues();
                        if (!opts.multiple){
                                $(target).combo('hidePanel');
                        }
                        opts.onClickRow.call(this, index, row);
                }
                function retrieveValues(){
                        var vv = $.map(grid.datagrid('getSelections'), function(row){
                                return row[opts.idField];
                        });
                        vv = vv.concat(opts.unselectedValues);
                        if (!opts.multiple){
                                vv = vv.length ? [vv[0]] : [''];
                        }
                        setValues(target, vv, state.remainText);
                }
        }
        var plugin = $.fn.combogrid;
        $.fn.combogrid = function(options, param){
                if (typeof options == 'string'){
                        return plugin.call(this, options, param);
                } else {
                        return this.each(function(){
                                plugin.call($(this), options, param);
                                setMe(this);
                        });
                }
        }
        $.fn.combogrid.defaults = plugin.defaults;
        $.fn.combogrid.methods = plugin.methods;
        $.fn.combogrid.parseOptions = plugin.parseOptions;

        $.extend($.fn.combogrid.defaults, {
                unselectedValues: [],
                mappingRows: [],
                filter: function(q, row){
                        var opts = $(this).combogrid('options');
                        return (row[opts.textField]||'').toLowerCase().indexOf(q.toLowerCase()) == 0;
                }
        });
        $.extend($.fn.combogrid.methods, {
                setValues: function(jq, values){
                        return jq.each(function(){
                                var opts = $(this).combogrid('options');
                                if ($.isArray(values)){
                                        values = $.map(values, function(value){
                                                if (typeof value == 'object'){
                                                        var v = value[opts.idField];
                                                        (function(){
                                                                for(var i=0; i<opts.mappingRows.length; i++){
                                                                        if (v == opts.mappingRows[i][opts.idField]){
                                                                                return;
                                                                        }
                                                                }
                                                                opts.mappingRows.push(value);
                                                        })();
                                                        return v;
                                                } else {
                                                        return value;
                                                }
                                        });
                                }
                                setValues(this, values);
                        });
                },
                setValue: function(jq, value){
                        return jq.each(function(){
                                $(this).combogrid('setValues', [value]);
                        });
                }
        });
})(jQuery);

(function($){
        function setBodySize(target){
                var state = $.data(target, 'datagrid');
                var opts = state.options;
                var dc = state.dc;
                var wrap = state.panel;
                var innerWidth = wrap.width();
                var innerHeight = wrap.height();
                
                var view = dc.view;
                var view1 = dc.view1;
                var view2 = dc.view2;
                var header1 = view1.children('div.datagrid-header');
                var header2 = view2.children('div.datagrid-header');
                var table1 = header1.find('table');
                var table2 = header2.find('table');
                
                // set view width
                view.width(innerWidth);
                var headerInner = header1.children('div.datagrid-header-inner').show();
                view1.width(headerInner.find('table').width());
                if (!opts.showHeader) headerInner.hide();
                view2.width(innerWidth - view1._outerWidth());
                view1.children()._outerWidth(view1.width());
                view2.children()._outerWidth(view2.width());
                
                // set header height
                var all = header1.add(header2).add(table1).add(table2);
                all.css('height', '');
                var hh = Math.max(table1.height(), table2.height());
                all._outerHeight(hh);
                
                // set body height
                dc.body1.add(dc.body2).children('table.datagrid-btable-frozen').css({
                        position: 'absolute',
                        top: dc.header2._outerHeight()
                });
                var frozenHeight = dc.body2.children('table.datagrid-btable-frozen')._outerHeight();
                var fixedHeight = frozenHeight + header2._outerHeight() + view2.children('.datagrid-footer')._outerHeight();
                wrap.children(':not(.datagrid-view,.datagrid-mask,.datagrid-mask-msg)').each(function(){
                        fixedHeight += $(this)._outerHeight();
                });
                
                var distance = wrap.outerHeight() - wrap.height();
                var minHeight = wrap._size('minHeight') || '';
                var maxHeight = wrap._size('maxHeight') || '';
                view1.add(view2).children('div.datagrid-body').css({
                        marginTop: frozenHeight,
                        height: (isNaN(parseInt(opts.height)) ? '' : (innerHeight-fixedHeight)),
                        minHeight: (minHeight ? minHeight-distance-fixedHeight : ''),
                        maxHeight: (maxHeight ? maxHeight-distance-fixedHeight : '')
                });
                
                view.height(view2.height());
        }

        var plugin = $.fn.datagrid;
        $.fn.datagrid = function(options, param){
                if (typeof options == 'string'){
                        return plugin.call(this, options, param);
                } else {
                        return this.each(function(){
                                var dg = $(this);
                                plugin.call(dg, options, param);
                                var opts = $.data(this, 'datagrid').options;
                                var panel = $(this).datagrid('getPanel');
                                panel.panel('options').onResize = function(width, height){
                                        setBodySize(dg[0]);
                                        dg.datagrid('fitColumns');
                                        opts.onResize.call(panel[0], width, height);
                                };
                                panel.panel('options').onExpand = function(){
                                        dg.datagrid('fixRowHeight').datagrid('fitColumns');
                                        opts.onExpand.call(panel[0]);
                                }
                        });
                }
        };
        $.fn.datagrid.defaults = plugin.defaults;
        $.fn.datagrid.methods = plugin.methods;
        $.fn.datagrid.parseOptions = plugin.parseOptions;
        $.fn.datagrid.parseData = plugin.parseData;

        $.extend($.fn.datagrid.defaults.view, {
                renderEmptyRow: function(target){
                        var body2 = $.data(target, 'datagrid').dc.body2;
                        body2.html(this.renderTable(target, 0, [{}], false));
                        body2.find('.datagrid-row').removeClass('datagrid-row').removeAttr('datagrid-row-index');
                        body2.find('tbody *').css({
                                height: 1,
                                borderColor: 'transparent',
                                background: 'transparent'
                        });
                        $(target).datagrid('autoSizeColumn');
                }
        });
        $.fn.treegrid.defaults.view.renderEmptyRow = $.fn.datagrid.defaults.view.renderEmptyRow;

        var setSelectionState = $.fn.datagrid.methods.setSelectionState;
        $.fn.datagrid.methods.setSelectionState = function(jq){
                return jq.each(function(){
                        setSelectionState($(this));
                        setBodySize(this);
                });
        }        
})(jQuery);

(function($){
	var plugin = $.fn.window;
	$.fn.window = function(options, param){
		if (typeof options == 'string'){
			return plugin.call(this, options, param);
		} else {
			return this.each(function(){
				plugin.call($(this), options, param);
				var state = $.data(this, 'window');
				if (state.mask && state.options.inline){
					state.mask.css({
						width: '100%',
						height: '100%'
					});
				}
			});
		}
	}
	$.fn.window.defaults = plugin.defaults;
	$.fn.window.methods = plugin.methods;
	$.fn.window.parseOptions = plugin.parseOptions;
})(jQuery);

(function($){
        function updateTab(container, param){
                param.type = param.type || 'all';
                var selectHis = $.data(container, 'tabs').selectHis;
                var pp = param.tab;     // the tab panel
                var opts = pp.panel('options'); // get the tab panel options
                var oldTitle = opts.title;
                $.extend(opts, param.options, {
                        iconCls: (param.options.icon ? param.options.icon : undefined)
                });

                if (param.type == 'all' || param.type == 'body'){
                        pp.panel();
                }
                if (param.type == 'all' || param.type == 'header'){
                        var tab = opts.tab;
                        
                        if (opts.header){
                                tab.find('.tabs-inner').html($(opts.header));
                        } else {
                                var s_title = tab.find('span.tabs-title');
                                var s_icon = tab.find('span.tabs-icon');
                                s_title.html(opts.title);
                                s_icon.attr('class', 'tabs-icon');
                                
                                tab.find('a.tabs-close').remove();
                                if (opts.closable){
                                        s_title.addClass('tabs-closable');
                                        $('<a href="javascript:void(0)" class="tabs-close"></a>').appendTo(tab);
                                } else{
                                        s_title.removeClass('tabs-closable');
                                }
                                if (opts.iconCls){
                                        s_title.addClass('tabs-with-icon');
                                        s_icon.addClass(opts.iconCls);
                                } else {
                                        s_title.removeClass('tabs-with-icon');
                                }
                                if (opts.tools){
                                        var p_tool = tab.find('span.tabs-p-tool');
                                        if (!p_tool.length){
                                                var p_tool = $('<span class="tabs-p-tool"></span>').insertAfter(tab.find('a.tabs-inner'));
                                        }
                                        if ($.isArray(opts.tools)){
                                                p_tool.empty();
                                                for(var i=0; i<opts.tools.length; i++){
                                                        var t = $('<a href="javascript:void(0)"></a>').appendTo(p_tool);
                                                        t.addClass(opts.tools[i].iconCls);
                                                        if (opts.tools[i].handler){
                                                                t.bind('click', {handler:opts.tools[i].handler}, function(e){
                                                                        if ($(this).parents('li').hasClass('tabs-disabled')){return;}
                                                                        e.data.handler.call(this);
                                                                });
                                                        }
                                                }
                                        } else {
                                                $(opts.tools).children().appendTo(p_tool);
                                        }
                                        var pr = p_tool.children().length * 12;
                                        if (opts.closable) {
                                                pr += 8;
                                        } else {
                                                pr -= 3;
                                                p_tool.css('right','5px');
                                        }
                                        s_title.css('padding-right', pr+'px');
                                } else {
                                        tab.find('span.tabs-p-tool').remove();
                                        s_title.css('padding-right', '');
                                }
                        }
                        if (oldTitle != opts.title){
                                for(var i=0; i<selectHis.length; i++){
                                        if (selectHis[i] == oldTitle){
                                                selectHis[i] = opts.title;
                                        }
                                }
                        }
                }
                
                $(container).tabs('resize');
                
                $.data(container, 'tabs').options.onUpdate.call(container, opts.title, $(container).tabs('getTabIndex',pp));
        }

        $.extend($.fn.tabs.methods, {
                update: function(jq, param){
                        return jq.each(function(){
                                updateTab(this, param);
                        })
                }
        })
})(jQuery);

(function($){
	var render = $.fn.tree.defaults.view.render;
	$.fn.tree.defaults.view.render = function(target, ul, data){
		render(target, ul, data);
		var opts = $(target).tree('options');
		if (opts.dnd){
			$(target).find('.tree-node').draggable({
				delay:0
			});
		}
	}
})(jQuery);
