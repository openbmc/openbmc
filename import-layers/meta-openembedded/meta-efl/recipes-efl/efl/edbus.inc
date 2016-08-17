DESCRIPTION = "DBus and HAL convenience wrappers for EFL"
DEPENDS = "dbus ecore eina"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=550b8cea13dacc4bf00508a8cce1074c"

inherit efl

# -dev and -dbg packages wants ${PN} which is empty here
ALLOW_EMPTY_${PN} = "1"

BBCLASSEXTEND = "native"
INC_PR = "r0"

SRCNAME = "e_dbus"

# it needs to be disabled, because creates circular dependency on elementary
EXTRA_OECONF += "--disable-edbus-performance-test"

PACKAGES =+ "${PN}-enotify ${PN}-edbus ${PN}-enm ${PN}-ehal ${PN}-econnman ${PN}-eofono ${PN}-eukit ${PN}-ebluez"
FILES_${PN}-enotify = "${libdir}/libenotify.so.* ${bindir}/e-notify-send"
FILES_${PN}-edbus = "${libdir}/libedbus.so.*"
FILES_${PN}-enm = "${libdir}/libenm.so.*"
FILES_${PN}-ehal = "${libdir}/libehal.so.*"
FILES_${PN}-econnman = "${libdir}/libeconnman.so.* ${libdir}/libeconnman0_7x.so.*"
FILES_${PN}-eofono = "${libdir}/libeofono.so.*"
FILES_${PN}-eukit = "${libdir}/libeukit.so.*"
FILES_${PN}-ebluez = "${libdir}/libebluez.so.*"
FILES_${PN} += "${datadir}/e_dbus/logo.png"

