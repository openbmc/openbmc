DESCRIPTION = "Primitives for identity and authorization"
HOMEPAGE = "https://github.com/docker/libtrust"
SECTION = "devel/go"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435b266b3899aa8a959f17d41c56def8"

SRCNAME = "libtrust"

PKG_NAME = "github.com/docker/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "230dfd18c2326f1e9d08238710e67a1040187d07"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_libtrust_sysroot_preprocess"

go_libtrust_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
