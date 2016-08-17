DESCRIPTION = "A small package for building command line apps in Go"
HOMEPAGE = "https://github.com/codegangsta/cli"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed9b539ed65d73926f30ff1f1587dc44"

SRCNAME = "cli"

PKG_NAME = "github.com/codegangsta/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "27ecc97192df1bf053a22b04463f2b51b8b8373e"
PV = "1.1.0+git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_cli_sysroot_preprocess"

go_cli_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
