require netcat.inc
SUMMARY = "OpenBSD Netcat"
HOMEPAGE = "http://ftp.debian.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=7c58e21ba8c9e76c25f46b2182b23bb8"

DEPENDS += "glib-2.0 libbsd"
do_patch[depends] = "quilt-native:do_populate_sysroot"

SRC_URI = "http://snapshot.debian.org/archive/debian/20250301T025722Z/pool/main/n/netcat-openbsd/netcat-openbsd_${PV}.orig.tar.gz;name=netcat \
           http://snapshot.debian.org/archive/debian/20250301T025722Z/pool/main/n/netcat-openbsd/netcat-openbsd_${PV}-1.debian.tar.xz;name=netcat-patch;subdir=${BP} \
           file://0001-bundle-own-base64-encode-decode-functions.patch \
           file://0001-fix-netcat-openbsd-ftbfs-with-GCC-15.patch \
           "

SRC_URI[netcat.sha256sum] = "e501b0239db0d8f981b964adee74effe80e6337e5d402a00515a6df8d933269e"
SRC_URI[netcat-patch.sha256sum] = "7989cc18b3ffa4ef1da57149bd3fc03999546a91c35b82b49caf4d758015c573"

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
