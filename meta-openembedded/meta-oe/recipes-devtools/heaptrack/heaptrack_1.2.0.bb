SUMMARY = "Heap memory profiler for Linux"
DESCRIPTION = "Heaptrack traces all memory allocations and annotates these \
events with stack traces. Dedicated analysis tools then allow you to interpret \
the heap memory profile to find hotspots to reduce memory, leaks, allocation \
hotspots and temporary allocations"
HOMEPAGE = "https://phabricator.kde.org/source/heaptrack/"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "zlib boost libunwind elfutils"

SRC_URI = "git://github.com/KDE/heaptrack.git;protocol=https \
           file://0001-libheaptrack-Replace-__pid_t-with-pid_t.patch \
           file://0002-heaptrack_inject-Include-dlfcn.h-for-dlopen-dlclose.patch \
           file://0003-heaptrack_preload-Make-noexcept-attribute-conditiona.patch \
           file://0004-backtrace-Always-include-stdint.h.patch \
           "

SRCREV = "bc9e3744bcc47de978673d1e382f4125a1ab5fa8"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DHEAPTRACK_BUILD_GUI=OFF"

# libunwind is not yet ported to RISCV
COMPATIBLE_HOST_riscv32 = "null"
COMPATIBLE_HOST_riscv64 = "null"

BBCLASSEXTEND = "native nativesdk"
