SUMMARY = "A garbage collector for C and C++"

DESCRIPTION = "The Boehm-Demers-Weiser conservative garbage collector can be\
 used as a garbage collecting replacement for C malloc or C++ new. It allows\
 you to allocate memory basically as you normally would, without explicitly\
 deallocating memory that is no longer useful. The collector automatically\
 recycles memory when it determines that it can no longer be otherwise\
 accessed.\
  The collector is also used by a number of programming language\
 implementations that either use C as intermediate code, want to facilitate\
 easier interoperation with C libraries, or just prefer the simple collector\
 interface.\
  Alternatively, the garbage collector may be used as a leak detector for C\
 or C++ programs, though that is not its primary goal.\
  Empirically, this collector works with most unmodified C programs, simply\
 by replacing malloc with GC_malloc calls, replacing realloc with GC_realloc\
 calls, and removing free calls."

HOMEPAGE = "http://www.hboehm.info/gc/"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.QUICK;md5=55f5088f90a982fed7af9a4897047ef7"

SRCREV = "e31cdc288ffbee5bf25f948e19deb5a7ce846971"
SRC_URI = "git://github.com/ivmai/bdwgc.git;branch=release-7_4 \
           file://0001-fix-build-with-musl.patch \
           file://0001-configure.ac-add-check-for-NO_GETCONTEXT-definition.patch \
           file://0002-Altera-NIOS2-support.patch \
          "

FILES_${PN}-doc = "${datadir}"

S = "${WORKDIR}/git"

ARM_INSTRUCTION_SET = "arm"

inherit autotools pkgconfig

# by default use external libatomic-ops
PACKAGECONFIG ??= "libatomic-ops"
PACKAGECONFIG[libatomic-ops] = "--with-libatomic-ops=yes,--with-libatomic-ops=no,libatomic-ops"

BBCLASSEXTEND = "native nativesdk"
