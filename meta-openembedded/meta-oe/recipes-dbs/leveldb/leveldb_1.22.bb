SUMMARY = "LevelDB is a fast key-value storage library"
DESCRIPTION = "LevelDB is a fast key-value storage library that provides an ordered mapping from string keys to string values"
HOMEPAGE = "https://github.com/google/leveldb"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92d1b128950b11ba8495b64938fc164d"

SRC_URI = "git://github.com/google/${BPN}.git;branch=master;protocol=https \
           file://run-ptest"

SRCREV = "78b39d68c15ba020c0d60a3906fb66dbf1697595"
S = "${WORKDIR}/git"

inherit cmake ptest

PACKAGECONFIG ??= ""
PACKAGECONFIG[benchmarks] = "-DLEVELDB_BUILD_BENCHMARKS=ON,-DLEVELDB_BUILD_BENCHMARKS=OFF,sqlite"
PACKAGECONFIG[snappy] = ",,snappy"
PACKAGECONFIG[tcmalloc] = ",,gperftools"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=RelWithDebInfo -DBUILD_SHARED_LIBS=ON -DCMAKE_SKIP_RPATH=ON \
                 -DLEVELDB_BUILD_TESTS=${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'ON', 'OFF', d)}"

do_install_append() {
    install -D -m 0755 ${B}/leveldbutil ${D}${bindir}/leveldbutil
}

do_install_ptest() {
    install -m 0755 ${B}/*_test ${D}${PTEST_PATH}
}

# Do not try to build lib32-leveldb for mips64, but allow libn32-leveldb.
#
COMPATIBLE_HOST_mipsarcho32_pn-lib32-leveldb = "null"
