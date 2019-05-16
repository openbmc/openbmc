SUMMARY = "Tools for managing AUFS mounts"
SECTION = "base"
HOMEPAGE = "http://aufs.sourceforge.net/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

DEPENDS = "coreutils-native aufs-util-native"
DEPENDS_class-native = ""

SRCREV = "8f35db59ef83078f87879ec2828e0bb45719e0ef"
SRC_URI = "git://git.code.sf.net/p/aufs/aufs-util;protocol=git;branch=aufs4.9 \
           https://raw.githubusercontent.com/sfjro/aufs4-linux/aufs4.9/include/uapi/linux/aufs_type.h;name=aufs_type \
           file://aufs-util-don-t-strip-executables.patch \
           file://aufs-util-add-tool-concept-to-Makefile-for-cross-com.patch \
"
SRC_URI[aufs_type.md5sum] = "b37129ef0703de72a852db7e48bdedc6"
SRC_URI[aufs_type.sha256sum] = "7ff6566adb9c7a3b6862cdc85a690ab546f1d0bc81ddd595fd663c0a69031683"

UPSTREAM_CHECK_COMMITS = "1"

PV = "4.9+git${SRCPV}"

S = "${WORKDIR}/git"

export HOSTCC = "${BUILD_CC}"
do_configure_prepend() {
   # Replace sbin,bin paths with bitbake environment
   sed -i -e 's;install_sbin: Tgt = ${DESTDIR}/sbin;install_sbin: Tgt = ${DESTDIR}/${base_sbindir};' \
          -e 's;install_ubin: Tgt = ${DESTDIR}/usr/sbin;install_sbin: Tgt = ${DESTDIR}/${bindir};' \
	  ${S}/Makefile
}

do_configure_append () {
    install -d ${S}/include/linux/
    cp ${WORKDIR}/aufs_type.h ${S}/include/linux/
    sed -i -e 's;__user;;' ${S}/include/linux/aufs_type.h
}

do_configure_append_class-target () {
    for i in ver c2sh c2tmac; do
        cp ${STAGING_BINDIR_NATIVE}/aufs-util-${PV}/$i ${B}
    done
}

do_compile () {
    oe_runmake CPPFLAGS="-I${S}/include -I${S}/libau"
}

do_compile_class-native () {
    oe_runmake tools CPPFLAGS="-I${S}/include -I${S}/libau" CC="${BUILD_CC}"
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install_sbin install_ubin install_etc
}

do_install_class-native () {
    install -d ${D}${bindir}/aufs-util-${PV}
    for i in ver c2sh c2tmac; do
        install -m 755 $i ${D}${bindir}/aufs-util-${PV}/$i
    done
}

RRECOMMENDS_${PN}_class-target += "kernel-module-aufs"

BBCLASSEXTEND = "native"
