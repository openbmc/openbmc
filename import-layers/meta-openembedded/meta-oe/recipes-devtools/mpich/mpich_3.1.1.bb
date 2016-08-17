SUMMARY = "Message Passing Interface (MPI) implementation"
HOMEPAGE = "http://www.mpich.org/"
SECTION = "devel"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=2106f0435056f3dd9349747a766e5816"

SRC_URI = " \
	http://www.mpich.org/static/downloads/${PV}/mpich-${PV}.tar.gz \
"

SRC_URI[md5sum] = "40dc408b1e03cc36d80209baaa2d32b7"
SRC_URI[sha256sum] = "455ccfaf4ec724d2cf5d8bff1f3d26a958ad196121e7ea26504fd3018757652d"

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
                --disable-cxx"

inherit autotools-brokensep gettext

do_configure_prepend() {
    autoreconf --verbose --install --force -I . -I confdb/ -I maint/
    oe_runconf
    exit
}

# http://errors.yoctoproject.org/Errors/Details/35146/
PNBLACKLIST[mpich] ?= "BROKEN: QA Issue: libmpi.la failed sanity test (workdir), QA Issue: mpich.pc failed sanity test (tmpdir)"
