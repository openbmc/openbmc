#!/bin/bash
# Compare the number of ttyUSB* to determine the RMC hardware revision
# and apply the appropriate rules.

TTYUSB_NUM_RMC_V1=8
TTYUSB_NUM_RMC_V1PLUS=12
DEFAULT_RULES="rmc-v1plus.rules"

declare -A RULES_MAP=(
    ["${TTYUSB_NUM_RMC_V1}"]="rmc-v1.rules"
    ["${TTYUSB_NUM_RMC_V1PLUS}"]="rmc-v1plus.rules"
    ["default"]="${DEFAULT_RULES}"
)

RULES_SRC_DIR="/etc/rs485-rules"
RULES_DEST="/etc/udev/rules.d/99-persistent-rs485-port-mapping.rules"

TTYUSB_COUNT=$(find /dev -maxdepth 1 -name 'ttyUSB*' 2>/dev/null | wc -l)

if [[ -n "${RULES_MAP[$TTYUSB_COUNT]}" ]]; then
    RULES_FILE="${RULES_SRC_DIR}/${RULES_MAP[$TTYUSB_COUNT]}"
else
    RULES_FILE="${RULES_SRC_DIR}/${RULES_MAP[default]}"
    echo "No rules file defined for TTYUSB_COUNT=${TTYUSB_COUNT}, using default: ${RULES_MAP[default]}"
fi

if [[ -f "${RULES_FILE}" ]]; then
    cp -f "${RULES_FILE}" "${RULES_DEST}"
    echo "Installed ${RULES_FILE} to ${RULES_DEST}"
else
    echo "Error: Rules file ${RULES_FILE} not found"
    exit 1
fi

udevadm control --reload
udevadm trigger
echo "udev rules reloaded and triggered"
