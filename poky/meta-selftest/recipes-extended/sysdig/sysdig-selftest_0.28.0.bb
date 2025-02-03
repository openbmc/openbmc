SUMMARY = "A New System Troubleshooting Tool Built for the Way You Work"
DESCRIPTION = "Sysdig is open source, system-level exploration: capture \
system state and activity from a running Linux instance, then save, \
filter and analyze."
HOMEPAGE = "http://www.sysdig.org/"
LICENSE = "Apache-2.0 & (MIT | GPL-2.0-only)"
LIC_FILES_CHKSUM = "file://COPYING;md5=f8fee3d59797546cffab04f3b88b2d44"

inherit cmake pkgconfig

#OECMAKE_GENERATOR = "Unix Makefiles"
JIT ?= "jit"
JIT:mipsarchn32 = ""
JIT:mipsarchn64 = ""
JIT:riscv64 = ""
JIT:riscv32 = ""
JIT:powerpc = ""
JIT:powerpc64le = ""
JIT:powerpc64 = ""

#DEPENDS += "libb64 lua${JIT} zlib c-ares grpc-native grpc curl ncurses jsoncpp \
#            tbb jq openssl elfutils protobuf protobuf-native jq-native valijson"
RDEPENDS:${PN} = "bash"

SRC_URI = "git://github.com/draios/sysdig.git;branch=dev;protocol=https;name=sysdig \
           git://github.com/falcosecurity/libs;protocol=https;branch=master;name=falco;subdir=git/falcosecurity-libs \
           file://0055-Add-cstdint-for-uintXX_t-types.patch;patchdir=./falcosecurity-libs \
           file://0099-cmake-Pass-PROBE_NAME-via-CFLAGS.patch \
           "
SRCREV_sysdig = "4fb6288275f567f63515df0ff0a6518043ecfa9b"
SRCREV_falco = "caa0e4d0044fdaaebab086592a97f0c7f32aeaa9"

SRCREV_FORMAT = "sysdig_falco"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "\
                -DBUILD_DRIVER=OFF \
                -DMINIMAL_BUILD=ON \
                -DUSE_BUNDLED_DEPS=OFF \
                -DCREATE_TEST_TARGETS=OFF \
                -DDIR_ETC=${sysconfdir} \
                -DLUA_INCLUDE_DIR=${STAGING_INCDIR}/luajit-2.1 \
                -DFALCOSECURITY_LIBS_SOURCE_DIR=${S}/falcosecurity-libs \
                -DVALIJSON_INCLUDE=${STAGING_INCDIR}/valijson \
"

#CMAKE_VERBOSE = "VERBOSE=1"

FILES:${PN} += " \
    ${DIR_ETC}/* \
    ${datadir}/zsh/* \
    ${prefix}/src/*  \
"
# Use getaddrinfo_a is a GNU extension in libsinsp
# It should be fixed in sysdig, until then disable
# on musl
# Something like this https://code.videolan.org/ePirat/vlc/-/commit/01fd9fe4c7f6c5558f7345f38abf0152e17853ab  is needed to fix it
COMPATIBLE_HOST:libc-musl = "null"
COMPATIBLE_HOST:mips = "null"
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:powerpc64le = "null"

EXCLUDE_FROM_WORLD = "1"
