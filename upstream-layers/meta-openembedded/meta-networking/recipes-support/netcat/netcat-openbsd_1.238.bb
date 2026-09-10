require netcat.inc
SUMMARY = "OpenBSD Netcat"
HOMEPAGE = "http://ftp.debian.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=258066eab695382a48c91d09e865d7c3"

DEPENDS += "glib-2.0 libbsd"
do_patch[depends] = "quilt-native:do_populate_sysroot"

SRC_URI = "http://snapshot.debian.org/archive/debian/20260519T203124Z/pool/main/n/netcat-openbsd/netcat-openbsd_${PV}.orig.tar.gz;name=netcat \
           http://snapshot.debian.org/archive/debian/20260519T203124Z/pool/main/n/netcat-openbsd/netcat-openbsd_${PV}-1.debian.tar.xz;name=netcat-patch;subdir=${BP} \
           file://0001-bundle-own-base64-encode-decode-functions.patch \
           "

SRC_URI[netcat.sha256sum] = "9f72a37d85dec1366309fdb0172be4e67e09ec26e89f13156315149e17c02479"
SRC_URI[netcat-patch.sha256sum] = "eefeb9ff863ebd4384274b132a3ff9b492aacb244e445c34dbfb36cfee596930"

inherit pkgconfig

EXTRA_OEMAKE += "'LDFLAGS=${LDFLAGS}'"

do_configure[noexec] = "1"

netcat_do_patch() {
    cd ${S}
    quilt pop -a || true
    if [ -d ${S}/.pc-netcat ]; then
            rm -rf ${S}/.pc
            mv ${S}/.pc-netcat ${S}/.pc
            QUILT_PATCHES=${S}/debian/patches quilt pop -a
            rm -rf ${S}/.pc
    fi
    QUILT_PATCHES=${S}/debian/patches quilt push -a
    mv ${S}/.pc ${S}/.pc-netcat
}

do_unpack[cleandirs] += "${S}"

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
ALTERNATIVE_PRIORITY = "60"
