require netcat.inc
SUMMARY = "OpenBSD Netcat"
HOMEPAGE = "http://ftp.debian.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=f39e60ae4ea9fdb559c833be2e59de99"

DEPENDS += "glib-2.0 libbsd"
do_patch[depends] = "quilt-native:do_populate_sysroot"

SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/netcat-openbsd/1.190-2/netcat-openbsd_${PV}.orig.tar.gz;name=netcat \
           https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/netcat-openbsd/1.190-2/netcat-openbsd_${PV}-2.debian.tar.xz;name=netcat-patch;subdir=${BP} \
           file://0001-bundle-own-base64-encode-decode-functions.patch \
           "

SRC_URI[netcat.md5sum] = "dd32fd1d7903b541ad8709794539b959"
SRC_URI[netcat.sha256sum] = "68ccc448392c05ec51baed0167a72b8c650454f990b895d6e6877d416a38e536"
SRC_URI[netcat-patch.md5sum] = "78058b7af0170654b877b02c37716cdf"
SRC_URI[netcat-patch.sha256sum] = "88088af3f520c7825e59bc133d65e70fc4a30139d451c6faabbd9f240bc78374"

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
