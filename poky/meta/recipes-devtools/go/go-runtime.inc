DEPENDS = "virtual/${TUNE_PKGARCH}-go go-native"
DEPENDS_class-nativesdk = "virtual/${TARGET_PREFIX}go-crosssdk"
PROVIDES = "virtual/${TARGET_PREFIX}go-runtime"

export CGO_CFLAGS = "${CFLAGS}"
export CGO_CPPFLAGS = "${CPPFLAGS}"
export CGO_CXXFLAGS = "${CXXFLAGS}"
export CGO_LDFLAGS = "${LDFLAGS}"
export GOCACHE = "${B}/.cache"

GO_EXTLDFLAGS ?= "${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} ${LDFLAGS}"
GO_SHLIB_LDFLAGS ?= '-ldflags="--linkmode=external -extldflags '${GO_EXTLDFLAGS}'"'

do_configure() {
	:
}

do_configure_libc-musl() {
	rm -f ${S}/src/runtime/race/*.syso
}

do_compile() {
	export CC_FOR_${TARGET_GOTUPLE}="${CC}"
	export CXX_FOR_${TARGET_GOTUPLE}="${CXX}"

	cd src
	./make.bash --target-only --no-banner std
	if [ -n "${GO_DYNLINK}" ]; then
		export GOTOOLDIR="${B}/pkg/tool/native_native"
		CC="$CC_FOR_${TARGET_GOTUPLE}" GOARCH="${TARGET_GOARCH}" GOOS="${TARGET_GOOS}" GOROOT=${B} \
			$GOTOOLDIR/go_bootstrap install -linkshared -buildmode=shared ${GO_SHLIB_LDFLAGS} std
	fi
	cd ${B}
}
do_compile[dirs] =+ "${GOTMPDIR} ${B}/bin ${B}/pkg"
do_compile[cleandirs] += "${GOTMPDIR} ${B}/bin ${B}/pkg"

do_install() {
	install -d ${D}${libdir}/go/src
	cp --preserve=mode,timestamps -R ${B}/pkg ${D}${libdir}/go/
	if [ "${BUILD_GOTUPLE}" != "${TARGET_GOTUPLE}" ]; then
		rm -rf ${D}${libdir}/go/pkg/${BUILD_GOTUPLE}
		rm -rf ${D}${libdir}/go/pkg/obj/${BUILD_GOTUPLE}
	fi
	rm -rf ${D}${libdir}/go/pkg/tool
	rm -rf ${D}${libdir}/go/pkg/obj
	rm -rf ${D}${libdir}/go/pkg/bootstrap
	find src -mindepth 1 -maxdepth 1 -type d | while read srcdir; do
		cp --preserve=mode,timestamps -R $srcdir ${D}${libdir}/go/src/
	done
	find ${D}${libdir}/go/src -depth -type d -name testdata -exec rm -rf {} \;
	rm -f ${D}${libdir}/go/src/cmd/dist/dist
        rm -f ${D}${libdir}/go/src/cmd/cgo/zdefaultcc.go
        rm -f ${D}${libdir}/go/src/cmd/go/internal/cfg/zdefaultcc.go

}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = "${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*${SOLIBSDEV}"
FILES_${PN}-dev = "${libdir}/go/src ${libdir}/go/pkg/include \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*/*/*.shlibname \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*/*/*.a \
"
FILES_${PN}-staticdev = "${libdir}/go/pkg/${TARGET_GOTUPLE}"

# Go sources include some scripts and pre-built binaries for
# multiple architectures.  The static .a files for dynamically-linked
# runtime are also required in -dev.
INSANE_SKIP_${PN}-dev = "staticdev file-rdeps arch"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

BBCLASSEXTEND = "nativesdk"
