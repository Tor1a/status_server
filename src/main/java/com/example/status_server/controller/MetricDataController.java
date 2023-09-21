package com.example.status_server.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MetricDataController {

    @GetMapping("/")
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
                String cpuId = "mem" + i;
                String diskId = "mem" + i;

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

                String mem_status = null;
                double mem_usage = 0.0;
                for (MetricDataResult item : data) {
                    List<Double> values = item.values();
                    if (values.isEmpty()) {
                        continue;
                    }
                    List<Double> modifiedValues = new ArrayList<>();
                    for (Double value : values) {
                        modifiedValues.add(value);
                    }


                    mem_usage = modifiedValues.get(0);
                    if (mem_usage <= 50.0) {
                        mem_status = "OK";
                    } else if (mem_usage > 70.0) {
                        mem_status = "CRITICAL";
                    }else {
                        mem_status = "WARNING";
                    }
                    System.out.println("mem_status: "  + mem_status);

                }


                String disk_status = null;
                double disk_usage = 0.0;
                for (MetricDataResult item : data2) {
                    List<Double> values = item.values();

                    if (values.isEmpty()) {
                        continue;
                    }
                    List<Double> modifiedValues = new ArrayList<>();
                    for (Double value : values) {
                        modifiedValues.add(value);
                    }


                    disk_usage = modifiedValues.get(0);
                    if (disk_usage <= 70.0) {
                        disk_status = "OK";
                    } else if (disk_usage > 85.0) {
                        disk_status = "CRITICAL";
                    }else {
                        disk_status = "WARNING";
                    }
                    System.out.println("disk_status: "  + disk_status);

                }


                String cpu_status = null;
                double cpu_usage = 0.0;
                for (MetricDataResult item : data3) {
                    List<Double> values = item.values();

                    if (values.isEmpty()) {
                        continue;
                    }
                    List<Double> modifiedValues = new ArrayList<>();
                    for (Double value : values) {
                        modifiedValues.add(100 - value);
                    }
                    cpu_usage = modifiedValues.get(0);
                    if (cpu_usage < 50.0) {
                        cpu_status = "OK";
                    } else if (cpu_usage >= 80.0) {
                        cpu_status = "CRITICAL";
                    }else {
                        cpu_status = "WARNING";
                    }
                    System.out.println("cpu_status: "  + cpu_status);

                }


            }
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        cw.close();

        return "status.html";
    }

}
