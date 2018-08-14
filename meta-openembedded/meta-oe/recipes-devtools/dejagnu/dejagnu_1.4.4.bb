SUMMARY = "GNU unit testing framework, written in Expect and Tcl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"
SECTION = "devel"

inherit autotools

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://configure.patch"

SRC_URI[md5sum] = "053f18fd5d00873de365413cab17a666"
SRC_URI[sha256sum] = "d0fbedef20fb0843318d60551023631176b27ceb1e11de7468a971770d0e048d"

BBCLASSEXTEND = "native"
