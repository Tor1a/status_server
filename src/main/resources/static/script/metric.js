// $(document).ready(function() {
//     $.ajax({
//         type: "GET",
//         url: "/getMetric",
//         success: function(data) {
//             alert(data);
//             $("#instanceId").text(data.instance_id);
//             $("#memUsage").text(data.mem_usage);
//             $("#diskUsage").text(data.disk_usage);
//             $("#cpuUsage").text(data.cpu_usage);
//         },
//         error: function() {
//             alert("데이터를 가져오는 데 실패했습니다.");
//         }
//     });
// });


// WebSocket 연결을 설정합니다.
const socket = new SockJS('/websocket-endpoint'); // WebSocket 엔드포인트 경로
const stompClient = Stomp.over(socket);

// WebSocket 연결을 시작합니다.
stompClient.connect({}, function (frame) {
    console.log('Connected to WebSocket');

    // WebSocket을 통해 메시지를 구독합니다.
    stompClient.subscribe('/topic/metric', function (response) {
        const metricData = JSON.parse(response.body);
        // 메트릭 데이터를 표시하거나 처리합니다.
        displayMetricData(metricData);
    });
});

// 메트릭 데이터를 표시하는 함수 예제
function displayMetricData(metricData) {
    const metricDiv = document.getElementById('metric-data');
    metricDiv.innerHTML = `
        <p>Instance ID: ${metricData.instance_id}</p>
        <p>Memory Usage: ${metricData.mem_usage}</p>
        <p>Disk Usage: ${metricData.disk_usage}</p>
        <p>CPU Usage: ${metricData.cpu_usage}</p>
    `;
}

