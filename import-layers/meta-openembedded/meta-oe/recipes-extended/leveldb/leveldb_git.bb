SUMMARY = "LevelDB is a fast key-value storage library"
DESCRIPTION = "LevelDB is a fast key-value storage library that provides an ordered mapping from string keys to string values"
HOMEPAGE = "http://leveldb.googlecode.com"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92d1b128950b11ba8495b64938fc164d"

SRCREV = "803d69203a62faf50f1b77897310a3a1fcae712b"
PV = "1.18+git${SRCPV}"

SRC_URI = "git://github.com/google/${BPN}.git \
    file://0001-Explicitly-disable-tcmalloc.patch \
"

S = "${WORKDIR}/git"

do_compile() {
    # do not use oe_runmake. oe_runmake pass to make compilation arguments and override
    # leveldb makefile variable CFLAGS and broke leveldb build.
    CFLAGS="${CFLAGS}" make || die
}

do_install() {
    install -d ${D}${libdir}
    oe_libinstall -C ${S} -so libleveldb ${D}${libdir}
    install -d ${D}${includedir}/leveldb
    install -m 644 ${S}/include/leveldb/*.h ${D}${includedir}/leveldb/
}
