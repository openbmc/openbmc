SUMMARY = "Fast, multi-threaded malloc() and nifty performance analysis tools"
HOMEPAGE = "https://github.com/gperftools/gperftools"
DESCRIPTION = "The gperftools, previously called google-perftools, package contains some \
utilities to improve and analyze the performance of C++ programs. \
Included are an optimized thread-caching malloc() and cpu and heap profiling utilities. \
"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=762732742c73dc6c7fbe8632f06c059a"

DEPENDS_append_libc-musl = " libucontext"

SRCREV = "f7c6fb6c8e99d6b1b725e5994373bcd19ffdf8fd"
SRC_URI = "git://github.com/gperftools/gperftools \
           file://0001-Support-Atomic-ops-on-clang.patch \
           file://0001-fix-build-with-musl-libc.patch \
           file://0001-disbale-heap-checkers-and-debug-allocator-on-musl.patch \
           file://disable_libunwind_aarch64.patch \
           file://sgidef.patch \
           "

SRC_URI_append_libc-musl = " file://ppc-musl.patch"

inherit autotools

S = "${WORKDIR}/git"

# On mips, we have the following error.
#   do_page_fault(): sending SIGSEGV to ls for invalid read access from 00000008
#   Segmentation fault (core dumped)
COMPATIBLE_HOST_mipsarch = "null"
COMPATIBLE_HOST_riscv64 = "null"
COMPATIBLE_HOST_riscv32 = "null"

# Disable thumb1
# {standard input}: Assembler messages:
# {standard input}:434: Error: lo register required -- `ldr pc,[sp]'
# Makefile:4538: recipe for target 'src/base/libtcmalloc_la-linuxthreads.lo' failed
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_toolchain-clang_arm = "arm"

EXTRA_OECONF_append_libc-musl_powerpc64le = " --disable-cpu-profiler --disable-heap-profiler --disable-heap-checker"
PACKAGECONFIG ?= "libunwind static"
PACKAGECONFIG_remove_arm_libc-musl = "libunwind"
PACKAGECONFIG_remove_riscv64 = "libunwind"
PACKAGECONFIG_remove_riscv32 = "libunwind"

PACKAGECONFIG[libunwind] = "--enable-libunwind,--disable-libunwind,libunwind"
PACKAGECONFIG[static] = "--enable-static,--disable-static,"

PACKAGE_BEFORE_PN += "libtcmalloc-minimal"
FILES_libtcmalloc-minimal = "${libdir}/libtcmalloc_minimal*${SOLIBS} ${libdir}/libtcmalloc_minimal_debug*${SOLIBS}"

# pprof tool requires Getopt::long and POSIX perl5 modules.
# Also runs `objdump` on each cpuprofile data file
RDEPENDS_${PN} += " \
    binutils \
    curl \
    perl-module-carp \
    perl-module-cwd \
    perl-module-getopt-long \
    perl-module-overloading \
    perl-module-posix \
"

RDEPENDS_${PN} += "libtcmalloc-minimal (= ${EXTENDPKGV})"
