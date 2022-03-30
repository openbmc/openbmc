DESCRIPTION = "libsigrokdecode is a shared library written in C, which provides (streaming) protocol decoding functionality."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "glib-2.0 python3"

inherit autotools pkgconfig

SRC_URI = "http://sigrok.org/download/source/libsigrokdecode/libsigrokdecode-${PV}.tar.gz \
           file://python39.patch \
           file://0001-configure.ac-add-py-3.10-support.patch \
           "

SRC_URI[md5sum] = "7ba4ed4ef1f06ae96979751e096c2821"
SRC_URI[sha256sum] = "c50814aa6743cd8c4e88c84a0cdd8889d883c3be122289be90c63d7d67883fc0"
