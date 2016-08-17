DESCRIPTION = "The Docker toolset to pack, ship, store, and deliver content"
HOMEPAGE = "https://github.com/docker/distribution"
SECTION = "devel/go"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRCNAME = "distribution"

PKG_NAME = "github.com/docker/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "d957768537c5af40e4f4cd96871f7b2bde9e2923"

S = "${WORKDIR}/git"

do_unpackpost() {
	rm -rf ${S}/[A-KM-Za-ce-z]* ${S}/doc*
}

addtask unpackpost after do_unpack before do_patch

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_distribution_digeset_sysroot_preprocess"

go_distribution_digeset_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
