SUMMARY = "Fast, multi-threaded malloc() and nifty performance analysis tools"
HOMEPAGE = "https://github.com/gperftools/gperftools"
DESCRIPTION = "The gperftools, previously called google-perftools, package contains some \
utilities to improve and analyze the performance of C++ programs. \
Included are an optimized thread-caching malloc() and cpu and heap profiling utilities. \
"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=762732742c73dc6c7fbe8632f06c059a"

DEPENDS:append:libc-musl = " libucontext"

SRCREV = "365060c4213a48adb27f63d5dfad41b3dfbdd62e"
SRC_URI = "git://github.com/gperftools/gperftools;branch=master;protocol=https \
           file://0001-disbale-heap-checkers-and-debug-allocator-on-musl.patch \
           file://disable_libunwind_aarch64.patch \
           file://0001-src-mmap_hook.cc-Fix-build-for-32bit-machine.patch \
           "

SRC_URI:append:libc-musl = " file://ppc-musl.patch"

inherit autotools

S = "${WORKDIR}/git"

# On mips, we have the following error.
#   do_page_fault(): sending SIGSEGV to ls for invalid read access from 00000008
#   Segmentation fault (core dumped)
COMPATIBLE_HOST:mipsarch = "null"
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"

# Disable thumb1
# {standard input}: Assembler messages:
# {standard input}:434: Error: lo register required -- `ldr pc,[sp]'
# Makefile:4538: recipe for target 'src/base/libtcmalloc_la-linuxthreads.lo' failed
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:toolchain-clang:arm = "arm"

EXTRA_OECONF:append:libc-musl:powerpc64le = " --disable-cpu-profiler"
EXTRA_OECONF:append:libc-musl:powerpc = " --disable-cpu-profiler"
PACKAGECONFIG ?= "libunwind static"
PACKAGECONFIG:remove:arm:libc-musl = "libunwind"
PACKAGECONFIG:remove:riscv64 = "libunwind"
PACKAGECONFIG:remove:riscv32 = "libunwind"

PACKAGECONFIG[libunwind] = "--enable-libunwind,--disable-libunwind,libunwind"
PACKAGECONFIG[static] = "--enable-static,--disable-static,"

PACKAGE_BEFORE_PN += "libtcmalloc-minimal"
FILES:libtcmalloc-minimal = "${libdir}/libtcmalloc_minimal*${SOLIBS} ${libdir}/libtcmalloc_minimal_debug*${SOLIBS}"

# pprof tool requires Getopt::long and POSIX perl5 modules.
# Also runs `objdump` on each cpuprofile data file
RDEPENDS:${PN} += " \
    binutils \
    curl \
    perl-module-carp \
    perl-module-cwd \
    perl-module-getopt-long \
    perl-module-overloading \
    perl-module-posix \
"

RDEPENDS:${PN} += "libtcmalloc-minimal (= ${EXTENDPKGV})"
