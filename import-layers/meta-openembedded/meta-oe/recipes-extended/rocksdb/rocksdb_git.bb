SUMMARY = "RocksDB an embeddable, persistent key-value store"
DESCRIPTION = "RocksDB is library that provides an embeddable, persistent key-value store for fast storage."
HOMEPAGE = "http://rocksdb.org/"
LICENSE = "(Apache-2.0 | GPL-2.0) & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.Apache;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.leveldb;md5=fb04ff57a14f308f2eed4a9b87d45837"

SRCREV = "266ac245affd69555643b735d19d3715c4eff91a"
PV = "5.8+git${SRCPV}"

SRC_URI = "git://github.com/facebook/${BPN}.git"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= "bzip2 zlib lz4"
PACKAGECONFIG[bzip2] = "-DWITH_BZ2=ON,-DWITH_BZ2=OFF,bzip2"
PACKAGECONFIG[lz4] = "-DWITH_LZ4=ON,-DWITH_LZ4=OFF,lz4"
PACKAGECONFIG[zlib] = "-DWITH_ZLIB=ON,-DWITH_ZLIB=OFF,zlib"
PACKAGECONFIG[lite] = "-DROCKSDB_LITE=ON,-DROCKSDB_LITE=OFF"

# Tools and tests currently don't compile on armv5 so we disable them
EXTRA_OECMAKE = "\
    -DPORTABLE=ON \
    -DWITH_TESTS=OFF \
    -DWITH_TOOLS=OFF \
"

FILES_${PN}-dev += "${libdir}/cmake"
