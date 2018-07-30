package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.FileSizeFormatter;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Slf4j
@Ignore("Download huge files")
public class NEPScpDownloadServiceImplTest {



    NEPDownloadService impl = new NEPScpDownloadServiceImpl(
        "sftp-itemizer.nepworldwide.nl",
        "npo",
        "***REMOVED***",
        "AAAAB3NzaC1yc2EAAAADAQABAAABAQCV4gmmgKyPVyOyZv1jdVpu/KzS9w2v4/vxDeKbuXvl0tldvDAmMi/QY1XvLueuZJy8PmilpGj6po1JuU0V2RGX/Js18b9lyCAQptdaeUk45lYvM8bpGfkzB509i3+CaM6U1onEIftFs4vzDLMwHrZQ6kdlRGGs6bLYy1vpqs7h6mO/XGDeLLVpjLPZbz/TrWt98kinn+Rg/TwYV0VNyqac5DkpWtFEUucIrq6zZs1q3Pw8YHMo02BWlWXFR/yi41ODb+RH1dTlZEs3vrMgwFvVD5c+4EKy1hZ65SJ6xVXwaMyN4w1LaHLwwe3K8rNDS+m5gyaswhdeZthqDiXysFwj",
        //"94:06:26:d5:e4:f5:18:b5:52:a9:19:b1:97:db:94:9e"
            Arrays.asList("/local/bin/scp", "/usr/bin/scp"),
        Arrays.asList("/usr/bin/sshpass", "/opt/local/bin/sshpass")


    );
    @Test
    public void test() throws IOException {

        Instant start = Instant.now();

        log.info("using {}", impl);
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        final long size[] = {-1L};
        impl.download(NEPSSHJDownloadServiceImplTest.fileName,
            () -> outputStream,
            Duration.ofSeconds(10), (fd) -> {
            log.info("{}", fd);
            size[0] = fd.getSize();
            return true;}
            );

        log.info("Duration {} ({})", Duration.between(start, Instant.now()), FileSizeFormatter.DEFAULT.formatSpeed(size[0], start));


    }

    @Test
    public void testTimeout() throws IOException {

        Instant start = Instant.now();

        log.info("using {}", impl);
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        final long size[] = {-1L};
        impl.download("bestaathelemaalniet.mp4",
            () -> outputStream,
            Duration.ofSeconds(1), (fd) -> {
            log.info("{}", fd);
            size[0] = fd.getSize();
            return true;}
            );

        log.info("Duration {} ({})", Duration.between(start, Instant.now()), FileSizeFormatter.DEFAULT.formatSpeed(size[0], start));


    }
}
