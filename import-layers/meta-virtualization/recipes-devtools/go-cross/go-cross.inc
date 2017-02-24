inherit cross

# libgcc is required for the target specific libraries to build properly
DEPENDS += "go-native libgcc"

# Prevent runstrip from running because you get errors when the host arch != target arch
#INHIBIT_PACKAGE_STRIP = "1"
STRIP = "echo"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOARM = "${TARGET_GOARM}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOROOT_FINAL = "${libdir}/go"
export CGO_ENABLED = "1"
export CC_FOR_TARGET="${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
export CXX_FOR_TARGET="${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
CC = "${@d.getVar('BUILD_CC', True).strip()}"

do_configure[noexec] = "1"

do_compile() {
    export GOBIN="${B}/bin"    
    rm -rf ${GOBIN} ${B}/pkg
    mkdir ${GOBIN}

    export TMPDIR=${WORKDIR}/build-tmp
    mkdir -p ${WORKDIR}/build-tmp

    cd src
    ./make.bash --host-only
    # Ensure cgo.a is built with the target toolchain
    export GOBIN="${B}/target/bin"
    rm -rf ${GOBIN}
    mkdir -p ${GOBIN}
    GO_FLAGS="-a" ./make.bash
}

do_install() {
    install -d ${D}${libdir}/go
    cp -a ${B}/pkg ${D}${libdir}/go/
    install -d ${D}${libdir}/go/src
    (cd ${S}/src; for d in *; do \
        [ -d $d ] && cp -a ${S}/src/$d ${D}${libdir}/go/src/; \
    done)
    install -d ${D}${bindir}
    for f in ${B}/bin/*
    do
        install -m755 $f ${D}${bindir}
    done
}

do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"
