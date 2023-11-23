FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:harma = " \
    adcsensor \
    hwmontempsensor \
    psusensor \
"
