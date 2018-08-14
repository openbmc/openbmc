python __anonymous () {

    machine_kernel_pr = d.getVar('MACHINE_KERNEL_PR')

    if machine_kernel_pr:
        d.setVar('PR', machine_kernel_pr)
}

