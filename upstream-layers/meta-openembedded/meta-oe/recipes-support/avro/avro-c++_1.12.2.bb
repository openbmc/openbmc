SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "https://avro.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34cb96edab958a981837bf6f44bf654d"

DEPENDS = "boost fmt"

SRC_URI = "git://github.com/apache/avro.git;branch=branch-1.12;protocol=https;name=avro-c++;tag=release-${PV} \
           "

SRCREV_avro-c++ = "8fa2067f70e3012cb3fd9a8839cd97e8c7cc1772"

S = "${UNPACKDIR}/${BP}/lang/c++"

inherit cmake pkgconfig

EXTRA_OECMAKE += "-DAVRO_BUILD_TESTS=OFF"

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
CXXFLAGS:append:toolchain-gcc = " -Wno-error=useless-cast -Wno-error=conversion -Wno-error=unused-but-set-variable"

BBCLASSEXTEND = "native nativesdk"
