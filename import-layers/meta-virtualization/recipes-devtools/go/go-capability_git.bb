DESCRIPTION = "Utilities for manipulating POSIX capabilities in Go."
HOMEPAGE = "https://github.com/syndtr/gocapability"
SECTION = "devel/go"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a7304f5073e7be4ba7bffabbf9f2bbca"

SRCNAME = "gocapability"

PKG_NAME = "github.com/syndtr/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "8e4cdcb3c22b40d5e330ade0b68cb2e2a3cf6f98"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_capability_sysroot_preprocess"

go_capability_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
