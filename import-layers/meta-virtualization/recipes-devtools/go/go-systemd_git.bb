DESCRIPTION = "Go bindings to systemd socket activation, journal, D-Bus, and unit files"
HOMEPAGE = "https://github.com/coreos/go-systemd"
SECTION = "devel/go"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19cbd64715b51267a47bf3750cc6a8a5"

SRCNAME = "systemd"

PKG_NAME = "github.com/coreos/go-${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "f743bc15d6bddd23662280b4ad20f7c874cdd5ad"
PV = "2+git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_systemd_sysroot_preprocess"

go_systemd_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
