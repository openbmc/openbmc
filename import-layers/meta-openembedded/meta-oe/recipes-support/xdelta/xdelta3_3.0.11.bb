SUMMARY = "Xdelta is a tool for differential compression"
DESCRIPTION = "Open-source binary diff, differential compression tools, \
               VCDIFF (RFC 3284) delta compression."
HOMEPAGE = "http://xdelta.org/"
SECTION = "console/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"
SRC_URI = "https://github.com/jmacd/xdelta-devel/releases/download/v${PV}/${BPN}-${PV}.tar.gz  \
          "
SRC_URI[md5sum] = "445d8be2ac512113d5ca601ae8359626"
SRC_URI[sha256sum] = "0ccc9751ceaa4d90d6b06938a4deddb481816f5d9284bd07d2e728609cb300aa"

inherit autotools

# Optional secondary compression
PACKAGECONFIG ??= ""
PACKAGECONFIG[lzma] = "--with-liblzma,--without-liblzma,xz"
