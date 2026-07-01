#!/usr/bin/env bash
set -euo pipefail

SVC="au.com.codeconstruct.MCTP1"
ROOT="/au/com/codeconstruct/mctp1"

IFACE="mctpi3c3"
LOCAL_EID="8"
HOST_EID="9"

DEVICE="1e7a5000.i3c"
DRIVER_PATH="/sys/bus/platform/drivers/ast2600-i3c-master"

is_eid_assigned() {
    busctl tree "$SVC" 2>/dev/null | grep -qE "/endpoints/${HOST_EID}$"
}

get_first_pid() {
    for pid_file in /sys/bus/i3c/devices/3-*/pid; do
        [ -f "$pid_file" ] && cat "$pid_file" && return 0
    done

    return 1
}

bind_i3c3_controller() {
    if [ -e "${DRIVER_PATH}/${DEVICE}" ]; then
        echo "I3C controller ${DEVICE} already bound"
        return 0
    fi

    if [ ! -w "${DRIVER_PATH}/bind" ]; then
        echo "I3C driver bind node is not available: ${DRIVER_PATH}/bind"
        return 1
    fi

    echo "Binding ${DEVICE}"
    echo "${DEVICE}" > "${DRIVER_PATH}/bind"
}

unbind_i3c3_controller() {
    if [ -w "${DRIVER_PATH}/unbind" ]; then
        echo "Unbinding ${DEVICE}"
        echo "${DEVICE}" > "${DRIVER_PATH}/unbind" 2>/dev/null || true
    fi
}

enable_mctp_link() {
    local pid lladdr bus_bytes obj

    echo "Bringing ${IFACE} up"
    mctp link set "$IFACE" up || true

    echo "Adding local EID ${LOCAL_EID} to ${IFACE}"
    mctp addr add "$LOCAL_EID" dev "$IFACE" 2>/dev/null || true

    if is_eid_assigned; then
        echo "Host EID ${HOST_EID} is already assigned, skipping"
        return 0
    fi

    pid="$(get_first_pid)" || {
        echo "No I3C PID device found, skipping"
        return 0
    }

    obj="${ROOT}/interfaces/${IFACE}"
    lladdr="$(echo "$pid" | sed 's/../&:/g; s/:$//')"
    bus_bytes="$(echo "$lladdr" | awk -F: '{for(i=1;i<=NF;i++) printf " 0x%s",$i}')"

    echo "Assigning MCTP I3C endpoint EID ${HOST_EID} with PID ${pid}"

    for _ in $(seq 1 5); do
	# shellcheck disable=SC2086
        if busctl call \
            "$SVC" \
            "$obj" \
            au.com.codeconstruct.MCTP.BusOwner1 \
            AssignEndpointStatic \
            ayy 6 ${bus_bytes} "$HOST_EID"; then
            echo "AssignEndpointStatic succeeded"
            return 0
        fi

        echo "Retrying endpoint assignment"
        sleep 1
    done

    echo "AssignEndpointStatic failed"
    return 1
}

disable_mctp_link() {
    echo "Bringing ${IFACE} down"
    mctp link set "$IFACE" down 2>/dev/null || true
}

start_service() {
    bind_i3c3_controller || return 0
    enable_mctp_link
}

stop_service() {
    disable_mctp_link
    unbind_i3c3_controller
}

case "${1:-}" in
    start) start_service ;;
    stop) stop_service ;;
    *) exit 1 ;;
esac
