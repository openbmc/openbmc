DESCRIPTION = "libsigrokdecode is a shared library written in C, which provides (streaming) protocol decoding functionality."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "glib-2.0 python3"

inherit autotools pkgconfig

SRC_URI = "http://sigrok.org/download/source/libsigrokdecode/libsigrokdecode-${PV}.tar.gz"

SRC_URI[md5sum] = "b9033bc7e68bc17fffffd4fdd793f5a1"
SRC_URI[sha256sum] = "e08d9e797c54eccf3144da631b6e5f1498ac531e51520428df537a1da82583f0"
