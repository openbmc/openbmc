FILESEXTRAPATHS:prepend := "${ARMFILESPATHS}"

# Enable ARM-FFA transport
SRC_URI:append = " \
    file://arm-ffa-transport.cfg \
    "
