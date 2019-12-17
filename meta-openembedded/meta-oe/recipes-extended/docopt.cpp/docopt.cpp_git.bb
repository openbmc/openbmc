SUMMARY = "C++11 port of docopt command-line interface description language and parser"

DESCRIPTION = "docopt is library that lets you define a command line interface with the \
utility argument syntax that has been used by command line utilities for \
decades (formalized in POSIX.1-2017). From the description, docopt \
automatically generates a parser for the command line arguments."

HOMEPAGE = "https://github.com/docopt/docopt.cpp"

LICENSE = "MIT | BSL-1.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE-Boost-1.0;md5=e4224ccaecb14d942c71d31bef20d78c \
    file://LICENSE-MIT;md5=4b242fd9ef20207e18286d73da8a6677 \
"

DEPENDS = "boost"
SRCREV = "3dd23e3280f213bacefdf5fcb04857bf52e90917"
PV = "0.6.2+git${SRCPV}"

SRC_URI = "\
    git://github.com/docopt/docopt.cpp.git;protocol=https \
    file://0001-Set-library-VERSION-and-SOVERSION.patch \
"

S = "${WORKDIR}/git"

inherit cmake
