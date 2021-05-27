require go-${PV}.inc
require go-target.inc

export GOBUILDMODE=""
export CGO_ENABLED_riscv64 = ""
# Add pie to GOBUILDMODE to satisfy "textrel" QA checking, but
# windows/mips/riscv doesn't support -buildmode=pie, so skip the QA checking
# for windows/mips/riscv and their variants.
python() {
    if 'mips' in d.getVar('TARGET_ARCH',True) or 'riscv' in d.getVar('TARGET_ARCH',True) or 'windows' in d.getVar('TARGET_GOOS', True):
        d.appendVar('INSANE_SKIP_%s' % d.getVar('PN',True), " textrel")
    else:
        d.setVar('GOBUILDMODE', 'pie')
}
