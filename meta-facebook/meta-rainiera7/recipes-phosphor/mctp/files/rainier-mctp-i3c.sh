#!/usr/bin/env bash
set -euo pipefail

SVC="au.com.codeconstruct.MCTP1"
ROOT="/au/com/codeconstruct/mctp1"

IFACE="mctpi3c3"
LOCAL_EID="8"
HOST_EID="9"
HOST_PID="55e712345678"

I3C_CONTROLLER_BASE="14c27000"
I3C_DRIVER_PATH="/sys/bus/platform/drivers/mipi-i3c-hci"

MCTP_POST_READY="rainier-mctp-ready@soc-post.target"
MCTP_POWER_READY="rainier-mctp-ready@power-good.target"

I3C_REBIND_RETRIES=2
I3C_REBIND_DELAY_SEC=1

I3C_DISCOVERY_ATTEMPTS=5
I3C_DISCOVERY_DELAY_SEC=1

ENDPOINT_ASSIGN_ATTEMPTS=5
ENDPOINT_ASSIGN_DELAY_SEC=1

ready_marker_is_active() {
    systemctl --quiet is-active "$1"
}

is_platform_ready() {
    ready_marker_is_active "$MCTP_POST_READY" &&
        ready_marker_is_active "$MCTP_POWER_READY"
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

get_host_pid() {
    local controller
    local bus_path
    local pid
    local pid_file

    controller="$(find_i3c_controller)" || return 1
    bus_path="/sys/bus/i3c/devices/${controller}"

    for pid_file in "${bus_path}"/*/pid; do
        [ -r "$pid_file" ] || continue

        pid="$(cat "$pid_file")" || continue

        if [ "$pid" = "$HOST_PID" ]; then
            printf '%s\n' "$pid"
            return 0
        fi
    done

    return 1
}

wait_for_i3c_interface() {
    local attempt

    for ((attempt = 1; attempt <= I3C_DISCOVERY_ATTEMPTS; attempt++)); do
        if [ -e "/sys/class/net/${IFACE}" ]; then
            return 0
        fi

        if [ "$attempt" -lt "$I3C_DISCOVERY_ATTEMPTS" ]; then
            sleep "$I3C_DISCOVERY_DELAY_SEC"
        fi
    done

    echo "MCTP I3C interface ${IFACE} did not appear after" \
        "${I3C_DISCOVERY_ATTEMPTS} attempts"
    return 1
}

wait_for_pid() {
    local attempt
    local pid

    for ((attempt = 1; attempt <= I3C_DISCOVERY_ATTEMPTS; attempt++)); do
        if pid="$(get_host_pid)"; then
            printf '%s\n' "$pid"
            return 0
        fi

        if [ "$attempt" -lt "$I3C_DISCOVERY_ATTEMPTS" ]; then
            sleep "$I3C_DISCOVERY_DELAY_SEC"
        fi
    done

    return 1
}

bind_i3c_controller() {
    local controller
    local platform_path

    controller="$(find_i3c_controller)" || return 1
    platform_path="/sys/bus/platform/devices/${controller}"

    if [ ! -e "$platform_path" ]; then
        echo "I3C platform device is not present: ${platform_path}"
        return 1
    fi

    if [ -e "${I3C_DRIVER_PATH}/${controller}" ]; then
        echo "I3C controller ${controller} already bound"
        return 0
    fi

    if [ ! -w "${I3C_DRIVER_PATH}/bind" ]; then
        echo "I3C driver bind node is not available:" \
            "${I3C_DRIVER_PATH}/bind"
        return 1
    fi

    echo "Binding ${controller}"

    if ! printf '%s\n' "$controller" > "${I3C_DRIVER_PATH}/bind"; then
        echo "Failed to bind ${controller}"
        return 1
    fi

    if [ ! -e "${I3C_DRIVER_PATH}/${controller}" ]; then
        echo "I3C controller ${controller} is not bound after bind"
        return 1
    fi

    return 0
}

unbind_i3c_controller() {
    local controller

    controller="$(find_i3c_controller)" || return 1

    if [ ! -e "${I3C_DRIVER_PATH}/${controller}" ]; then
        echo "I3C controller ${controller} already unbound"
        return 0
    fi

    if [ ! -w "${I3C_DRIVER_PATH}/unbind" ]; then
        echo "I3C driver unbind node is not available:" \
            "${I3C_DRIVER_PATH}/unbind"
        return 1
    fi

    echo "Unbinding ${controller}"

    if ! printf '%s\n' "$controller" > "${I3C_DRIVER_PATH}/unbind"; then
        echo "Failed to unbind ${controller}"
        return 1
    fi

    if [ -e "${I3C_DRIVER_PATH}/${controller}" ]; then
        echo "I3C controller ${controller} is still bound after unbind"
        return 1
    fi

    return 0
}

local_eid_present() {
    mctp address show "$IFACE" 2>/dev/null | awk \
        -v eid="$LOCAL_EID" -v dev="$IFACE" \
        '$1 == "eid" && $2 == eid && $5 == "dev" && $6 == dev {
            found = 1
        }
        END {
            exit !found
        }'
}

ensure_local_eid() {
    if local_eid_present; then
        echo "Local EID ${LOCAL_EID} already present on ${IFACE}"
        return 0
    fi

    echo "Adding local EID ${LOCAL_EID} to ${IFACE}"

    if ! mctp address add "$LOCAL_EID" dev "$IFACE"; then
        echo "Failed to add local EID ${LOCAL_EID} to ${IFACE}"
        return 1
    fi

    if ! local_eid_present; then
        echo "Local EID ${LOCAL_EID} is still missing from ${IFACE}"
        return 1
    fi

    return 0
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
        END {
            exit !found
        }'
}

get_endpoint_path() {
    local network
    local path

    network="$(get_network_id)" || return 1
    [ -n "$network" ] || return 1

    path="${ROOT}/networks/${network}/endpoints/${HOST_EID}"

    busctl tree --list "$SVC" 2>/dev/null | \
        grep -Fx "$path" >/dev/null || return 1

    printf '%s\n' "$path"
    return 0
}

remove_mctp_endpoint() {
    local path

    path="$(get_endpoint_path || true)"
    [ -n "$path" ] || return 0

    echo "Removing MCTP endpoint ${path}"

    if ! busctl call \
        "$SVC" \
        "$path" \
        au.com.codeconstruct.MCTP.Endpoint1 \
        Remove; then
        echo "Failed to remove MCTP endpoint ${path}"
        return 1
    fi

    return 0
}

endpoint_is_assigned() {
    get_endpoint_path >/dev/null
}

assign_mctp_endpoint() {
    local pid
    local obj
    local attempt
    local offset
    local -a bus_bytes=()

    if endpoint_is_assigned; then
        echo "Host EID ${HOST_EID} is already assigned on ${IFACE}"
        return 0
    fi

    pid="$(wait_for_pid)" || {
        echo "I3C target PID ${HOST_PID} was not found after" \
            "${I3C_DISCOVERY_ATTEMPTS} attempts"
        return 1
    }

    if [[ ! "$pid" =~ ^[[:xdigit:]]{12}$ ]]; then
        echo "Invalid I3C PID format: ${pid}"
        return 1
    fi

    for ((offset = 0; offset < ${#pid}; offset += 2)); do
        bus_bytes+=("0x${pid:offset:2}")
    done

    obj="${ROOT}/interfaces/${IFACE}"

    echo "Assigning MCTP I3C endpoint EID ${HOST_EID} with PID ${pid}"

    for ((attempt = 1; attempt <= ENDPOINT_ASSIGN_ATTEMPTS; attempt++)); do
        if ! is_platform_ready; then
            echo "Platform readiness was lost while assigning" \
                "EID ${HOST_EID}"
            return 1
        fi

        if busctl call \
            "$SVC" \
            "$obj" \
            au.com.codeconstruct.MCTP.BusOwner1 \
            AssignEndpointStatic \
            ayy "${#bus_bytes[@]}" "${bus_bytes[@]}" "$HOST_EID"; then
            echo "AssignEndpointStatic succeeded"
            return 0
        fi

        echo "AssignEndpointStatic attempt ${attempt}/" \
            "${ENDPOINT_ASSIGN_ATTEMPTS} failed"

        if [ "$attempt" -lt "$ENDPOINT_ASSIGN_ATTEMPTS" ]; then
            sleep "$ENDPOINT_ASSIGN_DELAY_SEC"
        fi
    done

    echo "AssignEndpointStatic failed after" \
        "${ENDPOINT_ASSIGN_ATTEMPTS} attempts"
    return 1
}

enable_mctp_link() {
    if ! wait_for_i3c_interface; then
        return 1
    fi

    echo "Bringing ${IFACE} up"

    if ! mctp link set "$IFACE" up; then
        echo "Failed to bring ${IFACE} up"
        return 1
    fi

    ensure_local_eid || return 1
    assign_mctp_endpoint
}

disable_mctp_link() {
    if [ ! -e "/sys/class/net/${IFACE}" ]; then
        echo "MCTP interface ${IFACE} is not present"
        return 0
    fi

    echo "Bringing ${IFACE} down"

    if ! mctp link set "$IFACE" down; then
        echo "Failed to bring ${IFACE} down"
        return 1
    fi

    return 0
}

reinitialize_controller() {
    # Endpoint and link cleanup are best-effort, but controller unbind
    # must succeed for this to be a real controller reinitialization.
    remove_mctp_endpoint || true
    disable_mctp_link || true

    if ! unbind_i3c_controller; then
        echo "Failed to reinitialize I3C controller"
        return 1
    fi

    sleep "$I3C_REBIND_DELAY_SEC"
    return 0
}

setup_once() {
    bind_i3c_controller || return 1
    enable_mctp_link
}

start_service() {
    local retry

    # ExecCondition performs the initial readiness gate. Keep this silent
    # check to catch a readiness transition between ExecCondition and setup.
    if ! is_platform_ready; then
        echo "Platform readiness was lost before MCTP I3C setup started"
        return 1
    fi

    for ((retry = 0; retry <= I3C_REBIND_RETRIES; retry++)); do
        if ! is_platform_ready; then
            echo "Platform readiness was lost before MCTP I3C setup completed"
            return 1
        fi

        if setup_once; then
            echo "MCTP I3C initialization complete"
            return 0
        fi

        if [ "$retry" -ge "$I3C_REBIND_RETRIES" ]; then
            break
        fi

        echo "I3C MCTP setup failed; reinitializing controller before" \
            "retry $((retry + 1))/${I3C_REBIND_RETRIES}"

        if ! reinitialize_controller; then
            echo "Failed to reinitialize I3C controller before retry"
            return 1
        fi
    done

    echo "MCTP I3C initialization failed"
    return 1
}

stop_service() {
    # Teardown is best-effort. Do not block host poweroff/reset or an
    # explicit service restart if cleanup fails after the platform is
    # already unavailable.
    remove_mctp_endpoint || true
    disable_mctp_link || true
    unbind_i3c_controller || true

    return 0
}

case "${1:-}" in
    start) start_service ;;
    stop) stop_service ;;
    *)
        echo "Usage: $0 {start|stop}" >&2
        exit 1
        ;;
esac
