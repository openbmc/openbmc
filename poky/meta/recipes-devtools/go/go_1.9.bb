require go-${PV}.inc
require go-target.inc

export GO_TARGET_INSTALL = "cmd"
export GO_FLAGS = "-a"
export CC_FOR_TARGET = "${CC}"
export CXX_FOR_TARGET = "${CXX}"
export GOBUILDMODE=""

do_compile() {
	export GOBIN="${B}/bin"
	export TMPDIR="$GOTMPDIR"
	export CC=$BUILD_CC

	cd src
	./make.bash
	cd ${B}
}

# Add pie to GOBUILDMODE to satisfy "textrel" QA checking, but mips
# doesn't support -buildmode=pie, so skip the QA checking for mips and its
# variants.
python() {
    if 'mips' in d.getVar('TARGET_ARCH'):
        d.appendVar('INSANE_SKIP_%s' % d.getVar('PN'), " textrel")
    else:
        d.setVar('GOBUILDMODE', 'pie')
}
