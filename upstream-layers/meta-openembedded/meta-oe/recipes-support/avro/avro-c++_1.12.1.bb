SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "https://avro.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34cb96edab958a981837bf6f44bf654d"

DEPENDS = "boost"

SRC_URI = "git://github.com/apache/avro.git;branch=branch-1.12;protocol=https;name=avro-c++;tag=release-${PV} \
           git://github.com/fmtlib/fmt.git;branch=10.x;protocol=https;name=fmt;destsuffix=_deps/fmt-src \
           "

SRCREV_FORMAT = "avro-c++_fmt"
SRCREV_avro-c++ = "9110c693767c1dde2665b2b57939333478b12036"
# Tag 10.2.1
SRCREV_fmt = "e69e5f977d458f2650bb346dadf2ad30c5320281"

S = "${UNPACKDIR}/${BP}/lang/c++"

inherit cmake pkgconfig

EXTRA_OECMAKE += "-DAVRO_BUILD_TESTS=OFF"
do_configure:prepend() {
        install -d ${B}/_deps
        cp -r ${UNPACKDIR}/_deps/fmt-src ${B}/_deps/
}

# Workaround for 32-bit build failures due to -Werror:
# 'useless-cast' in Symbol.cc and 'conversion' in FileStream.cc
#
# A patch has been submitted upstream: https://github.com/apache/avro/pull/3433
# Once this or an equivalent fix is merged and the recipe upgraded, this can be removed.
#
# These errors don't occur when using Clang accordingly to upstream configuration and
# the workaround flags aren't recognized by Clang either.
# To avoid unnecessary warnings and keep the build clean across toolchains, the flags are conditionally added
# using the toolchain-gcc override. This makes it unnecessary to override anything for Clang.
CXXFLAGS:append:toolchain-gcc = " -Wno-error=useless-cast -Wno-error=conversion"

BBCLASSEXTEND = "native nativesdk"
