SYS_POSTCODE_DISPLAY_PATH="/sys/bus/i2c/devices/12-000f/postcode-display-slot"

EXTRA_OEMESON:append = " \
    -Dpostcode-display-path=${SYS_POSTCODE_DISPLAY_PATH} \
"
