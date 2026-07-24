#!/bin/bash

set -euo pipefail

readonly BOOT_RAW_OBJ="/xyz/openbmc_project/state/boot/raw0"
readonly BOOT_RAW_INTERFACE="xyz.openbmc_project.State.Boot.Raw"
readonly BOOT_RAW_SERVICE="xyz.openbmc_project.State.Boot.Raw"

readonly LAST_POST_CODE_C0=0x1B
readonly LAST_POST_CODE_C1=0x3B
readonly LOG_TAG="rse-postcode-monitor"

instance="${1:-}"
chip=""
line_names_env="${LINE_NAMES:-}"
poll_sec="${POLL_SEC:-0.02}"
chiplet_offset=0
last_post_code=0
last_bits_key=""
read_raw_post_code=0

declare -a line_names=()
declare -a gpio_offsets=()
declare -a read_bits=()

log_message() {
    local priority="$1"
    shift
    local message="$*"

    if command -v systemd-cat >/dev/null 2>&1; then
        printf '%s\n' "${message}" |
            systemd-cat -t "${LOG_TAG}" -p "${priority}"
    else
        logger -t "${LOG_TAG}" -p "user.${priority}" -- "${message}"
    fi
}

log_info() {
    log_message info "$@"
}

log_error() {
    log_message err "$@" >&2
}

die() {
    log_error "$@"
    exit 1
}

format_hex_byte() {
    printf '0x%02X' "$1"
}

require_command() {
    local tool="$1"

    command -v "${tool}" >/dev/null 2>&1 ||
        die "Required command not found: ${tool}"
}

pack_5bits_lsb_first() {
    local value=0
    local index

    # LINE_NAMES are ordered bit0..bit4, so the first GPIO is the LSB.
    for index in "${!read_bits[@]}"; do
        if (( index >= 5 )); then
            break
        fi

        if (( read_bits[index] )); then
            (( value |= (1 << index) ))
        fi
    done

    read_raw_post_code=$(( value & 0x1F ))
}

read_gpio_bits_once() {
    local values_output

    if ! values_output=$(
        gpioget "${chip}" "${gpio_offsets[@]}" 2>/dev/null
    ); then
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
        found=$(gpiofind "${line_name}" 2>/dev/null) ||
            die "GPIO line not found: ${line_name}"
        read -r found_chip offset <<< "${found}"
        [[ -n "${found_chip:-}" && -n "${offset:-}" ]] ||
            die "GPIO line not found: ${line_name}"

        if [[ -z "${chip}" ]]; then
            chip="${found_chip}"
            [[ -e "/dev/${chip}" ]] || die "Failed to open gpio chip: ${chip}"
        fi

        [[ "${found_chip}" == "${chip}" ]] ||
            die "GPIO line ${line_name} found on ${found_chip}," \
                "expected ${chip}"
        gpio_offsets+=("${offset}")
    done
}

set_boot_raw_property() {
    local published_post_code="$1"

    busctl set-property \
        "${BOOT_RAW_SERVICE}" \
        "${BOOT_RAW_OBJ}" \
        "${BOOT_RAW_INTERFACE}" \
        Value \
        '(ayay)' \
        1 \
        "${published_post_code}" \
        0 >/dev/null 2>&1 || return 1
}

publish_post_code() {
    local raw_post_code="$1"
    local published_post_code="$2"

    set_boot_raw_property "${published_post_code}" ||
        log_error "DBus Boot.Raw update failed"
    log_info \
        "[${instance}] POST code updated" \
        "raw=$(format_hex_byte "${raw_post_code}")" \
        "published=$(format_hex_byte "${published_post_code}")"

    if (( published_post_code == last_post_code )); then
        log_info \
            "[${instance}] Last POST code" \
            "$(format_hex_byte "${published_post_code}") detected;" \
            "exiting monitor"
        exit 0
    fi
}

main() {
    require_command busctl
    require_command gpiofind
    require_command gpioget

    [[ -n "${line_names_env}" ]] || die "LINE_NAMES must be set"

    read -r -a line_names <<< "${line_names_env}"
    (( ${#line_names[@]} == 5 )) || die "Exactly 5 GPIO line names required"

    if [[ "${instance}" == "c1" ]]; then
        chiplet_offset=32
        last_post_code=$((LAST_POST_CODE_C1))
    elif [[ "${instance}" == "c0" ]]; then
        chiplet_offset=0
        last_post_code=$((LAST_POST_CODE_C0))
    else
        die "Instance must be 'c0' or 'c1'"
    fi

    resolve_gpio_offsets

    if read_gpio_bits_once; then
        local raw_post_code="${read_raw_post_code}"
        local published_post_code=$(( raw_post_code + chiplet_offset ))

        log_info \
            "[${instance}] Initial POST read" \
            "raw=$(format_hex_byte "${raw_post_code}")" \
            "published=$(format_hex_byte "${published_post_code}")"
        last_bits_key="${read_bits[*]}"

        if (( published_post_code == last_post_code )); then
            log_info \
                "[${instance}] Last POST code" \
                "$(format_hex_byte "${published_post_code}") detected;" \
                "exiting monitor"
            exit 0
        fi
    else
        log_error "[${instance}] Initial GPIO read failed; continuing"
    fi

    while true; do
        if read_gpio_bits_once; then
            local bits_key="${read_bits[*]}"

            if [[ "${bits_key}" != "${last_bits_key}" ]]; then
                local raw_post_code="${read_raw_post_code}"
                local published_post_code=$(( raw_post_code + chiplet_offset ))

                publish_post_code "${raw_post_code}" "${published_post_code}"
                last_bits_key="${bits_key}"
            fi
        else
            log_error "[${instance}] Failed to read GPIO values"
        fi

        sleep "${poll_sec}"
    done
}

main "$@"
