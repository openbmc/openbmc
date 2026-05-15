#!/bin/bash

# Platform-specific functions that should be implemented if enabling rack power off handler
# Output: LeakConfig value (0-3)
_platform_get_leak_config()             {
    echo $(( ($(i2cget -f -y 10 0x1f 0x02) >> 6) & 0x03));
}

# Output: RPU_READY value (0 or 1)
_platform_check_rpu_ready()             {
    local addr=0x1e780500
    local bit=15
    echo $(( ( $(devmem "$addr") >> bit ) & 1 ))
}

# Populate SHUTDOWN_DETECTORS associative array.
# Keys are detector names; value is always 1 (used as a set, not a map)
_platform_get_shutdown_detectors()      {
    # shellcheck disable=SC2034
    declare -gA SHUTDOWN_DETECTORS=(
        [RackDripPan]=1
        [RackRightManifold]=1
        [RackLeftManifold]=1
    )
}

# Populate VALVES array.
# Each element format: "cable_name|valve_name"
_platform_get_valves()                  {
    # shellcheck disable=SC2034
    declare -ga VALVES=(
        "ReturnValve_1_Port|ReturnValve_1"
        "SupplyValve_1_Port|SupplyValve_1"
    )
}

# Optional hooks for inserting platform-specific actions at key points in the leak handling flow.
hook_on_leak_detected() {
    local detector_name="$1"

    local leak_type temp portnum
    leak_type=$(echo "$detector_name" | cut -d'_' -f2)
    temp=${detector_name#*Port}
    portnum=${temp%%_*}

    if [ "$leak_type" == "Big" ]; then
        busctl set-property "xyz.openbmc_project.LED.GroupManager" \
            "/xyz/openbmc_project/led/groups/leakport${portnum}_amber" \
            "xyz.openbmc_project.Led.Group" "Asserted" "b" "true"

        busctl set-property "xyz.openbmc_project.LED.GroupManager" \
            "/xyz/openbmc_project/led/groups/leakport${portnum}_blue" \
            "xyz.openbmc_project.Led.Group" "Asserted" "b" "false"
    fi
}