SUMMARY = "Unsupervised coverage-guided kernel fuzzer"
HOMEPAGE = "https://github.com/google/syzkaller"
BUGTRACKER = "https://github.com/google/syzkaller/issues"
SECTION = "devel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=5335066555b14d832335aa4660d6c376"
require ${BPN}-licenses.inc

DEPENDS:class-native += "qemu-system-native"

SRC_URI = "git://${GO_IMPORT};protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX};branch=master"
SRCREV = "22ec1469fe8c0ba256de07e8f97fa7b375b522bd"
require ${BPN}-go-mods.inc

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

inherit go-mod

GO_IMPORT = "github.com/google/syzkaller"

COMPATIBLE_HOST = "(x86_64|i.86|arm|aarch64).*-linux"
COMPATIBLE_HOST:libc-musl = "null"

B = "${S}/src/${GO_IMPORT}/bin"

GO_EXTRA_LDFLAGS += ' -X ${GO_IMPORT}/prog.GitRevision=${SRCREV}'

export GOHOSTFLAGS = "${GOBUILDFLAGS}"
export GOTARGETFLAGS = "${GOBUILDFLAGS}"
export TARGETOS = '${GOOS}'
export TARGETARCH = '${GOARCH}'
export TARGETVMARCH = '${GOARCH}'

CGO_ENABLED = "0"

# -buildmode=pie requires external (cgo) linking on ARM and x86
GOBUILDFLAGS:remove = "-buildmode=pie"

LDFLAGS:append:class-target = "${@bb.utils.contains_any("TC_CXX_RUNTIME", "llvm android", " -lc++", " -lstdc++", d)}"

DEPENDS:class-native += "qemu-system-native"

compile_native() {
    export HOSTOS="${GOHOSTOS}"
    export HOSTARCH="${GOHOSTARCH}"

    oe_runmake HOSTGO="${GO}" host
}

do_compile:class-native() {
    compile_native
}

do_compile:class-nativesdk() {
    compile_native
}

do_compile:class-target() {
    export HOSTOS="${GOOS}"
    export HOSTARCH="${GOARCH}"
    export SYZ_CC_${TARGETOS}_${TARGETARCH}="${CC}"
    export SYZ_CXX_${TARGETOS}_${TARGETARCH}="${CXX}"

    # Unset GOOS and GOARCH so that the correct syz-sysgen binary can be
    # generated. Fixes:
    # go install: cannot install cross-compiled binaries when GOBIN is set
    unset GOOS
    unset GOARCH

    oe_runmake GO="${GO}" REV=${SRCREV} target
}

install_native() {
    SYZ_BINS_NATIVE="syz-manager syz-repro syz-mutate syz-prog2c syz-db \
                     syz-upgrade"

    install -d ${D}${bindir}

    for i in ${SYZ_BINS_NATIVE}; do
        install -m 0755 ${B}/${i} ${D}${bindir}
    done
}

do_install:class-native() {
    install_native
}

do_install:class-nativesdk() {
    install_native
}

do_install:class-target() {
    SYZ_TARGET_DIR="${TARGETOS}_${TARGETARCH}"
    SYZ_BINS_TARGET="syz-execprog syz-executor"

    install -d ${D}${bindir}/${SYZ_TARGET_DIR}

    for i in ${SYZ_BINS_TARGET}; do
        install -m 0755 ${B}/${SYZ_TARGET_DIR}/${i} ${D}${bindir}/${SYZ_TARGET_DIR}
    done
}

BBCLASSEXTEND += "native nativesdk"
