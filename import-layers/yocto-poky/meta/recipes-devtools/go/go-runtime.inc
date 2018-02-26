DEPENDS = "virtual/${TARGET_PREFIX}go go-native"
DEPENDS_class-nativesdk = "virtual/${TARGET_PREFIX}go-crosssdk"
PROVIDES = "virtual/${TARGET_PREFIX}go-runtime"

export GOHOSTOS = "${BUILD_GOOS}"
export GOHOSTARCH = "${BUILD_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOARCH = "${TARGET_GOARCH}"
export GOARM = "${TARGET_GOARM}"
export GO386 = "${TARGET_GO386}"
export GOROOT_BOOTSTRAP = "${STAGING_LIBDIR_NATIVE}/go"
export GOROOT_FINAL = "${libdir}/go"
export GO_TARGET_INSTALL = "std"
export CGO_ENABLED = "1"
export CC_FOR_TARGET="${CC}"
export CXX_FOR_TARGET="${CXX}"
export GOROOT_OVERRIDE = "1"

do_configure() {
	:
}

do_configure_libc-musl() {
	rm -f ${S}/src/runtime/race/*.syso
}

do_compile() {
	export GOBIN="${B}/bin"
	export CC="${@d.getVar('BUILD_CC').strip()}"
	rm -rf ${GOBIN} ${B}/pkg
	mkdir ${GOBIN}
	cd src
	./make.bash --host-only
	cp ${B}/pkg/tool/${BUILD_GOTUPLE}/go_bootstrap ${B}
	rm -rf ${B}/pkg/${TARGET_GOTUPLE}
	./make.bash --target-only
	if [ -n "${GO_DYNLINK}" ]; then
		cp ${B}/go_bootstrap ${B}/pkg/tool/${BUILD_GOTUPLE}
		GO_FLAGS="-buildmode=shared" GO_LDFLAGS="-extldflags \"${LDFLAGS}\"" ./make.bash --target-only
	fi
	cd ${B}
}

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
	rm -f ${D}${libdir}/go/src/cmd/dist/dist
}

# Remove test binaries that cannot be relocated
do_install_append_class-nativesdk() {
	rm -rf ${D}${libdir}/go/src/runtime/pprof/testdata
}

# These testdata directories aren't needed for builds and contain binaries
# that can cause errors in sysroot_strip(), so just remove them.
sysroot_stage_all_append() {
	find ${SYSROOT_DESTDIR}${libdir}/go/src -depth -type d -name 'testdata' -exec rm -rf {} \;
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
"
FILES_${PN}-staticdev = "${libdir}/go/pkg/${TARGET_GOTUPLE} \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*/*.a \
                   ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink/*/*/*/*/*/*/*.a \
"
# The testdata directories in the source tree include some binaries for various
# architectures, scripts, and .a files
INSANE_SKIP_${PN}-dev = "staticdev ldflags file-rdeps arch"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

BBCLASSEXTEND = "nativesdk"
