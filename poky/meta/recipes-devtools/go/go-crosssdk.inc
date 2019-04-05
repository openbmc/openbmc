inherit crosssdk

DEPENDS = "go-native virtual/${TARGET_PREFIX}gcc-crosssdk virtual/nativesdk-${TARGET_PREFIX}compilerlibs virtual/${TARGET_PREFIX}binutils-crosssdk"
PN = "go-crosssdk-${SDK_SYS}"
PROVIDES = "virtual/${TARGET_PREFIX}go-crosssdk"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOROOT_FINAL = "${libdir}/go"

do_configure[noexec] = "1"

do_compile() {
	export CC_FOR_${TARGET_GOOS}_${TARGET_GOARCH}="${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}${SDKPATHNATIVE}"
	export CXX_FOR_${TARGET_GOOS}_${TARGET_GOARCH}="${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}${SDKPATHNATIVE}"
	cd src
	./make.bash --host-only --no-banner
	cd ${B}
}
do_compile[dirs] =+ "${GOTMPDIR} ${B}/bin ${B}/pkg"
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin ${B}/pkg"

make_wrapper() {
    rm -f ${D}${bindir}/$2
    cat <<END >${D}${bindir}/$2
#!/bin/bash
here=\`dirname \$0\`
export GOARCH="${TARGET_GOARCH}"
export GOOS="${TARGET_GOOS}"
\$here/../../lib/${CROSS_TARGET_SYS_DIR}/go/bin/$1 "\$@"
END
    chmod +x ${D}${bindir}/$2
}

do_install() {
	install -d ${D}${libdir}/go
	install -d ${D}${libdir}/go/bin
	install -d ${D}${libdir}/go/pkg/tool
	install -d ${D}${bindir}
	cp --preserve=mode,timestamps -R ${S}/pkg/tool/${BUILD_GOTUPLE} ${D}${libdir}/go/pkg/tool/
	for f in ${B}/bin/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go/bin
		make_wrapper $base ${TARGET_PREFIX}$base
	done
}
