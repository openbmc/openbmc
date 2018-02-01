DESCRIPTION = "A library of C++ classes for flexible logging to files, syslog, IDSA and other destinations."
HOMEPAGE = "http://sourceforge.net/projects/log4cpp/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

RC = "rc5"
SRC_URI = "http://downloads.sourceforge.net/${BPN}/${BP}${RC}.tar.gz \
           file://fix-pc.patch;striplevel=2 \
          "
SRC_URI[md5sum] = "58b4591a2f3e7ef3d5e3e7cfb3a81a62"
SRC_URI[sha256sum] = "a611d99a20af6676c60219762771c0bfac90f4879bbde70038ece75338b588ec"

S = "${WORKDIR}/${BPN}"

inherit autotools pkgconfig

EXTRA_OECONF = "\
    --enable-doxygen=no \
    --enable-dot=no \
    --enable-html-docs=no \
    --enable-latex-docs=no \
"
