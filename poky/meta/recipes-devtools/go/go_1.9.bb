require go-${PV}.inc
require go-target.inc

export GO_TARGET_INSTALL = "cmd"
export GO_FLAGS = "-a"
export CC_FOR_TARGET = "${CC}"
export CXX_FOR_TARGET = "${CXX}"

do_compile() {
	export GOBIN="${B}/bin"
	export TMPDIR="$GOTMPDIR"
	export CC=$BUILD_CC

	cd src
	./make.bash
	cd ${B}
}
