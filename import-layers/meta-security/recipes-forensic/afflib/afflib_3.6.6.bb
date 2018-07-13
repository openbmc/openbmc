SUMMARY = "The Advanced Forensic Format (AFF) is on-disk format for storing computer forensic information."
HOMEPAGE = "http://www.afflib.org/"
LICENSE = " BSD-4-Clause  & CPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1b2c6d0d6908f45d143ef6380727828"

DEPENDS = " zlib ncurses readline openssl libgcrypt"

SRC_URI = "http://archive.ubuntu.com/ubuntu/pool/universe/a/${BPN}/${BPN}_${PV}.orig.tar.gz;name=orig \
        http://archive.ubuntu.com/ubuntu/pool/universe/a/${BPN}/${BPN}_${PV}-1.1.diff.gz;name=dpatch \
        file://configure_rm_ms_flags.patch \
        "

SRC_URI[orig.md5sum] = "b7ff4d2945882018eb1536cad182ad01"
SRC_URI[orig.sha256sum] = "19cacfd558dc00e11975e820e3c4383b52aabbd5ca081d27bb7994a035d2f4ad"
SRC_URI[dpatch.md5sum] = "171e871024545b487589e6c85290576f"
SRC_URI[dpatch.sha256sum] = "db632e254ee51a1e4328cd4449d414eff4795053d4e36bfa8e0020fcb4085cdd"

inherit autotools-brokensep pkgconfig

CPPFLAGS = "-I${STAGING_INCDIR}"
LDFLAGS = "-L${STAGING_LIBDIR}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[curl] = "--with-curl=${STAGING_LIBDIR}, --without-curl, curl"
PACKAGECONFIG[expat] = "--with-expat=${STAGING_LIBDIR}, --without-expat, expat"
PACKAGECONFIG[fuse] = "--enable-fuse=yes, --enable-fuse=no, fuse"
PACKAGECONFIG[python] = "--enable-python=yes, --enable-python=no, python"

EXTRA_OECONF += "--enable-s3=no CPPFLAGS=-I${STAGING_INCDIR} LDFLAGS=-L${STAGING_LIBDIR}"
EXTRA_OEMAKE += "CPPFLAGS='${CPPFLAGS}' LDFLAGS='-L${STAGING_LIBDIR} -I${STAGING_INCDIR}'"
