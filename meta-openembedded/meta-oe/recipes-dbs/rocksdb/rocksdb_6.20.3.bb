SUMMARY = "RocksDB an embeddable, persistent key-value store"
DESCRIPTION = "RocksDB is library that provides an embeddable, persistent key-value store for fast storage."
HOMEPAGE = "http://rocksdb.org/"
LICENSE = "(Apache-2.0 | GPL-2.0-only) & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.Apache;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.leveldb;md5=fb04ff57a14f308f2eed4a9b87d45837"

SRCREV = "8608d75d85f8e1b3b64b73a4fb6d19baec61ba5c"
SRCBRANCH = "6.20.fb"

SRC_URI = "git://github.com/facebook/${BPN}.git;branch=${SRCBRANCH};protocol=https \
           file://0001-cmake-Add-check-for-atomic-support.patch \
           file://0001-cmake-Use-exported-target-for-bz2.patch \
           file://0001-folly-Use-SYS_futex-for-syscall.patch \
           file://0001-jemalloc_helper-Limit-the-mm_malloc.h-hack-to-glibc-.patch \
           file://0001-range_tree-Implement-toku_time_now-for-rv32-rv64-in-.patch \
           file://ppc64.patch \
           file://mips.patch \
           file://arm.patch \
          "

SRC_URI:append:riscv32 = " file://0001-replace-old-sync-with-new-atomic-builtin-equivalents.patch"
SRC_URI:append:mips = " file://0001-replace-old-sync-with-new-atomic-builtin-equivalents.patch"
SRC_URI:append:powerpc = " file://0001-replace-old-sync-with-new-atomic-builtin-equivalents.patch"
SRC_URI:remove:toolchain-clang:riscv32 = "file://0001-replace-old-sync-with-new-atomic-builtin-equivalents.patch"

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

do_install:append() {
    # fix for qa check buildpaths
    sed -i "s#${RECIPE_SYSROOT}##g" ${D}${libdir}/cmake/rocksdb/RocksDBTargets.cmake
}

LDFLAGS:append:riscv64 = " -pthread"

# Need toku_time_now() implemented for ppc/musl
# see utilities/transactions/lock/range/range_tree/lib/portability/toku_time.h
COMPATIBLE_HOST:libc-musl:powerpc = "null"
