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
LIC_FILES_CHKSUM = "file://README.QUICK;md5=728501f233050290314d33fb07d883eb"

SRCREV = "a46546f40d18e60c31077c2e7c8bb4e44bf1bef1"
SRC_URI = "git://github.com/ivmai/bdwgc.git;branch=release-7_6 \
          "

FILES_${PN}-doc = "${datadir}"

S = "${WORKDIR}/git"

ARM_INSTRUCTION_SET = "arm"

inherit autotools pkgconfig

# by default use external libatomic-ops
PACKAGECONFIG ??= "libatomic-ops"
PACKAGECONFIG[libatomic-ops] = "--with-libatomic-ops=yes,--with-libatomic-ops=no,libatomic-ops"

BBCLASSEXTEND = "native nativesdk"
