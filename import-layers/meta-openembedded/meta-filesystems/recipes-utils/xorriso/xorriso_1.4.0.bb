DESCRIPTION = "xorriso copies file objects from POSIX compliant filesystems \
into Rock Ridge enhanced ISO 9660 filesystems and allows session-wise \
manipulation of such filesystems"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://www.gnu.org/software/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "ec79fd2736b8da76e7a870e27cadf6fa"
SRC_URI[sha256sum] = "0bd1e085015b28c24f57697d6def2fe84517967dc417554c0c3ccf1685ed0e56"

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
