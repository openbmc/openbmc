inherit cross

PROVIDES = "virtual/${TARGET_PREFIX}go"
DEPENDS += "go-native"

PN = "go-cross-${TARGET_ARCH}"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOARM = "${TARGET_GOARM}"
export GO386 = "${TARGET_GO386}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOROOT_FINAL = "${libdir}/go"
export CGO_ENABLED = "1"
export CC_FOR_TARGET="${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
export CXX_FOR_TARGET="${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
CC = "${@d.getVar('BUILD_CC').strip()}"

do_configure[noexec] = "1"

do_compile() {
    export GOBIN="${B}/bin"
    rm -rf ${GOBIN} ${B}/pkg
    mkdir ${GOBIN}
    cd src
    ./make.bash --host-only
    cd ${B}
}


make_wrapper() {
    rm -f ${D}${bindir}/$2
    cat <<END >${D}${bindir}/$2
#!/bin/bash
here=\`dirname \$0\`
export GOARCH="${TARGET_GOARCH}"
export GOOS="${TARGET_GOOS}"
export GOARM="\${GOARM:-${TARGET_GOARM}}"
export GO386="\${GO386:-${TARGET_GO386}}"
\$here/../../lib/${CROSS_TARGET_SYS_DIR}/go/bin/$1 "\$@"
END
    chmod +x ${D}${bindir}/$2
}

do_install() {
    install -d ${D}${libdir}/go
    cp --preserve=mode,timestamps -R ${B}/pkg ${D}${libdir}/go/
    install -d ${D}${libdir}/go/src
    (cd ${S}/src; for d in *; do \
        [ ! -d $d ] || cp --preserve=mode,timestamps -R ${S}/src/$d ${D}${libdir}/go/src/; \
    done)
    rm -rf ${D}${libdir}/go/src/runtime/pprof/testdata
    install -d ${D}${bindir} ${D}${libdir}/go/bin
    for f in ${B}/bin/*
    do
        base=`basename $f`
        install -m755 $f ${D}${libdir}/go/bin
        make_wrapper $base ${TARGET_PREFIX}$base
    done
}
