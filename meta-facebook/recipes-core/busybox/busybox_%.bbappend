FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append = " \
    file://timeout.cfg \
    file://wall.cfg \
"
