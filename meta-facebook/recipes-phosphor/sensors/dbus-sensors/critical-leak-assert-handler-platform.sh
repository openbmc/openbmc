#!/bin/bash

# Platform-specific functions that should be implemented if enabling rack power off handler
# Output: LeakConfig value (0-3)
_platform_get_leak_config()             { :; }

# Output: RPU_READY value (0 or 1)
_platform_check_rpu_ready()             { :; }

# Populate SHUTDOWN_DETECTORS associative array.
# Keys are detector names; value is always 1 (used as a set, not a map)
_platform_get_shutdown_detectors()      {
    # shellcheck disable=SC2034
    declare -gA SHUTDOWN_DETECTORS=();
}

# Populate VALVES array.
# Each element format: "cable_name|valve_name"
_platform_get_valves()                  {
    # shellcheck disable=SC2034
    declare -ga VALVES=();
}

# Optional hooks for inserting platform-specific actions at key points in the leak handling flow.
hook_on_leak_detected()       { :; }   # $1: detector_name
