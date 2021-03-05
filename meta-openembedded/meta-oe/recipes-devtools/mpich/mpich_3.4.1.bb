SUMMARY = "Message Passing Interface (MPI) implementation"
HOMEPAGE = "http://www.mpich.org/"
SECTION = "devel"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=bd4d7ab13df98988b1ca2a4e01c8c163"

SRC_URI = "http://www.mpich.org/static/downloads/${PV}/mpich-${PV}.tar.gz"
SRC_URI[sha256sum] = "8836939804ef6d492bcee7d54abafd6477d2beca247157d92688654d13779727"

RDEPENDS_${PN} += "bash perl libxml2"

EXTRA_OECONF = "--enable-debuginfo \
    --enable-fast \
    --enable-shared  \
    --with-pm=gforker  \
    --disable-rpath \
    --disable-f77 \
    --disable-fc \
    --disable-fortran \
    --disable-cxx \
    BASH_SHELL='${USRBINPATH}/env bash' \
    PERL='${USRBINPATH}/env perl' \
    --with-device=ch3:nemesis \
    --with-rdmacm=no \
    --disable-numa \
"

PACKAGECONFIG += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
"
PACKAGECONFIG[x11] = "--with-x --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR},--without-x,virtual/libx11"

inherit autotools gettext pkgconfig

do_configure() {
    for d in confdb test/mpi test/mpi/confdb src/pm/hydra/confdb \
        src/pm/hydra/tools/topo/hwloc/hwloc/config src/pm/hydra/mpl/confdb \
        modules/yaksa/m4 modules/json-c modules/ucx test/mpi/dtpools/confdb \
        src/mpl/confdb src/mpi/romio/confdb;  do
        install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/$d
        install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/$d
    done
#    cd ${S}
#    autoupdate
#    autoreconf --verbose --install --force
#    cd ${B}
    oe_runconf
}

do_install_append() {
    sed -i 's,${S}/,,g' ${D}/${libdir}/libmpi.la
    sed -i 's,${DEBUG_PREFIX_MAP},,g' ${D}/${libdir}/pkgconfig/mpich.pc
}
