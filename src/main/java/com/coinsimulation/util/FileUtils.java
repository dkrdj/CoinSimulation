package com.coinsimulation.util;

import com.coinsimulation.exception.UploadException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import software.amazon.awssdk.core.SdkResponse;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.List;


@UtilityClass
@Slf4j
public class FileUtils {

    public ByteBuffer dataBufferToByteBuffer(List<DataBuffer> buffers) {
        log.info("Creating ByteBuffer from {} chunks", buffers.size());

        int partSize = 0;
        for (DataBuffer b : buffers) {
            partSize += b.readableByteCount();
        }

        ByteBuffer partData = ByteBuffer.allocate(partSize);
        buffers.forEach(buffer -> partData.put(buffer.toByteBuffer()));

        // Reset read pointer to first byte
        partData.rewind();

        log.info("PartData: capacity={}", partData.capacity());
        return partData;
    }

    public void checkSdkResponse(SdkResponse sdkResponse) {
        if (AwsSdkUtil.isErrorSdkHttpResponse(sdkResponse)) {
            throw new UploadException(MessageFormat.format("{0} - {1}", sdkResponse.sdkHttpResponse().statusCode(), sdkResponse.sdkHttpResponse().statusText()));
        }
    }

}