SUMMARY = "Message Passing Interface (MPI) implementation"
HOMEPAGE = "http://www.mpich.org/"
SECTION = "devel"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=2106f0435056f3dd9349747a766e5816"

SRC_URI = " \
    http://www.mpich.org/static/downloads/${PV}/mpich-${PV}.tar.gz \
"

SRC_URI[md5sum] = "f414cfa77099cd1fa1a5ae4e22db508a"
SRC_URI[sha256sum] = "0778679a6b693d7b7caff37ff9d2856dc2bfc51318bf8373859bfa74253da3dc"

CACHED_CONFIGUREVARS += "BASH_SHELL=${base_bindir}/bash"

RDEPENDS_${PN} += "bash perl libxml2"
S = "${WORKDIR}/${BP}"

EXTRA_OECONF = "--enable-debuginfo \
    --enable-fast \
    --enable-shared  \
    --with-pm=gforker  \
    --disable-rpath \
    --disable-f77 \
    --disable-fc \
    --disable-fortran \
    --disable-cxx \
"

inherit autotools-brokensep gettext

do_configure_prepend() {
    autoreconf --verbose --install --force -I . -I confdb/ -I maint/
    oe_runconf
    exit
}

do_install_append() {
    sed -i 's,${S}/,,g' ${D}/${libdir}/libmpi.la
    sed -i 's,${DEBUG_PREFIX_MAP},,g' ${D}/${libdir}/pkgconfig/mpich.pc
}
