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

SRC_URI = "http://www.hboehm.info/gc/gc_source/gc-${PV}.tar.gz \
           file://0001-fix-build-with-musl.patch \
          "

SRC_URI[md5sum] = "12c05fd2811d989341d8c6d81f66af87"
SRC_URI[sha256sum] = "63320ad7c45460e4a40e03f5aa4c6893783f21a16416c3282b994f933312afa2"
FILES_${PN}-doc = "/usr/share"

REAL_PV = "${@[d.getVar('PV',1)[:-1], d.getVar('PV',1)][(d.getVar('PV',1)[-1]).isdigit()]}"
S = "${WORKDIR}/gc-${REAL_PV}"

ARM_INSTRUCTION_SET = "arm"

inherit autotools pkgconfig

# by default use external libatomic-ops
PACKAGECONFIG ??= "libatomic-ops"
PACKAGECONFIG[libatomic-ops] = "--with-libatomic-ops=yes,--with-libatomic-ops=no,libatomic-ops"

BBCLASSEXTEND = "native nativesdk"
