FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " \
    cpld-software-update \
    i2cvr-software-update \
"

PACKAGECONFIG:append:fb-compute-singlehost = " \
    bios-software-update \
"
