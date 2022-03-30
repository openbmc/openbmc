DESCRIPTION = "xorriso copies file objects from POSIX compliant filesystems \
into Rock Ridge enhanced ISO 9660 filesystems and allows session-wise \
manipulation of such filesystems"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://www.gnu.org/software/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "a5e3dc4e0b92be6837f9ed6cbf7f9df5"
SRC_URI[sha256sum] = "91663d61bee61ff790b3f6861fdf57c54af0e238ab14f297b55bae8ab5d17684"

PACKAGECONFIG ??= "acl attr zlib bzip2 readline"
PACKAGECONFIG[acl] = "--enable-libacl,--disable-libacl,acl,"
PACKAGECONFIG[attr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib,"
PACKAGECONFIG[bzip2] = "--enable-libbz2,--disable-libbz2,bzip2,"
PACKAGECONFIG[readline] = "--enable-libreadline,--disable-libreadline,readline,"

inherit autotools-brokensep pkgconfig features_check

do_configure:prepend () {
    touch NEWS
}

RDEPENDS:${PN} = "tk"
REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
