SUMMARY = "RocksDB an embeddable, persistent key-value store"
DESCRIPTION = "RocksDB is library that provides an embeddable, persistent key-value store for fast storage."
HOMEPAGE = "http://rocksdb.org/"
LICENSE = "(Apache-2.0 | GPL-2.0) & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.Apache;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.leveldb;md5=fb04ff57a14f308f2eed4a9b87d45837"

SRCREV = "f3e33549c151f30ac4eb7c22356c6d0331f37652"
SRCBRANCH = "6.12.fb"
PV = "6.12.7"

SRC_URI = "git://github.com/facebook/${BPN}.git;branch=${SRCBRANCH} \
           file://0001-cmake-Add-check-for-atomic-support.patch \
           file://0001-cmake-Use-exported-target-for-bz2.patch \
           file://0001-folly-Use-SYS_futex-for-syscall.patch \
          "

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= "bzip2 zlib lz4 gflags"
PACKAGECONFIG[bzip2] = "-DWITH_BZ2=ON,-DWITH_BZ2=OFF,bzip2"
PACKAGECONFIG[lz4] = "-DWITH_LZ4=ON,-DWITH_LZ4=OFF,lz4"
PACKAGECONFIG[zlib] = "-DWITH_ZLIB=ON,-DWITH_ZLIB=OFF,zlib"
PACKAGECONFIG[zstd] = "-DWITH_ZSTD=ON,-DWITH_ZSTD=OFF,zstd"
PACKAGECONFIG[lite] = "-DROCKSDB_LITE=ON,-DROCKSDB_LITE=OFF"
PACKAGECONFIG[gflags] = "-DWITH_GFLAGS=ON,-DWITH_GFLAGS=OFF,gflags"

# Tools and tests currently don't compile on armv5 so we disable them
EXTRA_OECMAKE = "\
    -DPORTABLE=ON \
    -DWITH_TESTS=OFF \
    -DWITH_BENCHMARK_TOOLS=OFF \
    -DWITH_TOOLS=OFF \
    -DFAIL_ON_WARNINGS=OFF \
"

do_install_append() {
    # fix for qa check buildpaths
    sed -i "s#${RECIPE_SYSROOT}##g" ${D}${libdir}/cmake/rocksdb/RocksDBTargets.cmake
}

LDFLAGS_append_riscv64 = " -pthread"
