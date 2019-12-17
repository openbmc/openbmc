DESCRIPTION = "xorriso copies file objects from POSIX compliant filesystems \
into Rock Ridge enhanced ISO 9660 filesystems and allows session-wise \
manipulation of such filesystems"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://www.gnu.org/software/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "d6b16763a2ca23eec718cfac1761b40f"
SRC_URI[sha256sum] = "97a9c9831fa1b053f781f74a17b79327e7402c6163f5c7973453ba881616aeb4"

PACKAGECONFIG ??= "acl attr zlib bzip2 readline"
PACKAGECONFIG[acl] = "--enable-libacl,--disable-libacl,acl,"
PACKAGECONFIG[attr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib,"
PACKAGECONFIG[bzip2] = "--enable-libbz2,--disable-libbz2,bzip2,"
PACKAGECONFIG[readline] = "--enable-libreadline,--disable-libreadline,readline,"

inherit autotools-brokensep pkgconfig features_check

do_configure_prepend () {
    touch NEWS
}

RDEPENDS_${PN} = "tk"
REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
