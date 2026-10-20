#!/bin/bash

set -euo pipefail

readonly BOOT_RAW_OBJ="/xyz/openbmc_project/state/boot/raw0"
readonly BOOT_RAW_INTERFACE="xyz.openbmc_project.State.Boot.Raw"
readonly BOOT_RAW_SERVICE="xyz.openbmc_project.State.Boot.Raw"

readonly MCTP_I3C_LINK_READY_POST_CODE=0x1C
readonly MCTP_I3C_LINK_READY_FILE="/run/rainier/mctp_i3c_link_ready"
readonly C0_LAST_POST_CODE=0x1F
readonly C1_LAST_POST_CODE=0x1C

instance="${1:-}"
chip="${CHIP:-}"
line_names_env="${LINE_NAMES:-}"
poll_sec="${POLL_SEC:-0.02}"
chiplet_offset=0
last_post_code=0
last_chiplet_post_code=""
read_chiplet_post_code=0
first_post_code=true
postcode_monitor_done=false

declare -a line_names=()
declare -a gpio_offsets=()
declare -a read_bits=()

print_error() { echo "ERROR: $1" >&2; }
print_info() { echo "INFO: $1"; }

format_hex_byte() {
    printf '0x%02X' "$1"
}

pack_5bits_lsb_first() {
    local value=0
    local index

    for index in "${!read_bits[@]}"; do
        if (( index >= 5 )); then
            break
        fi

        if (( read_bits[index] )); then
            (( value |= (1 << index) ))
        fi
    done

    read_chiplet_post_code=$(( value & 0x1F ))
}

read_gpio_bits_once() {
    local values_output

    if ! values_output=$(gpioget "${chip}" "${gpio_offsets[@]}" 2>/dev/null); then
        return 1
    fi

    read -r -a read_bits <<< "${values_output}"
    if (( ${#read_bits[@]} != 5 )); then
        return 1
    fi

    pack_5bits_lsb_first
}

resolve_gpio_offsets() {
    local line_name
    local found
    local found_chip
    local offset

    for line_name in "${line_names[@]}"; do
        found=$(gpiofind "${line_name}" 2>/dev/null) || {
            print_error "GPIO line not found: ${line_name}"
            exit 1
        }
        read -r found_chip offset <<< "${found}"
        if [[ -z "${chip}" ]]; then
            chip="${found_chip}"
            if [[ ! -e "/dev/${chip}" ]]; then
                print_error "Failed to open gpio chip: ${chip}"
                exit 1
            fi
        fi
        if [[ "${found_chip}" != "${chip}" || -z "${offset:-}" ]]; then
            print_error "GPIO line not found: ${line_name}"
            exit 1
        fi
        gpio_offsets+=("${offset}")
    done
}

set_boot_raw_property() {
    local soc_post_code="$1"

    busctl set-property \
        "${BOOT_RAW_SERVICE}" \
        "${BOOT_RAW_OBJ}" \
        "${BOOT_RAW_INTERFACE}" \
        Value \
        '(ayay)' \
        1 \
        "${soc_post_code}" \
        0 >/dev/null 2>&1 || return 1
}

set_mctp_i3c_link_ready() {
    local state="$1"

    [[ "${instance}" == "c0" ]] || return 0

    printf '%s\n' "${state}" > "${MCTP_I3C_LINK_READY_FILE}"
}

init_mctp_i3c_link_ready() {
    [[ "${instance}" == "c0" ]] || return 0

    mkdir -p "$(dirname "${MCTP_I3C_LINK_READY_FILE}")"
    [[ -e "${MCTP_I3C_LINK_READY_FILE}" ]] || set_mctp_i3c_link_ready false
}

update_mctp_i3c_link_ready() {
    local chiplet_post_code="$1"

    if (( chiplet_post_code >= MCTP_I3C_LINK_READY_POST_CODE )); then
        set_mctp_i3c_link_ready true ||
            print_error "MCTP I3C link ready state update failed"
    fi
}

clear_mctp_i3c_link_ready() {
    [[ "${instance}" == "c0" ]] || return 0

    mkdir -p "$(dirname "${MCTP_I3C_LINK_READY_FILE}")"
    set_mctp_i3c_link_ready false ||
        print_error "MCTP I3C link ready state clear failed"
}

exit_post_code_monitor() {
    postcode_monitor_done=true
}

main() {
    if [[ "${2:-}" == "stop" ]]; then
        clear_mctp_i3c_link_ready
        exit 0
    fi

    if [[ -z "${line_names_env}" ]]; then
        print_error "LINE_NAMES must be set"
        exit 1
    fi

    read -r -a line_names <<< "${line_names_env}"
    if (( ${#line_names[@]} != 5 )); then
        print_error "Exactly 5 GPIO line names required"
        exit 1
    fi

    init_mctp_i3c_link_ready ||
        print_error "MCTP I3C link ready state initialization failed"
    resolve_gpio_offsets

    if [[ "${instance}" == "c1" ]]; then
        chiplet_offset=32
        last_post_code="${C1_LAST_POST_CODE}"
    elif [[ "${instance}" == "c0" ]]; then
        chiplet_offset=0
        last_post_code="${C0_LAST_POST_CODE}"
    else
        print_error "Instance must be 'c0' or 'c1'"
        exit 1
    fi

    while [[ "${postcode_monitor_done}" == "false" ]]; do
        if read_gpio_bits_once; then
            local chiplet_post_code="${read_chiplet_post_code}"
            local soc_post_code=$(( chiplet_post_code + chiplet_offset ))

            if [[ "${chiplet_post_code}" != "${last_chiplet_post_code}" ]]; then
                update_mctp_i3c_link_ready "${chiplet_post_code}"

                if "${first_post_code}" && (( chiplet_post_code == last_post_code )); then
                    print_info "[${instance}] Last POST code $(format_hex_byte "${chiplet_post_code}") detected as first POST code; stopping polling"
                    exit_post_code_monitor
                    break
                fi

                set_boot_raw_property "${soc_post_code}" || print_error "DBus Boot.Raw update failed"
                print_info "[${instance}] POST code updated chiplet_post_code=$(format_hex_byte "${chiplet_post_code}") soc_post_code=$(format_hex_byte "${soc_post_code}")"
                last_chiplet_post_code="${chiplet_post_code}"
                first_post_code=false

                if (( chiplet_post_code == last_post_code )); then
                    print_info "[${instance}] Last POST code $(format_hex_byte "${chiplet_post_code}") detected; stopping polling"
                    exit_post_code_monitor
                    break
                fi
            fi
        else
            print_error "[${instance}] Failed to read GPIO values"
        fi

        sleep "${poll_sec}"
    done

    print_info "[${instance}] POST code monitoring completed"
}

main "$@"
