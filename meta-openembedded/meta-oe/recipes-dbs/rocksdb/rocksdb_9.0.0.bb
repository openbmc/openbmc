SUMMARY = "RocksDB an embeddable, persistent key-value store"
DESCRIPTION = "RocksDB is library that provides an embeddable, persistent key-value store for fast storage."
HOMEPAGE = "http://rocksdb.org/"
LICENSE = "(Apache-2.0 | GPL-2.0-only) & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.Apache;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.leveldb;md5=fb04ff57a14f308f2eed4a9b87d45837"

SRCREV = "f4441966592636253fd5ab0bb9ed44fc2697fc53"
SRCBRANCH = "9.0.fb"

SRC_URI = "git://github.com/facebook/${BPN}.git;branch=${SRCBRANCH};protocol=https \
           file://0001-cmake-Add-check-for-atomic-support.patch \
           file://0002-cmake-Use-exported-target-for-bz2.patch \
           file://0003-cmake-Do-not-add-msse4.2-mpclmul-on-clang.patch \
           file://0004-Implement-support-for-musl-ppc64.patch \
           file://0005-Implement-timer-implementation-for-mips-platform.patch \
           file://0006-Implement-timer-for-arm-v6.patch \
           file://0007-Fix-declaration-scope-of-LE_LOAD32-in-crc32c.patch \
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

CXXFLAGS += "${@bb.utils.contains('SELECTED_OPTIMIZATION', '-Og', '-DXXH_NO_INLINE_HINTS', '', d)}"

do_install:append() {
    # Fix for qa check buildpaths
    sed -i "s#${RECIPE_SYSROOT}##g" ${D}${libdir}/cmake/rocksdb/RocksDBTargets.cmake
}

# Need toku_time_now() implemented for ppc/musl
# see utilities/transactions/lock/range/range_tree/lib/portability/toku_time.h
COMPATIBLE_HOST:libc-musl:powerpc = "null"
