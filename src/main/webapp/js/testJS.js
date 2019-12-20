


var $all_product = $('.layui-tab-item');

layui.use('element', function() {
    var element = layui.element;


    var $ = jQuery = layui.jquery;

    console.log("dddd#d");
    var sort=null;
    element.on('tab()', function(){
        sort=this.getAttribute('lay-id');
        console.log(this.getAttribute('lay-id'));
        $.ajax({
            url: "/getItemOfthisKind.do",
            type: "POSt",
            dataType: "JSON",
            data: {sort:sort},
            success: function (data) {
                $all_product.html('');
                if (data.length === 0) {
                    $all_product.append("<div class='product_content_div'>" +
                        "<div class='detail_product'>" +
                        "<input type='hidden' value= ''/>" +
                        "<div class='product_img_div'><img src='' title='暂时没有该分类的商品' /></div>" +
                        "<span class='detail_product_name'></span><br/>" +
                        "<span class='detail_product_cost'></span><br/>" +
                        "<span class='detail_buy product_1'>加入购物车</span>" +
                        "</div>" +
                        "</div>");
                }
                for (var i = 0; i < data.length; i++) {
                    $all_product.append("<div class='product_content_div'>" +
                        "<div class='detail_product'>" +
                        "<input type='hidden' value=" + data[i].id + " '/>" +
                        "<div class='product_img_div'>" +
                        "<img class='show_img' src='" + data[i].image + "' title=" + data.name + "'/>" +
                        "</div>" +
                        "<p class='show_tip'>"+data[i].remark+"</p>"+
                        "<span class='detail_product_name' value='"+data[i].id+"'>" + data[i].name + "</span><br/>" +
                        "<span class='detail_product_cost'>￥" + data[i].price + "</span><br/>" +
                        "<span class='detail_buy product_1' value='"+data[i].id+"'>加入购物车</span>" +
                        "</div>" +
                        "</div>");
                }




                element.render();

            },
            error: function (XMLHttpRequest, textStatus, errorThrown,data) {

                console.log(errorThrown);
            }
        });

    });


});
layui.use('carousel', function() {
    var carousel = layui.carousel;
    //建造实例
    carousel.render({
        elem: '#test1',
        width: '100%' //设置容器宽度
        ,
        arrow: 'always' //始终显示箭头
        //,anim: 'updown' //切换动画方式
    });
});
