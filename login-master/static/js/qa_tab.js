(function ($) {
    $.fn.tabs = function (document) {
        var wt = $(this).find(".wrap-top");
        var wtl = $(".wrap .wrap-top li");
        var wtla = $(this).find(".wrap-top li a");
        var contscrli = $(".cont-scroll li");
        var contscrli = $(".cont-scroll li");

        // 存储左右点击次数和左右滑动长短
        var clicks = 0, maLf = -200, marginwi = 0;

        // 修改内容------点击li的index
        var modfre;


        // 打开网页只运行一次
        wtl.eq(0).css({'background': '#fff'}).find('span').css({'color': 'rgba(66,133,244,0.95)'});


        // 删除选项卡的点击项和选项卡对应内容
        wtla.live('click', function (e) {
            // 获取点击a元素上一级的li元素的index
            index = $(this).parent("li").index();


            // 关闭选项卡标题和相应内容
            $(".wrap-content").eq(index).remove();
            $(".wrap .wrap-top li").eq(index).remove();

            // 关闭后切换同类最后一个元素并切换
            $(".wrap .wrap-top li").eq($(".wrap .wrap-top li").size() - 1).css({'background': '#fff'}).siblings().css({'background': 'rgba(66,133,244,0.95)'}).find('span').css({'color': '#fff'})
            $(".wrap .wrap-top li").eq($(".wrap .wrap-top li").size() - 1).find('span').css({'color': 'rgba(66,133,244,0.95)'}).siblings().find('span').css({'color': 'rgba(66,133,244,0.95)'})
            $(".wrap-content").eq($(".wrap-content").size() - 1).show().siblings().hide();
            wt.show();
            margile();
        })


        // 切换样式
        wtl.live('click', function (e) {
            // 切换样式和打开相应内容
            $(this).css({'background': '#fff'}).siblings().css({'background': 'rgba(66,133,244,0.95)'});

            // 切换样式并将上一级的同类标签切换样式
            $(this).find("span").css({'color': 'rgba(66,133,244,0.95)'}).end().siblings().find('span').css({'color': '#fff'});
            $(".wrap-content").eq($(this).index()).show().siblings().hide();
            wt.show();
            margile();
        })


        wtl.live('dblclick', function (e) {
            modfre = $(this).index();

            // 获取原参数数据
            modifyspan = $(this).find('span').text();
            modifytextarea = $(".wrap-content").eq(modfre).text();


            // 打开全屏全屏修改遮罩
            $(".full-modify").show()


            // 原数据写入
            Smadi = $(".full-content .full-modi").find('input').val(modifyspan);
            Tmadi = $(".full-content .full-modi").find('textarea').val(modifytextarea);
        })

        $(".full-modify .full-modi botton").click(function () {

            // 输入完后的参数数据保存
            persein = $(".full-content .full-modi").find('input').val()
            persearea = $(".full-content .full-modi").find('textarea').val();

            $(".cont-scroll li").eq(modfre).find('span').text(persein);
            $(".wrap-content").eq(modfre).text(persearea);
            $(".full-modify").hide();
            margile()
        })


        $(".full-top span").click(function () {
            $(".full,.full-modify").hide()
        })


        // 点击添加内容打开全屏遮罩
        $(this).find(".add-cont").click(function (document) {
            $(".full").show();
        });


        // 点击删除清空全部
        $(".del-cont").click(function () {
            $(".cont-scroll li").remove();
            $(".wrap-content").remove();
        })


        // 点击除输入框以外隐藏全屏遮罩
        $(".full,.full-modify").live('click', function () {
            $(this).not(".full-content").toggle();
        })


        $(".full-wrap").live('click', function (e) {
            e.stopPropagation();
        })


        //添加选项内容
        $(".full .full-inp botton").live('click', function () {
            Fin = $(".full-content input").val();
            Far = $(".full-content textarea").val();

            // 添加选项卡标题和内容
            $(".cont-scroll:last").append('<li><span>' + Fin + '</span><a href="javascript:void(0)">X</a></li>');
            $(".full").before('<div class="wrap-content">' + Far + '</div>');

            // 输入完后清空，否则下次添加内容还显示上次添加的内容（未在其他PC测试）
            $(".full-content input,.full-content textarea").val("");

            // 添加内容后切换至相应的选项卡和相应的内容
            $(".wrap-top li").eq($(".wrap-top li").size() - 1).css({'background': '#fff'}).siblings("li").css({'background': 'rgba(66,133,244,0.95)'});
            $(".wrap-top li").eq($(".wrap-top li").size() - 1).find('span').css({'color': 'rgba(66,133,244,0.95)'}).end().siblings().find('span').css({'color': '#fff'});
            $(".wrap-content").eq($(".wrap-content").size() - 1).show().siblings().hide();
            wt.show();
            $(".Add").show();
            $(".full").hide();


            margile()
        })


        $(".tab-right").click(function () {
            clicks++;
            // 判断右边
            if (parseInt($(".cont-scroll").css('marginLeft')) - $(".cont-tab").width() < parseInt(-$(".cont-scroll").width())) {
                clicks--;
                $(".cont-scroll").stop().animate({marginLeft: maLf * clicks});
                alert('已经很右了呢！')
            } else {
                $(".cont-scroll").stop().animate({marginLeft: maLf * clicks});
            }
        })


        $(".tab-left").click(function () {
            clicks--;
            // 判断左边
            if (parseInt($(".cont-scroll").css('marginLeft')) >= 0) {
                $(".cont-scroll").stop().animate({marginLeft: 0});

                clicks++;
                alert('不能在左了呀！')
            } else {
                $(".cont-scroll").stop().animate({marginLeft: maLf * clicks});
            }


        })


        // 初始化计算cont-scroll宽度
        margile = function () {

            if ($(".cont-scroll li").size() == 0) {
                marginwi = 1;
            } else {
                var Windex = 0;


                // 因为不是固定宽度,所以要循环计算li的outerWidth宽度
                $(".cont-scroll").children("li").each(function () {
                    Windex += $(this).outerWidth();
                })

                // marginwi=parseInt($(".cont-scroll li").size()*$(".cont-scroll li").outerWidth());
                // marginwi+=$(".cont-scroll li").eq(-1).outerWidth();
            }


            // marginwi=(parseInt($(".cont-scroll li").outerWidth())+parseInt($(".cont-scroll li").css('marginLeft')))*($(".cont-scroll li").size()+1);
            $(".cont-scroll").css({'width': Windex});
            $(".Add").show();
        };
        margile()
    }
})(jQuery);





















