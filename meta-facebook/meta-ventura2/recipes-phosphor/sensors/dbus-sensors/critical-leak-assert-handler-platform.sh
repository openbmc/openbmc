#!/bin/bash

# Platform-specific functions that should be implemented if enabling rack power off handler
# Output: LeakConfig value (0-3)
_platform_get_leak_config()             {
    echo $(( ($(i2cget -f -y 10 0x64 0x00) >> 6) & 0x03));
}

# Output: RPU_READY value (0 or 1)
_platform_check_rpu_ready()             {
    # RPU_READY_PLD_R is managed by phosphor-multi-gpio-monitor.service
    # and not accessible from userspace. Read SGPIO register instead.
    local addr=0x1e780538
    local bit=4
    echo $(( ( $(devmem "$addr") >> bit ) & 1 ))
}

# Populate SHUTDOWN_DETECTORS associative array.
# Keys are detector names; value is always 1 (used as a set, not a map)
_platform_get_shutdown_detectors()      {
    # shellcheck disable=SC2034
    declare -gA SHUTDOWN_DETECTORS=(
        [RackDripTrayLeak1]=1
        [RackDripTrayLeak2]=1
        [RackLeakSpare1]=1
        [RackLeakSpare2]=1
        [RackLeakSpare3]=1
        [RackVerticalColdManifoldLeak]=1
        [RackVerticalHotManifoldLeak]=1
        [RackHorizontalColdManifoldLeak]=1
        [RackHorizontalHotManifoldLeak]=1
    )
}

# Populate VALVES array.
# Each element format: "cable_name|valve_name"
_platform_get_valves()                  {
    # shellcheck disable=SC2034
    declare -ga VALVES=(
        "ReturnValve_1_Port|ReturnValve_1"
        "ReturnValve_2_Port|ReturnValve_2"
        "ReturnValve_3_Port|ReturnValve_3"
    )
}
