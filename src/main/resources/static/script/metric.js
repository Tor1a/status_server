$(document).ready(function() {
    $.ajax({
        type: "GET",
        url: "/getMetric",
        success: function(data) {
            alert(data);
            $("#instanceId").text(data.instance_id);
            $("#memUsage").text(data.mem_usage);
            $("#diskUsage").text(data.disk_usage);
            $("#cpuUsage").text(data.cpu_usage);
        },
        error: function() {
            alert("데이터를 가져오는 데 실패했습니다.");
        }
    });
});