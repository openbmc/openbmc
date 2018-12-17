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

# for aarch64 ends with textrel in ${PN}
# http://errors.yoctoproject.org/Errors/Details/185634/
# ERROR: QA Issue: ELF binary '/work/aarch64-oe-linux/go/1.9.7-r0/packages-split/go/usr/lib/go/bin/go' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/go/1.9.7-r0/packages-split/go/usr/lib/go/pkg/tool/linux_arm64/trace' has relocations in .text
# ELF binary '/work/aarch64-oe-linux/go/1.9.7-r0/packages-split/go/usr/lib/go/pkg/tool/linux_arm64/pprof' has relocations in .text [textrel]
INSANE_SKIP_${PN} += "textrel"
