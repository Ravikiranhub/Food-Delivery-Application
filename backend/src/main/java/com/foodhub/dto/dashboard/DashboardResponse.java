package com.foodhub.dto.dashboard;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardResponse(
        Map<String, Long> counts,
        Map<String, BigDecimal> money,
        Map<String, Object> highlights
) {
}

