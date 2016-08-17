DESCRIPTION = "PTY interface for Go"
HOMEPAGE = "https://github.com/kr/pty"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://License;md5=93958070863d769117fa33b129020050"

SRCNAME = "pty"

PKG_NAME = "github.com/kr/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "05017fcccf23c823bfdea560dcc958a136e54fb7"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_pty_sysroot_preprocess"

go_pty_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
