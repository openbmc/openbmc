SUMMARY = "Heap memory profiler for Linux"
DESCRIPTION = "Heaptrack traces all memory allocations and annotates these \
events with stack traces. Dedicated analysis tools then allow you to interpret \
the heap memory profile to find hotspots to reduce memory, leaks, allocation \
hotspots and temporary allocations"
HOMEPAGE = "https://phabricator.kde.org/source/heaptrack/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "zlib boost libunwind elfutils"

SRC_URI = "git://github.com/KDE/heaptrack.git;protocol=https;branch=master \
           file://0001-libheaptrack-Replace-__pid_t-with-pid_t.patch \
           file://0002-heaptrack_inject-Include-dlfcn.h-for-dlopen-dlclose.patch \
           file://0003-heaptrack_preload-Make-noexcept-attribute-conditiona.patch \
           file://0004-backtrace-Always-include-stdint.h.patch \
           file://0001-track-Check-for-unw_set_caching_policy-before-using.patch \
           "

SRCREV = "bc9e3744bcc47de978673d1e382f4125a1ab5fa8"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DHEAPTRACK_BUILD_GUI=OFF"

# libunwind is not yet ported to RISCV
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:riscv64 = "null"

BBCLASSEXTEND = "native"

# http://errors.yoctoproject.org/Errors/Details/766879/
#     buildResult:
#       variable: "LIBUNWIND_HAS_UNW_BACKTRACE"
#       cached: true
#       stdout: |
#         Change Dir: '/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build/CMakeFiles/CMakeScratch/TryCompile-kguYrO'
# 
#         Run Build Command(s): ninja -v cmTC_51d86
#         [1/2] ccache /OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot-native/usr/bin/aarch64-webos-linux/aarch64-webos-linux-gcc --sysroot=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot -DLIBUNWIND_HAS_UNW_BACKTRACE  -mcpu=cortex-a72+crc -mbranch-protection=standard -fstack-protector-strong  -O2 -D_FORTIFY_SOURCE=2 -Wformat -Wformat-security -Werror=format-security -Werror=return-type  --sysroot=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot  -O2 -pipe -g -feliminate-unused-debug-types -fcanon-prefix-map  -fmacro-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/git=/usr/src/debug/heaptrack/1.2.0  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/git=/usr/src/debug/heaptrack/1.2.0  -fmacro-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build=/usr/src/debug/heaptrack/1.2.0  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build=/usr/src/debug/heaptrack/1.2.0  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot=  -fmacro-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot=  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot-native= -o CMakeFiles/cmTC_51d86.dir/src.c.o -c /OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build/CMakeFiles/CMakeScratch/TryCompile-kguYrO/src.c
#         FAILED: CMakeFiles/cmTC_51d86.dir/src.c.o
#         ccache /OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot-native/usr/bin/aarch64-webos-linux/aarch64-webos-linux-gcc --sysroot=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot -DLIBUNWIND_HAS_UNW_BACKTRACE  -mcpu=cortex-a72+crc -mbranch-protection=standard -fstack-protector-strong  -O2 -D_FORTIFY_SOURCE=2 -Wformat -Wformat-security -Werror=format-security -Werror=return-type  --sysroot=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot  -O2 -pipe -g -feliminate-unused-debug-types -fcanon-prefix-map  -fmacro-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/git=/usr/src/debug/heaptrack/1.2.0  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/git=/usr/src/debug/heaptrack/1.2.0  -fmacro-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build=/usr/src/debug/heaptrack/1.2.0  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build=/usr/src/debug/heaptrack/1.2.0  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot=  -fmacro-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot=  -fdebug-prefix-map=/OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/recipe-sysroot-native= -o CMakeFiles/cmTC_51d86.dir/src.c.o -c /OE/lge/build/webos/styhead/BUILD/work/raspberrypi4_64-webos-linux/heaptrack/1.2.0/build/CMakeFiles/CMakeScratch/TryCompile-kguYrO/src.c
#         src.c: In function 'main':
#         src.c:3:43: error: passing argument 1 of 'unw_backtrace' from incompatible pointer type [-Wincompatible-pointer-types]
#             3 | int main() { void* buf[10]; unw_backtrace(&buf, 10); return 0; }
#               |                                           ^~~~
#               |                                           |
#               |                                           void * (*)[10]
#         In file included from ../../../../recipe-sysroot/usr/include/libunwind-aarch64.h:232,
#                          from ../../../../recipe-sysroot/usr/include/libunwind-64.h:7,
#                          from ../../../../recipe-sysroot/usr/include/libunwind.h:27,
#                          from src.c:2:
#         ../../../../recipe-sysroot/usr/include/libunwind-common.h:290:27: note: expected 'void **' but argument is of type 'void * (*)[10]'
#           290 | extern int unw_backtrace (void **, int);
#               |                           ^~~~~~~
#         ninja: build stopped: subcommand failed.
# 
#       exitCode: 1
# 
CFLAGS += "-Wno-error=incompatible-pointer-types"
