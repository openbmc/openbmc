require go-${PV}.inc
require go-runtime.inc

export GO_TARGET_INSTALL = "std"
export CC_FOR_TARGET = "${CC}"
export CXX_FOR_TARGET = "${CXX}"

do_compile() {
	export GOBIN="${B}/bin"
	export TMPDIR="$GOTMPDIR"
	export CC=$BUILD_CC

	cd src
	CGO_CFLAGS="${BUILD_CFLAGS}" CGO_LDFLAGS="${BUILD_LDFLAGS}" ./make.bash --host-only
	cp ${B}/pkg/tool/${BUILD_GOTUPLE}/go_bootstrap ${B}
	rm -rf ${B}/pkg/${TARGET_GOTUPLE}
	./make.bash --target-only
	if [ -n "${GO_DYNLINK}" ]; then
		cp ${B}/go_bootstrap ${B}/pkg/tool/${BUILD_GOTUPLE}
		GO_FLAGS="-buildmode=shared" GO_LDFLAGS="-extldflags \"${LDFLAGS}\"" ./make.bash --target-only
	fi
	cd ${B}
}
