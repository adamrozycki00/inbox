package com.tenetmind.inbox.infra.adapter.restapi;

import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.OK;


@RestController
@Slf4j
public class HeapDumpController {

  @PostMapping("api:makeHeapDump")
  @ResponseStatus(OK)
  public void makeHeapDump(@RequestBody MakeHeapDumpRequest request) throws Exception {
    File dir = new File(request.dir());

    if (!dir.exists()) {
      log.error("Directory does not exists: {}", dir.getAbsolutePath());
    }

    if (!dir.canWrite()) {
      log.error("Directory is not writable: {}", dir.getAbsolutePath());
    }

    String filePath = dir + "/heapdump_" + LocalDateTime.now() + ".hprof";
    log.info("Heap dump file path is: {}", filePath);

    HeapDumpUtil.generateHeapDump(filePath, request.live());
  }

  private static final class HeapDumpUtil {
    static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

    static void generateHeapDump(String filePath, boolean live) throws Exception {
      HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
          ManagementFactory.getPlatformMBeanServer(),
          HOTSPOT_BEAN_NAME,
          HotSpotDiagnosticMXBean.class
      );
      mxBean.dumpHeap(filePath, live); // `live` ensures only reachable objects are dumped
    }
  }

  public record MakeHeapDumpRequest(String dir, boolean live) {
  }
}
