require go-crosssdk.inc
require go-${PV}.inc

export CC_FOR_TARGET = "${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
export CXX_FOR_TARGET = "${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
export GO_INSTALL = "cmd"

do_compile_prepend() {
	export GOBIN="${B}/bin"
	export TMPDIR="$GOTMPDIR"
}
