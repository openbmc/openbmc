# grub-efi.bbclass
# Copyright (c) 2011, Intel Corporation.
#
# SPDX-License-Identifier: MIT

# Provide grub-efi specific functions for building bootable images.

# External variables
# ${INITRD} - indicates a list of filesystem images to concatenate and use as an initrd (optional)
# ${ROOTFS} - indicates a filesystem image to include as the root filesystem (optional)
# ${GRUB_GFXSERIAL} - set this to 1 to have graphics and serial in the boot menu
# ${LABELS} - a list of targets for the automatic config
# ${APPEND} - an override list of append strings for each label
# ${GRUB_OPTS} - additional options to add to the config, ';' delimited # (optional)
# ${GRUB_TIMEOUT} - timeout before executing the deault label (optional)
# ${GRUB_ROOT} - grub's root device.

GRUB_SERIAL ?= "console=ttyS0,115200"
GRUB_CFG_VM = "${S}/grub_vm.cfg"
GRUB_CFG_LIVE = "${S}/grub_live.cfg"
GRUB_TIMEOUT ?= "10"
#FIXME: build this from the machine config
GRUB_OPTS ?= "serial --unit=0 --speed=115200 --word=8 --parity=no --stop=1"

GRUB_ROOT ?= "${ROOT}"
APPEND ?= ""

# Uses MACHINE specific KERNEL_IMAGETYPE
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Need UUID utility code.
inherit fs-uuid

python build_efi_cfg() {
    import sys

    workdir = d.getVar('WORKDIR')
    if not workdir:
        bb.error("WORKDIR not defined, unable to package")
        return

    gfxserial = d.getVar('GRUB_GFXSERIAL') or ""

    labels = d.getVar('LABELS')
    if not labels:
        bb.debug(1, "LABELS not defined, nothing to do")
        return

    if labels == []:
        bb.debug(1, "No labels, nothing to do")
        return

    cfile = d.getVar('GRUB_CFG')
    if not cfile:
        bb.fatal('Unable to read GRUB_CFG')

    try:
         cfgfile = open(cfile, 'w')
    except OSError:
        bb.fatal('Unable to open %s' % cfile)

    cfgfile.write('# Automatically created by OE\n')

    opts = d.getVar('GRUB_OPTS')
    if opts:
        for opt in opts.split(';'):
            cfgfile.write('%s\n' % opt)

    cfgfile.write('default=%s\n' % (labels.split()[0]))

    timeout = d.getVar('GRUB_TIMEOUT')
    if timeout:
        cfgfile.write('timeout=%s\n' % timeout)
    else:
        cfgfile.write('timeout=50\n')

    root = d.getVar('GRUB_ROOT')
    if not root:
        bb.fatal('GRUB_ROOT not defined')

    if gfxserial == "1":
        btypes = [ [ " graphics console", "" ],
            [ " serial console", d.getVar('GRUB_SERIAL') or "" ] ]
    else:
        btypes = [ [ "", "" ] ]

    for label in labels.split():
        localdata = d.createCopy()

        overrides = localdata.getVar('OVERRIDES')
        if not overrides:
            bb.fatal('OVERRIDES not defined')

        localdata.setVar('OVERRIDES', 'grub_' + label + ':' + overrides)

        for btype in btypes:
            cfgfile.write('\nmenuentry \'%s%s\'{\n' % (label, btype[0]))
            lb = label
            if label == "install":
                lb = "install-efi"
            kernel = localdata.getVar('KERNEL_IMAGETYPE')
            cfgfile.write('linux /%s LABEL=%s' % (kernel, lb))

            cfgfile.write(' %s' % replace_rootfs_uuid(d, root))

            append = localdata.getVar('APPEND')
            initrd = localdata.getVar('INITRD')

            if append:
                append = replace_rootfs_uuid(d, append)
                cfgfile.write(' %s' % (append))

            cfgfile.write(' %s' % btype[1])
            cfgfile.write('\n')

            if initrd:
                cfgfile.write('initrd /initrd')
            cfgfile.write('\n}\n')

    cfgfile.close()
}
build_efi_cfg[vardepsexclude] += "OVERRIDES"
