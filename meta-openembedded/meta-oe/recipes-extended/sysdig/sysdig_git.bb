SUMMARY = "A New System Troubleshooting Tool Built for the Way You Work"
DESCRIPTION = "Sysdig is open source, system-level exploration: capture \
system state and activity from a running Linux instance, then save, \
filter and analyze."
HOMEPAGE = "http://www.sysdig.org/"
LICENSE = "Apache-2.0 & (MIT | GPL-2.0)"
LIC_FILES_CHKSUM = "file://COPYING;md5=f8fee3d59797546cffab04f3b88b2d44"

inherit cmake pkgconfig

#OECMAKE_GENERATOR = "Unix Makefiles"
JIT ?= "jit"
JIT_mipsarchn32 = ""
JIT_mipsarchn64 = ""
JIT_riscv64 = ""
JIT_riscv32 = ""

DEPENDS += "lua${JIT} zlib c-ares grpc-native grpc curl ncurses jsoncpp tbb jq openssl elfutils protobuf protobuf-native jq-native"
RDEPENDS_${PN} = "bash"

SRC_URI = "git://github.com/draios/sysdig.git;branch=dev \
           file://0001-fix-build-with-LuaJIT-2.1-betas.patch \
           file://0001-Fix-build-with-musl-backtrace-APIs-are-glibc-specifi.patch \
           file://fix-uint64-const.patch \
           file://aarch64.patch \
          "
SRCREV = "8daeef8da752c5f07f439391bc20c5948eb11470"
PV = "0.26.6"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "\
                -DBUILD_DRIVER=OFF \
                -DUSE_BUNDLED_DEPS=OFF \
                -DUSE_BUNDLED_B64=ON \
                -DCREATE_TEST_TARGETS=OFF \
                -DDIR_ETC=${sysconfdir} \
                -DLUA_INCLUDE_DIR=${STAGING_INCDIR}/luajit-2.1 \
                -DLUA_LIBRARY=libluajit-5.1.so \
"

FILES_${PN} += " \
    ${DIR_ETC}/* \
    ${datadir}/zsh/* \
    ${prefix}/src/*  \
"
# Use getaddrinfo_a is a GNU extension in libsinsp
# It should be fixed in sysdig, until then disable
# on musl
# Something like this https://code.videolan.org/ePirat/vlc/-/commit/01fd9fe4c7f6c5558f7345f38abf0152e17853ab  is needed to fix it
COMPATIBLE_HOST_libc-musl = "null"
COMPATIBLE_HOST_mips = "null"
COMPATIBLE_HOST_riscv64 = "null"
COMPATIBLE_HOST_riscv32 = "null"
