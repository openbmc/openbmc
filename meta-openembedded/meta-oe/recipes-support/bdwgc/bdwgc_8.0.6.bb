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
LIC_FILES_CHKSUM = "file://README.QUICK;md5=e3d68fff6b0c661d949d8df559fdc78f"

DEPENDS = "libatomic-ops"

SRCREV = "3e1477b72ef4329a196d67b74da4cbe274d04eff"
SRC_URI = "git://github.com/ivmai/bdwgc.git;branch=release-8_0;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "--enable-cpluscplus"

CFLAGS:append:libc-musl = " -D_GNU_SOURCE -DNO_GETCONTEXT -DSEARCH_FOR_DATA_START -DUSE_MMAP -DHAVE_DL_ITERATE_PHDR"

FILES:${PN}-doc = "${datadir}"

BBCLASSEXTEND = "native nativesdk"
