DESCRIPTION = "Tiny XML Library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
HOMEPAGE = "https://www.msweet.org/mxml/"
BUGTRACKER = "https://github.com/michaelrsweet/mxml/issues"

SRC_URI = "git://github.com/michaelrsweet/mxml.git;branch=master;protocol=https"
SRCREV = "804c4f4d922e4ca240ce2cad3ec7bc5d00c08691"
S = "${WORKDIR}/git"

# Package does not support out of tree builds.
inherit autotools-brokensep

PACKAGECONFIG ??= "threads"
PACKAGECONFIG[threads] = "--enable-threads,--disable-threads"

# MXML uses autotools but it explicitly states it does not support autoheader.
EXTRA_AUTORECONF = "--exclude=autopoint,autoheader"

do_configure:prepend() {
    # Respect optimization CFLAGS specified by OE.
    sed -e 's/-Os -g//' -i ${S}/configure.ac

    # Enable verbose compilation output. This is required for extra QA checks to work.
    sed -e '/.SILENT:/d' -i ${S}/Makefile.in
}

do_install() {
    # Package uses DSTROOT instread of standard DESTDIR to specify install location.
    oe_runmake install DSTROOT=${D}
}
