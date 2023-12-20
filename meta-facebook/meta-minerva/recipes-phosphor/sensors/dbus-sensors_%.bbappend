FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:minerva = " \
    adcsensor \
    fansensor \
    hwmontempsensor \
    psusensor \
"
