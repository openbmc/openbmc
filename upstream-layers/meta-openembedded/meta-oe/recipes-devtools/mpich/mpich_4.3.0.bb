SUMMARY = "Message Passing Interface (MPI) implementation"
HOMEPAGE = "http://www.mpich.org/"
SECTION = "devel"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=3a296dfb961b957b0e8adf67d8478d3d"

SRC_URI = "http://www.mpich.org/static/downloads/${PV}/mpich-${PV}.tar.gz"
SRC_URI[sha256sum] = "5e04132984ad83cab9cc53f76072d2b5ef5a6d24b0a9ff9047a8ff96121bcc63"

RDEPENDS:${PN} += "bash perl libxml2"

EXTRA_OECONF = "--enable-debuginfo \
    --enable-fast \
    --enable-shared  \
    --with-pm=gforker  \
    BASH_SHELL='${USRBINPATH}/env bash' \
    PERL='${USRBINPATH}/env perl' \
    --with-device=ch3:nemesis \
"

PACKAGECONFIG ??= ""

PACKAGECONFIG[cxx] = "--enable-cxx,--disable-cxx"
PACKAGECONFIG[fortran] = "--with-cross=${WORKDIR}/cross_values.txt --enable-fortran,--disable-f77 --disable-fortran,libgfortran"

# libmpi.so needs symbols like __multf3 and somehow it does not respect --rtlib option passed by clang
LDFLAGS:append:x86-64 = " -lgcc"
LDFLAGS:append:x86 = " -lgcc"

inherit autotools gettext pkgconfig qemu

DEPENDS += "qemu-native"

do_configure() {
    if [ "${@bb.utils.contains('PACKAGECONFIG', 'fortran', '1', '', d)}" = "1" ]; then
        qemu_binary="${@qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST'), [d.expand('${STAGING_DIR_HOST}${libdir}'),d.expand('${STAGING_DIR_HOST}${base_libdir}')])}"
        cat > ${WORKDIR}/qemuwrapper << EOF
#!/bin/sh
$qemu_binary "\$@"
EOF
        chmod +x ${WORKDIR}/qemuwrapper

        sed -i 's:my \(.*\) ./t`;:my \1 ${WORKDIR}/qemuwrapper ${WORKDIR}/t`;:' ${S}/maint/gen_cross.pl

        cd ${WORKDIR}
        perl ${S}/maint/gen_cross.pl

        sed -i 's:\(CROSS_F90_INTEGER_MODEL_MAP=.*\) }"$:\1 }, ":' ${WORKDIR}/cross_values.txt
    fi

    cd ${S}
    ./autogen.sh

    cd ${B}
    oe_runconf
    sed -i -e 's,${WORKDIR},,g' ${B}/src/include/mpichinfo.h
}

do_install:append() {
    sed -i 's,${S}/,,g' ${D}/${libdir}/libmpi.la
    sed -i 's,${DEBUG_PREFIX_MAP},,g' ${D}${bindir}/mpicxx
    sed -i 's,${DEBUG_PREFIX_MAP},,g' ${D}${bindir}/mpicc
    sed -i 's,${DEBUG_PREFIX_MAP},,g' ${D}/${libdir}/pkgconfig/mpich.pc
    sed -i 's,${RECIPE_SYSROOT},/,g' ${D}${bindir}/mpicc
    sed -i 's,${RECIPE_SYSROOT},/,g' ${D}${bindir}/mpicxx
}
