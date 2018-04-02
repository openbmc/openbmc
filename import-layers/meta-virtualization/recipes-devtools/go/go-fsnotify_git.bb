DESCRIPTION = "A golang registry for global request variables."
HOMEPAGE = "https://github.com/go-fsnotify/fsnotify"
SECTION = "devel/go"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c38914c9a7ab03bb2b96d4baaee10769"

SRCNAME = "fsnotify"

PKG_NAME = "github.com/fsnotify/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "836bfd95fecc0f1511dd66bdbf2b5b61ab8b00b6"
PV = "1.2.11+git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_fsnotify_sysroot_preprocess"

go_fsnotify_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
