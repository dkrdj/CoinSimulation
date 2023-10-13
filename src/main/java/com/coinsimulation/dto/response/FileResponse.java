package com.coinsimulation.dto.response;

public record FileResponse(String name, String uploadId, String path, String type, String eTag) {
}
