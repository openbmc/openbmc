DESCRIPTION = "Native Go bindings for D-Bus"
HOMEPAGE = "https://github.com/godbus/dbus"
SECTION = "devel/go"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09042bd5c6c96a2b9e45ddf1bc517eed"

SRCNAME = "dbus"

PKG_NAME = "github.com/godbus/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "5f6efc7ef2759c81b7ba876593971bfce311eab3"
PV = "4.0.0+git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_dbus_sysroot_preprocess"

go_dbus_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
