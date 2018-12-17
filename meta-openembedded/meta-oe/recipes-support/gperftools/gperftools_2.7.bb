SUMMARY = "Fast, multi-threaded malloc() and nifty performance analysis tools"
HOMEPAGE = "https://github.com/gperftools/gperftools"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=762732742c73dc6c7fbe8632f06c059a"
DEPENDS += "libunwind"

SRCREV = "9608fa3bcf8020d35f59fbf70cd3cbe4b015b972"
SRC_URI = "git://github.com/gperftools/gperftools \
           file://0001-Support-Atomic-ops-on-clang.patch \
           file://0001-fix-build-with-musl-libc.patch \
           file://0001-disbale-heap-checkers-and-debug-allocator-on-musl.patch \
           file://disable_libunwind_aarch64.patch \
           file://sgidef.patch \
           "

inherit autotools

S = "${WORKDIR}/git"

# On mips, we have the following error.
#   do_page_fault(): sending SIGSEGV to ls for invalid read access from 00000008
#   Segmentation fault (core dumped)
COMPATIBLE_HOST_mipsarch_libc-glibc = "null"
# Disable thumb1
# {standard input}: Assembler messages:
# {standard input}:434: Error: lo register required -- `ldr pc,[sp]'
# Makefile:4538: recipe for target 'src/base/libtcmalloc_la-linuxthreads.lo' failed
ARM_INSTRUCTION_SET_armv5 = "arm"

