SUMMARY = "RCF (Remote Call Framework) is a cross-platform interprocess communication framework for C++"
DESCRIPTION = "Unlike other communication frameworks, RCF doesn't use a separate \
IDL (Interface Definition Language). RCF interfaces are defined directly in C++, \
and serialization for user-defined data types likewise is implemented in C++. \
Instead of a separate IDL compiler tool, RCF uses the C++ compiler to generate \
client and server stubs."
HOMEPAGE = "http://www.deltavsoft.com/"
SECTION = "libs"
PRIORITY = "optional"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://license.txt;md5=7586a312b9e978f9d6fac9a5780d1f84"

SRC_URI = "http://www.deltavsoft.com/downloads/RCF-${PV}.tar.gz \
           file://0001-Add-CMake-build-files.patch \
           file://aarch64-support.patch \
           file://0001-ClientStub.hpp-fix-a-clang-compiling-issue.patch \
           file://mips-support.patch \
           file://0001-Check-for-__powerpc__-define.patch \
           "

SRC_URI[md5sum] = "7ecb3c73f7eb66dba8790b659374f690"
SRC_URI[sha256sum] = "bbfcc88de502c39604878c395f516b03fff4eac63eb4f7f44c07d433839712dd"

S = "${WORKDIR}/RCF-${PV}"

inherit cmake dos2unix

PACKAGECONFIG ?= "zlib openssl sf-serialization boost-filesystem boost-asio protobuf json dll static shared demos"
PACKAGECONFIG[zlib] = "-DLIBRCF_USE_ZLIB=ON,-DLIBRCF_USE_ZLIB=OFF,zlib,zlib"
PACKAGECONFIG[zlib-static] = "-DLIBRCF_USE_ZLIB=ON -DLIBRCF_ZLIB_STATIC=ON,-DLIBRCF_ZLIB_STATIC=OFF,zlib,"
PACKAGECONFIG[openssl] = "-DLIBRCF_USE_OPENSSL=ON,-DLIBRCF_USE_OPENSSL=OFF,openssl,libssl libcrypto"
PACKAGECONFIG[openssl-static] = "-DLIBRCF_USE_OPENSSL=ON -DLIBRCF_OPENSSL_STATIC=ON,-DLIBRCF_OPENSSL_STATIC=OFF,openssl,"
PACKAGECONFIG[sf-serialization] = "-DLIBRCF_USE_SF_SERIALIZATION=ON,-DLIBRCF_USE_SF_SERIALIZATION=OFF,boost,"
PACKAGECONFIG[boost-serialization] = "-DLIBRCF_USE_BOOST_SERIALIZATION=ON,-DLIBRCF_USE_BOOST_SERIALIZATION=OFF,boost,"
PACKAGECONFIG[boost-filesystem] = "-DLIBRCF_USE_BOOST_FILESYSTEM=ON,-DLIBRCF_USE_BOOST_FILESYSTEM=OFF,boost,"
PACKAGECONFIG[boost-asio] = "-DLIBRCF_USE_BOOST_ASIO=ON,-DLIBRCF_USE_BOOST_ASIO=OFF,boost,"
PACKAGECONFIG[protobuf] = "-DLIBRCF_USE_PROTOBUF=ON,-DLIBRCF_USE_PROTOBUF=OFF,protobuf,protobuf"
PACKAGECONFIG[json] = "-DLIBRCF_USE_JSON=ON,-DLIBRCF_USE_JSON=OFF,json-spirit,json-spirit"
PACKAGECONFIG[ipv6] = "-DLIBRCF_USE_IPV6=ON,-DLIBRCF_USE_IPV6=OFF,"
PACKAGECONFIG[custom-allocator] = "-DLIBRCF_USE_CUSTOM_ALLOCATOR=ON,-DLIBRCF_USE_CUSTOM_ALLOCATOR=OFF,"
PACKAGECONFIG[dll] = "-DLIBRCF_BUILD_DLL=ON,-DLIBRCF_BUILD_DLL=OFF,"
PACKAGECONFIG[auto-init-deinit] = "-DLIBRCF_AUTO_INIT_DEINIT=ON,-DLIBRCF_AUTO_INIT_DEINIT=OFF,"
PACKAGECONFIG[static] = "-DLIBRCF_BUILD_STATIC_LIBS=ON,-DLIBRCF_BUILD_STATIC_LIBS=OFF,"
PACKAGECONFIG[shared] = "-DLIBRCF_BUILD_SHARED_LIBS=ON,-DLIBRCF_BUILD_SHARED_LIBS=OFF,"
PACKAGECONFIG[demos] = "-DLIBRCF_BUILD_DEMOS=ON,-DLIBRCF_BUILD_DEMOS=OFF,"

PACKAGES =+ "${@bb.utils.contains('PACKAGECONFIG', 'demos', '${PN}-demos', '', d)}"

FILES_${PN}-demos = "${bindir}/*"
FILES_${PN}-dev += "${datadir}/cmake/Modules/FindLibRcf.cmake"

BBCLASSEXTEND = "nativesdk"
