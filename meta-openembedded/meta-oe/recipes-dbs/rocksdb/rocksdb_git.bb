SUMMARY = "RocksDB an embeddable, persistent key-value store"
DESCRIPTION = "RocksDB is library that provides an embeddable, persistent key-value store for fast storage."
HOMEPAGE = "http://rocksdb.org/"
LICENSE = "(Apache-2.0 | GPL-2.0) & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.Apache;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.leveldb;md5=fb04ff57a14f308f2eed4a9b87d45837"

SRCREV = "641fae60f63619ed5d0c9d9e4c4ea5a0ffa3e253"
SRCBRANCH = "5.18.fb"
PV = "5.18.3"

SRC_URI = "git://github.com/facebook/${BPN}.git;branch=${SRCBRANCH} \
           file://0001-Disable-Wshadow-and-do-not-mark-default-copy-constru.patch \
           file://0001-utilities-Fix-build-failure-with-Werror-maybe-uninit.patch \
          "

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= "bzip2 zlib lz4"
PACKAGECONFIG[bzip2] = "-DWITH_BZ2=ON -DBZIP2_LIBRARIES:STRING=bz2,-DWITH_BZ2=OFF,bzip2"
PACKAGECONFIG[lz4] = "-DWITH_LZ4=ON -DLZ4_LIBRARIES:STRING=lz4,-DWITH_LZ4=OFF,lz4"
PACKAGECONFIG[zlib] = "-DWITH_ZLIB=ON -DZLIB_LIBRARIES:STRING=z,-DWITH_ZLIB=OFF,zlib"
PACKAGECONFIG[lite] = "-DROCKSDB_LITE=ON,-DROCKSDB_LITE=OFF"

# Tools and tests currently don't compile on armv5 so we disable them
EXTRA_OECMAKE = "\
    -DPORTABLE=ON \
    -DWITH_TESTS=OFF \
    -DWITH_TOOLS=OFF \
"

do_install_append() {
    # fix for qa check buildpaths
    sed -i "s#${RECIPE_SYSROOT}##g" ${D}${libdir}/cmake/rocksdb/RocksDBTargets.cmake
}
