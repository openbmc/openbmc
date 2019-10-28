DESCRIPTION = "xorriso copies file objects from POSIX compliant filesystems \
into Rock Ridge enhanced ISO 9660 filesystems and allows session-wise \
manipulation of such filesystems"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://www.gnu.org/software/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "e5fbae9ada52730fbe248ab9a88e7127"
SRC_URI[sha256sum] = "a93fa7ae5bb1902198cddfec25201388156932f36f2f5da829bf4fcae9a6062b"

PACKAGECONFIG ??= "acl attr zlib bzip2 readline"
PACKAGECONFIG[acl] = "--enable-libacl,--disable-libacl,acl,"
PACKAGECONFIG[attr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib,"
PACKAGECONFIG[bzip2] = "--enable-libbz2,--disable-libbz2,bzip2,"
PACKAGECONFIG[readline] = "--enable-libreadline,--disable-libreadline,readline,"

inherit autotools-brokensep pkgconfig distro_features_check

do_configure_prepend () {
    touch NEWS
}

RDEPENDS_${PN} = "tk"
REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
