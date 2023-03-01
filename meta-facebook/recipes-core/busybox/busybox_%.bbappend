FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " \
    file://timeout.cfg \
    file://wall.cfg \
"
