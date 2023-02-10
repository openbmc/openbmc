FILESEXTRAPATHS:prepend := "${THISDIR}/dbus-sensors:"

PACKAGECONFIG:daytonax = "\
    hwmontempsensor \
    fansensor \
    psusensor \
    adcsensor \
    "
