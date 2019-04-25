DESCRIPTION = "sigrok-cli is a command-line frontend for sigrok."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsigrok"

PACKAGECONFIG[decode] = "--with-libsigrokdecode,--without-libsigrokdecode,libsigrokdecode"

PACKAGECONFIG ??= "decode"

inherit autotools pkgconfig

SRC_URI = "http://sigrok.org/download/source/sigrok-cli/sigrok-cli-${PV}.tar.gz"

SRC_URI[md5sum] = "77cb745e2fa239c7bd1ea81e2d67ede9"
SRC_URI[sha256sum] = "5669d968c2de3dfc6adfda76e83789b6ba76368407c832438cef5e7099a65e1c"
