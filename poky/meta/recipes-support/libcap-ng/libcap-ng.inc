SUMMARY = "An alternate posix capabilities library"
DESCRIPTION = "The libcap-ng library is intended to make programming \
with POSIX capabilities much easier than the traditional libcap library."
HOMEPAGE = "http://freecode.com/projects/libcap-ng"
SECTION = "base"
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

SRC_URI = "http://people.redhat.com/sgrubb/libcap-ng/libcap-ng-${PV}.tar.gz \
           file://python.patch \
           file://0001-configure.ac-add-library-if-header-found.patch \
           file://0002-Wrap-pthread_atfork-usage-in-HAVE_PTHREAD_H.patch \
"

SRC_URI[md5sum] = "57dc267e2949cdecb651a929f9206572"
SRC_URI[sha256sum] = "a84ca7b4e0444283ed269b7a29f5b6187f647c82e2b876636b49b9a744f0ffbf"

BBCLASSEXTEND = "native nativesdk"
