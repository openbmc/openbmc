require netcat.inc
SUMMARY = "OpenBSD Netcat"
HOMEPAGE = "http://ftp.debian.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=f39e60ae4ea9fdb559c833be2e59de99"

DEPENDS += "glib-2.0 libbsd"
do_patch[depends] = "quilt-native:do_populate_sysroot"

SRC_URI = "http://snapshot.debian.org/archive/debian/20181022T085404Z/pool/main/n/netcat-openbsd/netcat-openbsd_${PV}.orig.tar.gz;name=netcat \
           http://snapshot.debian.org/archive/debian/20181022T085404Z/pool/main/n/netcat-openbsd/netcat-openbsd_${PV}-1.debian.tar.xz;name=netcat-patch;subdir=${BP} \
           file://0001-bundle-own-base64-encode-decode-functions.patch \
           "

SRC_URI[netcat.md5sum] = "219d5e49c45658e229a3bda63063a986"
SRC_URI[netcat.sha256sum] = "0e283b2a214313c69447cd962c528ac19afb3ddfe606b25de6d179f187cde4c3"
SRC_URI[netcat-patch.md5sum] = "7eba241989dbef6caa78ec4bc8e35151"
SRC_URI[netcat-patch.sha256sum] = "c6736fcbab5254cbbc52278993a951da1126e42800a297c27db297e332e2017e"

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
