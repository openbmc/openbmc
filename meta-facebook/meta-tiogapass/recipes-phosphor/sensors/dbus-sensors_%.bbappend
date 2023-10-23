FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append:tiogapass = " \
        intelcpusensor \
        "
