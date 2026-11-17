FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

FACEBOOK_REMOVED_DBUS_SENSORS:remove = " \
    external \
"

PACKAGECONFIG:append = " \
    nvmesensor \
"

SRC_URI:append = " \
    file://critical-leak-assert-handler \
    "

RDEPENDS:${PN}:append = " bash"

do_install:append() {

    install -d ${D}${libexecdir}/${BPN}/
    install -m 0755 ${UNPACKDIR}/critical-leak-assert-handler \
                    ${D}${libexecdir}/${BPN}/critical-leak-assert-handler
}