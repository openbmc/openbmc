require go-${PV}.inc
require go-target.inc

inherit linuxloader

export GOBUILDMODE=""
export GO_LDSO = "${@get_linuxloader(d)}"
export CC_FOR_TARGET = "gcc"
export CXX_FOR_TARGET = "g++"

# mips/rv64 doesn't support -buildmode=pie, so skip the QA checking for mips/riscv32 and its
# variants.
python() {
    if 'mips' in d.getVar('TARGET_ARCH') or 'riscv32' in d.getVar('TARGET_ARCH'):
        d.appendVar('INSANE_SKIP:%s' % d.getVar('PN'), " textrel")
}

