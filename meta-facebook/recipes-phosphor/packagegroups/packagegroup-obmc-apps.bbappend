FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGES:remove:fb-nohost = "\
        ${PN}-console \
        "
