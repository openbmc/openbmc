DESCRIPTION = "A library of C++ classes for flexible logging to files, syslog, IDSA and other destinations."
HOMEPAGE = "http://sourceforge.net/projects/log4cpp/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "http://downloads.sourceforge.net/${BPN}/${BP}.tar.gz \
           file://fix-pc.patch;striplevel=2 \
          "
SRC_URI[sha256sum] = "696113659e426540625274a8b251052cc04306d8ee5c42a0c7639f39ca90c9d6"

S = "${WORKDIR}/${BPN}"

inherit autotools pkgconfig

EXTRA_OECONF = "\
    --enable-doxygen=no \
    --enable-dot=no \
    --enable-html-docs=no \
    --enable-latex-docs=no \
"

CXXFLAGS += "-std=c++14"
