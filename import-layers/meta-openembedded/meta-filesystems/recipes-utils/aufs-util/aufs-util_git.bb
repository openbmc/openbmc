SUMMARY = "Tools for managing AUFS mounts"
SECTION = "base"
HOMEPAGE = "http://aufs.sourceforge.net/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

DEPENDS = "coreutils-native aufs-util-native"
DEPENDS_class-native = ""

SRCREV = "89afb1806c3d2eed8db2666ae254b77518ae3ceb"
SRC_URI = "git://git.code.sf.net/p/aufs/aufs-util;protocol=git;branch=aufs4.4 \
           https://raw.githubusercontent.com/sfjro/aufs4-linux/aufs4.4/include/uapi/linux/aufs_type.h;name=aufs_type \
           file://aufs-util-don-t-strip-executables.patch \
           file://aufs-util-add-tool-concept-to-Makefile-for-cross-com.patch \
           file://0001-libau-Define-STRIP-weakly.patch \
"
SRC_URI[aufs_type.md5sum] = "f7b4a255dcb55fe7b0967f5f59b44f19"
SRC_URI[aufs_type.sha256sum] = "85bc8e4c1a94a7d526c382e4b047b4256cab8c4a65fc0396291707ad9a327a18"

PV = "4.4+git${SRCPV}"

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

RRECOMMENDS_${PN} += "kernel-module-aufs"

BBCLASSEXTEND = "native"
