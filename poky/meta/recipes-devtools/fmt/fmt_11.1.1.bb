SUMMARY = "open-source formatting library for C++"
DESCRIPTION = "{fmt} is an open-source formatting library for C++. It can be used as a safe and fast alternative to (s)printf and iostreams."
HOMEPAGE = "https://fmt.dev"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b9257785fc4f3803a4b71b76c1412729"

SRC_URI = "git://github.com/fmtlib/fmt;branch=master;protocol=https \
           file://fix-gcc9.patch"
SRCREV = "e3ddede6c4ee818825c4e5a6dfa1d384860c27d9"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

BBCLASSEXTEND = "native nativesdk"
