do_patch_append() {
    bb.build.exec_func('do_fix_pcie_reset', d)
}

do_fix_pcie_reset () {
        cd ${WORKDIR}/git/op-pwrctl
        patch -p2 < ${WORKDIR}/git/op-pwrctl/Firestone-Garrison-power-control-pcie-reset.patch
}
