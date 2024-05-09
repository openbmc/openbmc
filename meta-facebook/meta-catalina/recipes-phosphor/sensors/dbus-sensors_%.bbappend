FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG = " \
    adcsensor \
    hwmontempsensor \
    psusensor \
    nvmesensor \
    fansensor \
"
