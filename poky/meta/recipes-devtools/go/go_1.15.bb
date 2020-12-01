require go-${PV}.inc
require go-target.inc

inherit linuxloader

export GOBUILDMODE=""
export CGO_ENABLED_riscv64 = ""
export GO_LDSO = "${@get_linuxloader(d)}"
export CC_FOR_TARGET = "gcc"
export CXX_FOR_TARGET = "g++"

# mips/rv64 doesn't support -buildmode=pie, so skip the QA checking for mips/riscv and its
# variants.
python() {
    if 'mips' in d.getVar('TARGET_ARCH',True) or 'riscv' in d.getVar('TARGET_ARCH',True):
        d.appendVar('INSANE_SKIP_%s' % d.getVar('PN',True), " textrel")
}

