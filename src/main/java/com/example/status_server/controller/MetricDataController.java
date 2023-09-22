package com.example.status_server.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MetricDataController {
    @GetMapping("/getMetric")
    public String getMetricData(){
        Region region = Region.AP_NORTHEAST_2;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try {
            Instant start = Instant.now().minus(Duration.ofMinutes(5));
            Instant endDate = Instant.now();
            List<String> instanceIds = List.of("i-0d66b83b3c1617562");


            List<MetricDataQuery> dqList = new ArrayList<>();
            List<MetricDataQuery> dqList2 = new ArrayList<>();
            List<MetricDataQuery> dqList3 = new ArrayList<>();


            for (int i = 0; i < instanceIds.size(); i++) {
                String instanceId = instanceIds.get(i);
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

                System.out.println(data.get(0));
                System.out.println(data2);
                System.out.println(data3);

            }
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        cw.close();

        return "status.html";
    }


}
