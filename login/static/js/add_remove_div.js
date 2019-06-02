var questionIndex = 2;
var choiceIndex = new Array();

choiceIndex[0] = 3;


function setQuestion() {
    return '<div id="question">' +
        '<div class="col-md-6" id="qu">' +
        '    <div class="form-group">' +
        '        <label for="id_q1" id="ques">问题' + questionIndex + ':</label>' +
        '        <input type="text" name="q" class="form-control" maxlength="128" required id="id_q" />' +
        '    </div>' +
        '    <div class="form-group">' +
        '        <label for="id_a1_q" id="choi1_">选项1:</label>' +
        '        <input type="text" name="a1_q" class="form-control" maxlength="128" required id="id_a1_q" />' +
        '    </div>' +
        '    <div class="form-group">' +
        '        <label for="id_a2_q" id="choi2_">选项2:</label>' +
        '        <input type="text" name="a2_q" class="form-control" maxlength="128" required id="id_a2_q" />' +
        '    </div>' +
        '</div>' +
        '<div class="col-md-6 col-md-offset-6 " style="margin-top: -200px;">' +
        '    <div class="col-md-8">' +
        '        <img class="a" src="../static/css/add.png/" width="35px;"' +
        '             height="35px;"' +
        '             onclick="addQuestion()"/>' +
        '        <span width="40px;" height="40px;"' +
        '              style="font-size: 18px;">增加问题</span>' +
        '    </div>' +
        '    <div class="col-md-8">' +
        '        <img class="a" src="../static/css/shanchu.png/" width="35px;"' +
        '             height="35px;"' +
        '             onclick="removeQuestion(this)"/>' +
        '        <span width="40px;" height="40px;"' +
        '              style="font-size: 18px;">删除问题</span>' +
        '    </div>' +
        '    <div class="col-md-8">' +
        '        <img class="a" src="../static/css/queding.png/" width="35px;"' +
        '             height="35px;" onclick="addChoice(this)" id="add1"/>' +
        '        <span width="40px;" height="40px;"' +
        '              style="font-size: 18px;">增加选项</span>' +
        '    </div>' +
        '    <div class="col-md-8">' +
        '        <img class="a" src="../static/css/shanchuxuanxiang.png/"' +
        '             width="35px;" height="35px;" onclick="removeChoice(this)"' +
        '             id="del1"/>' +
        '        <span width="40px;" height="40px;"' +
        '              style="font-size: 18px;">删除选项</span>' +
        '    </div>' +
        '</div>' +
        '</div>';
}

function addQuestion() {
    $("#form-div").prepend(setQuestion());
    $("#question").attr('id', 'question' + questionIndex);
    $("#qu").attr('id', 'qu' + questionIndex);
    $("#ques").attr('for', 'id_q' + questionIndex);
    $("#ques").attr('id', 'ques' + questionIndex);
    $("#id_q").attr('name', 'q' + questionIndex);
    $("#id_q").attr('id', 'id_q' + questionIndex);
    $("#choi1_").attr('for', 'id_a1_q' + questionIndex);
    $("#choi1_").attr('id', 'choi1_' + questionIndex);
    $("#choi2_").attr('for', 'id_a2_q' + questionIndex);
    $("#choi2_").attr('id', 'choi2_' + questionIndex);
    $("#id_a1_q").attr('name', 'a1_q' + questionIndex);
    $("#id_a1_q").attr('id', 'id_a1_q' + questionIndex);
    $("#id_a2_q").attr('name', 'a2_q' + questionIndex);
    $("#id_a2_q").attr('id', 'id_a2_q' + questionIndex);
    choiceIndex[questionIndex - 1] = 3;
    questionIndex++;
}

function removeQuestion(t) {
    var temp = $(t).parent().parent().parent().attr('id');
    var index = temp.substr(temp.length - 1);
    $("#question" + index).remove();
    for (index++; index < questionIndex; index++) {
        $("#question"+index).attr('id', 'question' + (index-1));
        $("#qu"+index).attr('id', 'qu' + (index-1));
        $("#ques"+index).html("问题"+(index-1));
        $("#ques"+index).attr('for', 'id_q' + (index-1));
        $("#ques"+index).attr('id', 'ques' + (index-1));
        $("#id_q"+index).attr('name', 'q' + (index-1));
        $("#id_q"+index).attr('id', 'id_q' + (index-1));
        for(var i=1;i<choiceIndex[index-1];i++){
            $("#choi"+i+"_"+index).attr('for', 'id_a'+i+'_q' + (index-1));
            $("#choi"+i+"_"+index).attr('id', 'choi'+i+'_' + (index-1));
            $("#id_a"+i+"_q"+index).attr('name', 'a'+i+'_q' + (index-1));
            $("#id_a"+i+"_q"+index).attr('id', 'id_a'+i+'_q' + (index-1));
        }
        choiceIndex[index-2] = choiceIndex[index-1];
    }
    questionIndex--;
}

function setChoice() {
    return ''+
        '<div class="form-group">' +
        '    <label for="id_a_q" id="choi_"></label>' +
        '    <input type="text" name="a_q" class="form-control" maxlength="128" required id="id_a_q" />' +
        '</div>'
}

function addChoice(t) {
    var temp = $(t).parent().parent().parent().attr('id');
    var index = temp.substr(temp.length - 1);
    $(t).parent().parent().parent().children('#qu'+index).append(setChoice());
    $("#choi_").html("选项"+choiceIndex[index - 1]+":");
    $("#choi_").attr('for', 'id_a' + choiceIndex[index - 1] + "_q" + index);
    $("#choi_").attr('id', 'choi' + choiceIndex[index - 1]+'_'+index);
    $("#id_a_q").attr('name', 'a' + choiceIndex[index - 1] + "_q" + index);
    $("#id_a_q").attr('id', 'id_a' + choiceIndex[index - 1] + "_q" + index);
    choiceIndex[index - 1]++;
}

function removeChoice(t) {
    var temp = $(t).parent().parent().parent().attr('id');
    var index = temp.substr(temp.length - 1);
    if(choiceIndex[index - 1]>3){
        choiceIndex[index - 1]--;
        $(t).parent().parent().parent().children('#qu'+index).find('#choi' + choiceIndex[index - 1]+'_'+index).parent().remove();
    }
}