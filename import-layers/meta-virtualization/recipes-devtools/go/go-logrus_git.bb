DESCRIPTION = "A golang registry for global request variables."
HOMEPAGE = "https://github.com/Sirupsen/logrus"
SECTION = "devel/go"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8dadfef729c08ec4e631c4f6fc5d43a0"

SRCNAME = "logrus"

PKG_NAME = "github.com/Sirupsen/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git"

SRCREV = "d26492970760ca5d33129d2d799e34be5c4782eb"
PV = "0.11.0+git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	cp -r ${S}/* ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_logrus_sysroot_preprocess"

go_logrus_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
