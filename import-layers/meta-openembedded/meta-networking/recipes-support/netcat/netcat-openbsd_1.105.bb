require netcat.inc
SUMMARY = "OpenBSD Netcat"
HOMEPAGE = "http://ftp.debian.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../debian/copyright;md5=ee6bbaacb5db5f2973818f0902c3ae6f"

DEPENDS += "glib-2.0 libbsd"

SRC_URI = "${DEBIAN_MIRROR}/main/n/netcat-openbsd/netcat-openbsd_${PV}.orig.tar.gz;name=netcat \
           ${DEBIAN_MIRROR}/main/n/netcat-openbsd/netcat-openbsd_${PV}-7.debian.tar.gz;name=netcat-patch"

SRC_URI[netcat.md5sum] = "7e67b22f1ad41a1b7effbb59ff28fca1"
SRC_URI[netcat.sha256sum] = "40653fe66c1516876b61b07e093d826e2a5463c5d994f1b7e6ce328f3edb211e"
SRC_URI[netcat-patch.md5sum] = "e914f8eb7eda5c75c679dd77787ac76b"
SRC_URI[netcat-patch.sha256sum] = "eee759327ffea293e81d0dde67921b7fcfcad279ffd7a2c9d037bbc8f882b363"

S = "${WORKDIR}/${BPN}-${PV}"

EXTRA_OEMAKE += "'LDFLAGS=${LDFLAGS}'"

do_configure[noexec] = "1"

netcat_do_patch() {
    cd ${S}
    quilt pop -a || true
    QUILT_PATCHES=${WORKDIR}/debian/patches QUILT_SERIES=${WORKDIR}/debian/patches/series quilt push -a
}

python do_patch() {
    bb.build.exec_func('netcat_do_patch', d)
    bb.build.exec_func('patch_do_patch', d)
}

do_compile() {
    cd ${S}
    pkgrel=4
    oe_runmake CFLAGS="$CFLAGS -DDEBIAN_VERSION=\"\\\"${pkgrel}\\\"\""
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${S}/nc ${D}${bindir}/nc.${BPN}
}
ALTERNATIVE_PRIORITY = "50"
