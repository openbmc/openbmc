DESCRIPTION = "A library of C++ classes for flexible logging to files, syslog, IDSA and other destinations."
HOMEPAGE = "http://sourceforge.net/projects/log4cpp/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "http://downloads.sourceforge.net/${BPN}/${BP}.tar.gz \
           file://fix-pc.patch;striplevel=2 \
          "
SRC_URI[sha256sum] = "a036bc6bd6044479e6c456de7edd042b060ea5c843e47beb75f59baea9b20e3a"

S = "${UNPACKDIR}/${BPN}"

inherit autotools pkgconfig

EXTRA_AUTORECONF += "-I m4"

EXTRA_OECONF = "\
    --enable-doxygen=no \
    --enable-dot=no \
    --enable-html-docs=no \
    --enable-latex-docs=no \
"

CXXFLAGS += "-std=c++14"

do_install:append() {
	sed -i -e 's|${DEBUG_PREFIX_MAP}||g; s|--sysroot=${STAGING_DIR_TARGET}||g' ${D}${bindir}/log4cpp-config
}
