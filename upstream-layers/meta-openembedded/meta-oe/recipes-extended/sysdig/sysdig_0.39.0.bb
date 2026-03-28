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

DEPENDS += "libb64 lua${JIT} zlib c-ares grpc-native grpc curl ncurses jsoncpp \
            tbb jq openssl elfutils protobuf protobuf-native jq-native valijson \
            uthash libbpf clang-native bpftool-native yaml-cpp nlohmann-json"
RDEPENDS:${PN} = "bash"

SRC_URI = "git://github.com/draios/sysdig.git;branch=dev;protocol=https;name=sysdig \
           git://github.com/falcosecurity/libs;protocol=https;branch=release/0.18.x;name=falco;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/falcosecurity-libs \
           git://github.com/falcosecurity/libs;protocol=https;branch=release/0.18.x;name=driver;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/driver \
           file://0001-cmake-Pass-PROBE_NAME-via-CFLAGS.patch \
           file://0001-Avoid-duplicate-operations-of-add_library.patch;patchdir=./falcosecurity-libs \
          "
SRCREV_sysdig = "6ef29110cf1add746e10ab5b38957e22730b7349"
SRCREV_falco = "e1999d079880d10800c57e004fca794a03cd060a"
SRCREV_driver = "d4efc80ece48174a71c1a420cb52d233fa94f946"

SRCREV_FORMAT = "sysdig_falco"

#Add this function to generate driver_config.h
do_configure:prepend() {
    mkdir -p ${WORKDIR}/driver_Make
    cd ${WORKDIR}/driver_Make
    cmake ${S}/driver -DMINIMAL_BUILD=ON -DCREATE_TEST_TARGETS=OFF
    cd -
}
do_configure[cleandirs] = "${WORKDIR}/driver_Make"

EXTRA_OECMAKE = "\
                -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
                -DBUILD_DRIVER=OFF \
                -DMINIMAL_BUILD=ON \
                -DUSE_BUNDLED_DEPS=OFF \
                -DCREATE_TEST_TARGETS=OFF \
                -DDIR_ETC=${sysconfdir} \
                -DLUA_INCLUDE_DIR=${STAGING_INCDIR}/luajit-2.1 \
                -DFALCOSECURITY_LIBS_SOURCE_DIR=${S}/falcosecurity-libs \
                -DDRIVER_SOURCE_DIR=${S}/driver \
                -DVALIJSON_INCLUDE=${STAGING_INCDIR}/valijson \
                -DUSE_BUNDLED_RE2=OFF \
                -DUSE_BUNDLED_TBB=OFF \
                -DUSE_BUNDLED_JSONCPP=OFF \
                -DBUILD_SYSDIG_MODERN_BPF=OFF \
                -DCREATE_TEST_TARGETS=OFF \
"

#Add include dir to find driver_config.h
CXXFLAGS:append = " -I${WORKDIR}/driver_Make/driver/src"
CFLAGS:append = " -I${WORKDIR}/driver_Make/driver/src"

#To fix do_package QA Issue
do_compile:append() {
    sed -i -e "s,${WORKDIR},,g" ${B}/libsinsp/libsinsp.pc
    sed -i -e "s,${WORKDIR},,g" ${B}/driver/libscap/libscap.pc
    mkdir -p ${B}/driver/libsinsp
    cp ${B}/libsinsp/libsinsp.pc ${B}/driver/libsinsp/
}
do_compile[cleandirs] = "${B}/driver/libsinsp"

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
