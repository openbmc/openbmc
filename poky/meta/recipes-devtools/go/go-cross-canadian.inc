inherit cross-canadian

DEPENDS = "go-native virtual/${HOST_PREFIX}go-crosssdk virtual/nativesdk-${HOST_PREFIX}go-runtime \
           virtual/${HOST_PREFIX}gcc-crosssdk virtual/nativesdk-${HOST_PREFIX}libc-for-gcc \
           virtual/nativesdk-${HOST_PREFIX}compilerlibs"
PN = "go-cross-canadian-${TRANSLATED_TARGET_ARCH}"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOTOOLDIR_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/${HOST_SYS}/go/pkg/tool/${BUILD_GOTUPLE}"
export GOROOT_FINAL = "${libdir}/go"
export CGO_CFLAGS = "${CFLAGS}"
export CGO_LDFLAGS = "${LDFLAGS}"
export GO_LDFLAGS = '-linkmode external -extld ${HOST_PREFIX}gcc -extldflags "--sysroot=${STAGING_DIR_HOST} ${SECURITY_NOPIE_CFLAGS} ${HOST_CC_ARCH} ${LDFLAGS}"'

do_configure[noexec] = "1"

do_compile() {
	export CC_FOR_${HOST_GOOS}_${HOST_GOARCH}="${HOST_PREFIX}gcc --sysroot=${STAGING_DIR_HOST}${SDKPATHNATIVE} ${SECURITY_NOPIE_CFLAGS}"
	export CXX_FOR_${HOST_GOOS}_${HOST_GOARCH}="${HOST_PREFIX}gxx --sysroot=${STAGING_DIR_HOST}${SDKPATHNATIVE} ${SECURITY_NOPIE_CFLAGS}"
	cd src
	./make.bash --host-only --no-banner
	cd ${B}
}
do_compile[dirs] =+ "${GOTMPDIR} ${B}/bin ${B}/pkg"
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin ${B}/pkg"


make_wrapper() {
	rm -f ${D}${bindir}/$2
	cat <<END >${D}${bindir}/$2
#!/bin/sh
here=\`dirname \$0\`
native_goroot=\`readlink -f \$here/../../lib/${TARGET_SYS}/go\`
export GOARCH="${TARGET_GOARCH}"
export GOOS="${TARGET_GOOS}"
test -n "\$GOARM" || export GOARM="${TARGET_GOARM}"
test -n "\$GO386" || export GO386="${TARGET_GO386}"
test -n "\$GOMIPS" || export GOMIPS="${TARGET_GOMIPS}"
export GOTOOLDIR="\$native_goroot/pkg/tool/${HOST_GOTUPLE}"
test -n "\$GOROOT" || export GOROOT="\$OECORE_TARGET_SYSROOT/${target_libdir}/go"
\$here/../../lib/${TARGET_SYS}/go/bin/$1 "\$@"
END
	chmod +x ${D}${bindir}/$2
}

do_install() {
	install -d ${D}${libdir}/go/pkg/tool
	cp --preserve=mode,timestamps -R ${B}/pkg/tool/${HOST_GOTUPLE} ${D}${libdir}/go/pkg/tool/
	install -d ${D}${bindir} ${D}${libdir}/go/bin
	for f in ${B}/${GO_BUILD_BINDIR}/*
	do
		base=`basename $f`
		install -m755 $f ${D}${libdir}/go/bin
		make_wrapper $base ${TARGET_PREFIX}$base
	done
}
