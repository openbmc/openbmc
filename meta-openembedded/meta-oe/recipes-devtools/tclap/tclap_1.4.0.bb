SUMMARY = "Templatized C++ Command Line Parser"
HOMEPAGE = "http://tclap.sourceforge.net/"
DESCRIPTION = "TCLAP is a small, flexible library that provides a simple interface \
for defining and accessing command line arguments. It was intially inspired by the \
user friendly CLAP libary. The difference is that this library is templatized, so \
the argument class is type independent. Type independence avoids identical-except-for-type \
objects, such as IntArg, FloatArg, and StringArg. While the library is not strictly \
compliant with the GNU or POSIX standards, it is close. \
"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5c64b2e8cc50917b2744a90950faa7cd"

SRCREV = "81b3d2a0c47895c22e9bb8c577f5ab521f76e5d2"
SRC_URI = "git://git.code.sf.net/p/tclap/code;branch=1.4"

S = "${WORKDIR}/git"
inherit cmake

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
