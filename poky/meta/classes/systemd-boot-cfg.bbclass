SYSTEMD_BOOT_CFG ?= "${S}/loader.conf"
SYSTEMD_BOOT_ENTRIES ?= ""
SYSTEMD_BOOT_TIMEOUT ?= "10"

# Need UUID utility code.
inherit fs-uuid

python build_efi_cfg() {
    s = d.getVar("S")
    labels = d.getVar('LABELS')
    if not labels:
        bb.debug(1, "LABELS not defined, nothing to do")
        return

    if labels == []:
        bb.debug(1, "No labels, nothing to do")
        return

    cfile = d.getVar('SYSTEMD_BOOT_CFG')
    cdir = os.path.dirname(cfile)
    if not os.path.exists(cdir):
        os.makedirs(cdir)
    try:
         cfgfile = open(cfile, 'w')
    except OSError:
        bb.fatal('Unable to open %s' % cfile)

    cfgfile.write('# Automatically created by OE\n')
    cfgfile.write('default %s\n' % (labels.split()[0]))
    timeout = d.getVar('SYSTEMD_BOOT_TIMEOUT')
    if timeout:
        cfgfile.write('timeout %s\n' % timeout)
    else:
        cfgfile.write('timeout 10\n')
    cfgfile.close()

    for label in labels.split():
        localdata = d.createCopy()

        entryfile = "%s/%s.conf" % (s, label)
        if not os.path.exists(s):
            os.makedirs(s)
        d.appendVar("SYSTEMD_BOOT_ENTRIES", " " + entryfile)
        try:
            entrycfg = open(entryfile, "w")
        except OSError:
            bb.fatal('Unable to open %s' % entryfile)

        entrycfg.write('title %s\n' % label)

        kernel = localdata.getVar("KERNEL_IMAGETYPE")
        entrycfg.write('linux /%s\n' % kernel)

        append = localdata.getVar('APPEND')
        initrd = localdata.getVar('INITRD')

        if initrd:
            entrycfg.write('initrd /initrd\n')
        lb = label
        if label == "install":
            lb = "install-efi"
        entrycfg.write('options LABEL=%s ' % lb)
        if append:
            append = replace_rootfs_uuid(d, append)
            entrycfg.write('%s' % append)
        entrycfg.write('\n')
        entrycfg.close()
}
