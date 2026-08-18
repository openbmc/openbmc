FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS:remove = " \
    external \
"

PACKAGECONFIG:append = " \
    nvmesensor \
"

SRC_URI:append = " \
    file://critical-leak-assert-handler \
    file://warning-leak-assert-handler \
    "

RDEPENDS:${PN}:append = " bash"

do_install:append() {

    install -d ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/critical-leak-assert-handler \
                    ${D}${libexecdir}/${PN}/critical-leak-assert-handler
    install -m 0755 ${UNPACKDIR}/warning-leak-assert-handler \
                    ${D}${libexecdir}/${PN}/warning-leak-assert-handler
}