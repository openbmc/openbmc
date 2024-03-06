FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = " \
    external \
    intrusionsensor \
    intelcpusensor \
"
