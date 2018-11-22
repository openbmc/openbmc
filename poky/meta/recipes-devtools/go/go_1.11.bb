require go-${PV}.inc
require go-target.inc

export GOBUILDMODE=""

# Add pie to GOBUILDMODE to satisfy "textrel" QA checking, but mips
# doesn't support -buildmode=pie, so skip the QA checking for mips and its
# variants.
python() {
    if 'mips' in d.getVar('TARGET_ARCH'):
        d.appendVar('INSANE_SKIP_%s' % d.getVar('PN'), " textrel")
    else:
        d.setVar('GOBUILDMODE', 'pie')
}
