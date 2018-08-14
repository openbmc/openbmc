SUMMARY = "library with support for Expert Witness Compression Format"
LICENSE = "LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=58c39b26c0549f8e1bb4122173f474cd"

DEPENDS = "virtual/gettext libtool"

SRC_URI = "http://archive.ubuntu.com/ubuntu/pool/universe/libe/${BPN}/${BPN}_${PV}.orig.tar.gz;name=orig \
        file://gcc5_fix.patch \
        "
SRC_URI[orig.md5sum] = "fdf615f23937fad8e02b60b9e3e5fb35"
SRC_URI[orig.sha256sum] = "d14030ce6122727935fbd676d0876808da1e112721f3cb108564a4d9bf73da71"

inherit autotools-brokensep pkgconfig gettext

PACKAGECONFIG ??= "zlib ssl bz2"
PACKAGECONFIG[zlib] = "--with-zlib, --without-zlib, zlib, zlib"
PACKAGECONFIG[bz2] = "--with-bzip2, --without-bzip2, bzip2, bzip2"
PACKAGECONFIG[ssl] = "--with-openssl, --without-openssl, openssl, openssl"
PACKAGECONFIG[fuse] = "--with-libfuse, --without-libfuse, fuse"
PACKAGECONFIG[python] = "--enable-python, --disable-python, python"

EXTRA_OECONF += "--with-gnu-ld --disable-rpath"

RDEPENDS_${PN} += " util-linux-libuuid"
