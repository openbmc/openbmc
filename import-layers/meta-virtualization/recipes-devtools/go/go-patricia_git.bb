DESCRIPTION = "A generic patricia trie (also called radix tree) implemented in Go (Golang)"
HOMEPAGE = "https://github.com/gorilla/context"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9949b99212edd6b1e24ce702376c3baf"

SRCNAME = "go-patricia"

PKG_NAME = "github.com/tchap/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "666120de432aea38ab06bd5c818f04f4129882c9"
PV = "2.2.6+git${SRCPV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_patricia_sysroot_preprocess"

go_patricia_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
