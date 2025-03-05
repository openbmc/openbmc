SERIAL_DEVICE = "ttyAMA2"

PACKAGECONFIG:remove = "transport-null"
PACKAGECONFIG:append = " \
    transport-serial \
"

