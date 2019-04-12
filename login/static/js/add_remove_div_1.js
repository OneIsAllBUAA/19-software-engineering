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
        '    </div>' +
        '    <div class="form-group">' +
        '    </div>' +
        '</div>' +
        '<div class="col-md-6 col-md-offset-6 " style="margin-top: -70px;">' +
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
        '    </div>' +
        '    <div class="col-md-8">' +
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
    $("#id_a1_q").attr('name', 'a1_q' + questionIndex);
    $("#id_a1_q").attr('id', 'id_a1_q' + questionIndex);
    $("#id_a2_q").attr('name', 'a2_q' + questionIndex);
    $("#id_a2_q").attr('id', 'id_a2_q' + questionIndex);
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
        
       
    }
    questionIndex--;
}
