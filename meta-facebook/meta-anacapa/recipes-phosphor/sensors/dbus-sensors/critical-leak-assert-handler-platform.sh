#!/bin/bash

# Anacapa has no rack power-off role: leave the shutdown detector and valve
# lists empty so the common handler only runs hook_on_leak_detected.

_platform_get_leak_config()             { :; }
_platform_check_rpu_ready()             { :; }

_platform_get_shutdown_detectors()      {
    # shellcheck disable=SC2034
    declare -gA SHUTDOWN_DETECTORS=();
}

_platform_get_valves()                  {
    # shellcheck disable=SC2034
    declare -ga VALVES=();
}

# One stamp per critical detector that took a running host down.  The
# critical deassert handler powers the host back on once they are all clear.
# shellcheck disable=SC2034
readonly LEAK_STAMP_DIR="/run/openbmc/leak-power-loss"

readonly HOST_SERVICE="xyz.openbmc_project.State.Host0"
readonly HOST_PATH="/xyz/openbmc_project/state/host0"
readonly HOST_IFACE="xyz.openbmc_project.State.Host"

get_host_property() {
    busctl get-property "${HOST_SERVICE}" "${HOST_PATH}" "${HOST_IFACE}" "$1" |
        awk '{print $2}' | tr -d '"'
}

set_host_transition() {
    busctl set-property "${HOST_SERVICE}" "${HOST_PATH}" "${HOST_IFACE}" \
        RequestedHostTransition s \
        "xyz.openbmc_project.State.Host.Transition.${1}"
}

# The tray CPLD drops host power on a critical leak before leakdetector
# reports it, and assert-power-good-drop then rewrites RequestedHostTransition
# to Off.  It leaves a marker when it does so, and only for an unexpected
# power loss, so "the host was up" means the requested state is still On or
# that marker is present.
host_was_running() {
    [ "$(get_host_property RequestedHostTransition)" == \
      "xyz.openbmc_project.State.Host.Transition.On" ] ||
        [ -e /run/openbmc/host-abnormal-power-loss ]
}

hook_on_leak_detected() {
    local detector="$1"

    if host_was_running; then
        mkdir -p "${LEAK_STAMP_DIR}"
        touch "${LEAK_STAMP_DIR}/${detector}"
        echo "Host taken down by leak '${detector}', will power on once clear"
    else
        echo "Host was already off at leak '${detector}', leaving it off"
    fi
}
