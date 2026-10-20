#!/usr/bin/env bash
set -euo pipefail

SVC="au.com.codeconstruct.MCTP1"
ROOT="/au/com/codeconstruct/mctp1"

IFACE="mctpi3c3"
LOCAL_EID="8"
HOST_EID="9"

I3C_CONTROLLER_BASE="14c27000"
I3C_DRIVER_PATH="/sys/bus/platform/drivers/mipi-i3c-hci"

MCTP_I3C_LINK_READY_FILE="/run/rainier/mctp_i3c_link_ready"
MCTP_I3C_LINK_READY_WAIT_RETRIES="${MCTP_I3C_LINK_READY_WAIT_RETRIES:-40}"
MCTP_I3C_LINK_READY_WAIT_DELAY_SEC="${MCTP_I3C_LINK_READY_WAIT_DELAY_SEC:-1}"
I3C_DISCOVERY_ATTEMPTS=5
I3C_DISCOVERY_DELAY_SEC=1

mctp_i3c_link_ready() {
    local state

    [ -r "${MCTP_I3C_LINK_READY_FILE}" ] || return 1
    read -r state < "${MCTP_I3C_LINK_READY_FILE}" || return 1
    [ "${state}" = "true" ]
}

wait_for_mctp_i3c_link_ready() {
    local attempt

    echo "Waiting for MCTP I3C link ready..."
    for attempt in $(seq 1 "${MCTP_I3C_LINK_READY_WAIT_RETRIES}"); do
        if mctp_i3c_link_ready; then
            echo "MCTP I3C link ready detected"
            return 0
        fi

        sleep "${MCTP_I3C_LINK_READY_WAIT_DELAY_SEC}"
    done

    echo "Timed out waiting for MCTP I3C link ready"
    return 1
}

find_i3c_controller() {
    local path
    local controller
    local found=""

    for path in "/sys/bus/platform/devices/${I3C_CONTROLLER_BASE}.i3c"*; do
        [ -e "$path" ] || continue

        controller="${path##*/}"

        if [ -n "$found" ]; then
            echo "Multiple I3C controllers found for" \
                "${I3C_CONTROLLER_BASE}: ${found}, ${controller}" >&2
            return 1
        fi

        found="$controller"
    done

    if [ -z "$found" ]; then
        echo "I3C controller ${I3C_CONTROLLER_BASE} not found" >&2
        return 1
    fi

    printf '%s\n' "$found"
    return 0
}

get_first_pid() {
    local pid_file pid

    for pid_file in /sys/bus/i3c/devices/3-*/pid; do
        [ -r "$pid_file" ] || continue
        pid="$(cat "$pid_file")" || continue

        if [[ "$pid" =~ ^[[:xdigit:]]{1,12}$ ]]; then
            printf '%012x\n' "$((16#$pid))"
            return 0
        fi
    done

    return 1
}

controller_is_bound() {
    [ -e "${I3C_DRIVER_PATH}/${DEVICE}" ]
}

get_network_id() {
    mctp link show "$IFACE" 2>/dev/null | awk \
        -v dev="$IFACE" \
        '$1 == "dev" && $2 == dev {
            for (i = 3; i < NF; i++) {
                if ($i == "net") {
                    print $(i + 1)
                    found = 1
                }
            }
        }
        END { exit !found }'
}

get_endpoint_path() {
    local network path
    network="$(get_network_id)" || return 1
    path="${ROOT}/networks/${network}/endpoints/${HOST_EID}"
    busctl tree --list "$SVC" 2>/dev/null | grep -Fx "$path" >/dev/null || return 1
    printf '%s\n' "$path"
}

remove_mctp_endpoint() {
    local path
    path="$(get_endpoint_path || true)"
    [ -n "$path" ] || return 0
    echo "Removing MCTP endpoint ${path}"
    busctl call "$SVC" "$path" au.com.codeconstruct.MCTP.Endpoint1 Remove
}

bind_i3c3_controller() {
    DEVICE="$(find_i3c_controller)" || return 1

    if controller_is_bound; then
        echo "I3C controller ${DEVICE} is already bound; refreshing"
        remove_mctp_endpoint || return 1
        disable_mctp_link || true
        unbind_i3c3_controller || return 1
    fi

    echo "Binding ${DEVICE}..."
    printf "%s\n" "${DEVICE}" > "${I3C_DRIVER_PATH}/bind"
}

unbind_i3c3_controller() {
    DEVICE="$(find_i3c_controller)" || return 1
    controller_is_bound || return 0

    echo "Unbinding ${DEVICE}..."
    printf "%s\n" "${DEVICE}" > "${I3C_DRIVER_PATH}/unbind"
}

wait_for_i3c_interface() {
    local attempt

    for ((attempt = 1; attempt <= I3C_DISCOVERY_ATTEMPTS; attempt++)); do
        [ -e "/sys/class/net/${IFACE}" ] && return 0
        [ "$attempt" -lt "$I3C_DISCOVERY_ATTEMPTS" ] && sleep "$I3C_DISCOVERY_DELAY_SEC"
    done

    return 1
}

wait_for_pid() {
    local attempt pid

    for ((attempt = 1; attempt <= I3C_DISCOVERY_ATTEMPTS; attempt++)); do
        if pid="$(get_first_pid)"; then
            printf '%s\n' "$pid"
            return 0
        fi
        [ "$attempt" -lt "$I3C_DISCOVERY_ATTEMPTS" ] && sleep "$I3C_DISCOVERY_DELAY_SEC"
    done

    return 1
}

enable_mctp_link() {
    local pid="$1"
    local obj address_error offset
    local -a bus_bytes=()

    if ! wait_for_i3c_interface; then
        echo "MCTP I3C interface ${IFACE} is not available"
        return 1
    fi

    echo "Bringing ${IFACE} up..."
    if ! mctp link set "${IFACE}" up; then
        echo "Failed to bring ${IFACE} up"
        return 1
    fi

    echo "Adding local EID ${LOCAL_EID}..."
    if ! address_error="$(mctp address add "${LOCAL_EID}" dev "${IFACE}" 2>&1)"; then
        case "$address_error" in
            *"File exists"*)
                echo "Local EID ${LOCAL_EID} already configured"
                ;;
            *)
                echo "Failed to add local EID ${LOCAL_EID}: $address_error"
                return 1
                ;;
        esac
    fi

    obj="${ROOT}/interfaces/${IFACE}"
    for ((offset = 0; offset < ${#pid}; offset += 2)); do
        bus_bytes+=("0x${pid:offset:2}")
    done

    echo "Assigning MCTP endpoint Static ${HOST_EID} with PID ${pid}"

    if ! busctl call \
        "$SVC" \
        "$obj" \
        au.com.codeconstruct.MCTP.BusOwner1 \
        AssignEndpointStatic \
        ayy "${#bus_bytes[@]}" "${bus_bytes[@]}" "${HOST_EID}"; then
        echo "AssignEndpointStatic failed"
        return 1
    fi

    echo "AssignEndpointStatic succeeded"
}

disable_mctp_link() {
    if [ ! -e "/sys/class/net/${IFACE}" ]; then
        echo "MCTP interface ${IFACE} is already removed"
        return 0
    fi

    echo "Bringing ${IFACE} down..."
    if ! mctp link set "${IFACE}" down; then
        echo "Failed to bring ${IFACE} down"
        return 1
    fi
}

start_service() {
    local pid

    if ! wait_for_mctp_i3c_link_ready; then
        return 1
    fi

    if ! bind_i3c3_controller; then
        echo "I3C controller setup failed"
        return 1
    fi

    pid="$(wait_for_pid)" || {
        echo "I3C PID is not available"
        stop_service || true
        return 1
    }

    if ! enable_mctp_link "$pid"; then
        echo "MCTP initialization failed"
        stop_service || true
        return 1
    fi

    echo "MCTP initialization complete"
}

stop_service() {
    local rc=0

    remove_mctp_endpoint || rc=1
    disable_mctp_link || rc=1
    unbind_i3c3_controller || rc=1

    if [ "$rc" -eq 0 ]; then
        echo "MCTP cleanup complete"
    else
        echo "MCTP cleanup failed"
    fi

    return "$rc"
}

case "${1:-}" in
    start) start_service ;;
    stop)  stop_service ;;
    *) exit 1 ;;
esac
