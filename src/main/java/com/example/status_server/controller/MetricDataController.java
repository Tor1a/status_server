package com.example.status_server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;


import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MetricDataController {


    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MetricDataController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/getMetric")
    public ResponseEntity<Object> getMetricData() {
        Region region = Region.AP_NORTHEAST_2;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        double mem_usage = 0;
        double disk_usage = 0;
        double cpu_usage = 0;
        String instanceId = null;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        try {
            Instant start = Instant.now().minus(Duration.ofMinutes(5));
            Instant endDate = Instant.now();
            List<String> instance_id = List.of("i-0d66b83b3c1617562");


            List<MetricDataQuery> dqList = new ArrayList<>();
            List<MetricDataQuery> dqList2 = new ArrayList<>();
            List<MetricDataQuery> dqList3 = new ArrayList<>();


            for (int i = 0; i < instance_id.size(); i++) {
                instanceId = instance_id.get(i);
                String memId = "mem" + i;
                String cpuId = "cpu" + i;
                String diskId = "disk" + i;

                Metric mem = Metric.builder()
                        .metricName("mem_used_percent")
                        .namespace("CWAgent")
                        .dimensions(Dimension.builder()
                                .name("InstanceId")
                                .value(instanceId)
                                .build())
                        .build();
                Metric disk = Metric.builder()
                        .metricName("disk_used_percent")
                        .namespace("CWAgent")
                        .dimensions(Dimension.builder()
                                .name("InstanceId")
                                .value(instanceId)
                                .build())
                        .build();

                Metric cpu = Metric.builder()
                        .metricName("cpu_usage_idle")
                        .namespace("CWAgent")
                        .dimensions(Dimension.builder()
                                .name("InstanceId")
                                .value(instanceId)
                                .build())
                        .build();


                MetricStat metMemory = MetricStat.builder()
                        .stat("Average")
                        .period(60)
                        .metric(mem)
                        .build();

                MetricStat metDisk = MetricStat.builder()
                        .stat("Average")
                        .period(60)
                        .metric(disk)
                        .build();

                MetricStat metCpu = MetricStat.builder()
                        .stat("Average")
                        .period(60)
                        .metric(cpu)
                        .build();

                MetricDataQuery memoryDataQUery = MetricDataQuery.builder()
                        .metricStat(metMemory)
                        .id(memId)
                        .returnData(true)
                        .build();

                MetricDataQuery diskDataQUery = MetricDataQuery.builder()
                        .metricStat(metDisk)
                        .id(diskId)
                        .returnData(true)
                        .build();

                MetricDataQuery cpuDataQUery = MetricDataQuery.builder()
                        .metricStat(metCpu)
                        .id(cpuId)
                        .returnData(true)
                        .build();


                dqList.add(memoryDataQUery);
                dqList2.add(diskDataQUery);
                dqList3.add(cpuDataQUery);


                GetMetricDataRequest getMetReq = GetMetricDataRequest.builder()
                        .maxDatapoints(100)
                        .startTime(start)
                        .endTime(endDate)
                        .metricDataQueries(dqList)
                        .build();

                GetMetricDataRequest getMetReq2 = GetMetricDataRequest.builder()
                        .maxDatapoints(100)
                        .startTime(start)
                        .endTime(endDate)
                        .metricDataQueries(dqList2)
                        .build();

                GetMetricDataRequest getMetReq3 = GetMetricDataRequest.builder()
                        .maxDatapoints(100)
                        .startTime(start)
                        .endTime(endDate)
                        .metricDataQueries(dqList3)
                        .build();

                GetMetricDataResponse response = cw.getMetricData(getMetReq);
                List<MetricDataResult> data = response.metricDataResults();

                GetMetricDataResponse response2 = cw.getMetricData(getMetReq2);
                List<MetricDataResult> data2 = response2.metricDataResults();

                GetMetricDataResponse response3 = cw.getMetricData(getMetReq3);
                List<MetricDataResult> data3 = response3.metricDataResults();


                for (MetricDataResult item : data) {
                    List<Double> values = item.values();
                    if (values.isEmpty()) {
                        continue;
                    }
                    List<Double> modifiedValues = new ArrayList<>();
                    for (Double value : values) {
                        modifiedValues.add(value);
                    }
                    mem_usage = Double.parseDouble(decimalFormat.format(modifiedValues.get(0)));
                }


                for (MetricDataResult item : data2) {
                    List<Double> values = item.values();
                    if (values.isEmpty()) {
                        continue;
                    }
                    List<Double> modifiedValues = new ArrayList<>();
                    for (Double value : values) {
                        modifiedValues.add(value);
                    }
                    disk_usage = Double.parseDouble(decimalFormat.format(modifiedValues.get(0)));
                }

                for (MetricDataResult item : data3) {
                    List<Double> values = item.values();
                    if (values.isEmpty()) {
                        continue;
                    }
                    List<Double> modifiedValues = new ArrayList<>();
                    for (Double value : values) {
                        modifiedValues.add(value);
                    }
                    cpu_usage = Double.parseDouble(decimalFormat.format(100 - modifiedValues.get(0)));
                }


            }

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        cw.close();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("instance_id", instanceId);
        responseData.put("mem_usage", mem_usage);
        responseData.put("disk_usage", disk_usage);
        responseData.put("cpu_usage", cpu_usage);
        System.out.println(responseData);
        messagingTemplate.convertAndSend("/topic/metric", responseData);
        return ResponseEntity.ok(responseData);
    }

}
