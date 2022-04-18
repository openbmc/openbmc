FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:bletchley = " \
    adcsensor \
    fansensor \
    hwmontempsensor \
    psusensor \
"
