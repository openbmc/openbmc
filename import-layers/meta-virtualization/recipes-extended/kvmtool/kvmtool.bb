SUMMARY = "Native Linux KVM tool"
DESCRIPTION = "kvmtool is a lightweight tool for hosting KVM guests."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=fcb02dc552a041dee27e4b85c7396067"

DEPENDS = "dtc libaio zlib"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/will/kvmtool.git \
           file://external-crosscompiler.patch \
    "

SRCREV = "0093df80d754e1a05b016e5a4ccd4b51a00c562c"
PV = "3.18.0+git${SRCREV}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE='ARCH="${TARGET_ARCH}" V=1'

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/lkvm ${D}${bindir}/
}
