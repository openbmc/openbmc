FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " \
    cpld-software-update \
    i2cvr-software-update \
    eepromdevice-software-update \
    tpm-software-update \
"

PACKAGECONFIG:append:fb-compute-singlehost = " \
    bios-software-update \
"
