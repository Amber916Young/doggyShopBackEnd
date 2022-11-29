var MANETsList = new Array();

console.info(MANETsList)
setTimeout(() => {
    sendMsgToCloud()
}, 3000);
function sendMsgToCloud() {
    for (var i = 0; i < MANETsList.length; i++) {
        var ids=new Array(),ids2=new Array(), jsonData={};
        for (var j = 0; j < MANETsList[i].nodeInfos.length; j++) {
            var temp = MANETsList[i].nodeInfos[j];
            var number = Math.floor(Math.random() * 11);
            ids2.push(temp.nodeID);
            if(number%4==0) continue;
            ids.push(temp.nodeID);
        }
        jsonData["nodeIDs"] = ids;
        jsonData["allNodes"] = ids2;
        jsonData["MANET_SSID"] = MANETsList[i].manet_SSID;
        var params = JSON.stringify(jsonData);
        var url = "/node/broadcast"
        ajaxPost(url, params, function (data) {
            if (data.code == 0) {
                console.info(data.msg);
                layui.table.reload('Broadcast_table');
                setTimeout(() => {
                    // sendMsgToCloud()
                }, 5000);
            }else {
                console.info(data.msg);
            }
        });
    }
}