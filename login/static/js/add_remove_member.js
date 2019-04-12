var memberIndex = 2;



function setMember() {
    return '<div id="member">' +
        '<div class="col-md-6" id="mem">' +
        '    <div class="form-group">' +
        '        <label for="id_m1" id="memb">成员' + memberIndex + ':</label>' +
        '        <input type="text" name="m" class="form-control" maxlength="128" required id="id_m" />' +
        '    </div>' +
        '    <div class="form-group">' +
        '    </div>' +
        '    <div class="form-group">' +
        '    </div>' +
        '</div>' +
        '<div class="col-md-6 col-md-offset-6 " style="margin-top: -70px;">' +
        '    <div class="col-md-8">' +
        '        <img class="a" src="../static/css/add.png/" width="35px;"' +
        '             height="35px;"' +
        '             onclick="addMember()"/>' +
        '        <span width="40px;" height="40px;"' +
        '              style="font-size: 18px;">增加辅助审核人员</span>' +
        '    </div>' +
        '    <div class="col-md-8">' +
        '        <img class="a" src="../static/css/shanchu.png/" width="35px;"' +
        '             height="35px;"' +
        '             onclick="removeMember(this)"/>' +
        '        <span width="40px;" height="40px;"' +
        '              style="font-size: 18px;">删除辅助审核人员</span>' +
        '    </div>' +
        '    <div class="col-md-8">' +
        '    </div>' +
        '    <div class="col-md-8">' +
        '    </div>' +
        '</div>' +
        '</div>';
}

function addMember() {
    $("#form-div_1").prepend(setMember());
    $("#member").attr('id', 'member' + memberIndex);
    $("#mem").attr('id', 'mem' + memberIndex);
    $("#memb").attr('for', 'id_m' + memberIndex);
    $("#memb").attr('id', 'memb' + memberIndex);
    $("#id_m").attr('name', 'm' + memberIndex);
    $("#id_m").attr('id', 'id_m' + memberIndex);
    $("#id_a1_m").attr('name', 'a1_m' + memberIndex);
    $("#id_a1_m").attr('id', 'id_a1_m' + memberIndex);
    $("#id_a2_m").attr('name', 'a2_m' + memberIndex);
    $("#id_a2_m").attr('id', 'id_a2_m' + memberIndex);
    memberIndex++;
}

function removeMember(t) {
    var temp = $(t).parent().parent().parent().attr('id');
    var index = temp.substr(temp.length - 1);
    $("#member" + index).remove();
    for (index++; index < memberIndex; index++) {
        $("#member"+index).attr('id', 'member' + (index-1));
        $("#mem"+index).attr('id', 'mem' + (index-1));
        $("#memb"+index).html("成员"+(index-1));
        $("#memb"+index).attr('for', 'id_m' + (index-1));
        $("#memb"+index).attr('id', 'memb' + (index-1));
        $("#id_m"+index).attr('name', 'm' + (index-1));
        $("#id_m"+index).attr('id', 'id_m' + (index-1));
        
       
    }
    memberIndex--;
}
