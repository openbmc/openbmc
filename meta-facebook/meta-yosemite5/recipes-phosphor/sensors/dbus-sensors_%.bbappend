FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-dbus-service

PACKAGECONFIG = " \
    adcsensor \
    hwmontempsensor \
    psusensor \
    fansensor \
"
