# Copyright (C) 2014 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# gummiboot.bbclass - equivalent of grub-efi.bbclass
# Set EFI_PROVIDER = "gummiboot" to use gummiboot on your live images instead of grub-efi
# (images built by image-live.bbclass or image-vm.bbclass)

do_bootimg[depends] += "${MLPREFIX}gummiboot:do_deploy"
do_bootdirectdisk[depends] += "${MLPREFIX}gummiboot:do_deploy"

EFIDIR = "/EFI/BOOT"

GUMMIBOOT_CFG ?= "${S}/loader.conf"
GUMMIBOOT_ENTRIES ?= ""
GUMMIBOOT_TIMEOUT ?= "10"

# Need UUID utility code.
inherit fs-uuid

efi_populate() {
        DEST=$1

        EFI_IMAGE="gummibootia32.efi"
        DEST_EFI_IMAGE="bootia32.efi"
        if [ "${TARGET_ARCH}" = "x86_64" ]; then
            EFI_IMAGE="gummibootx64.efi"
            DEST_EFI_IMAGE="bootx64.efi"
        fi

        install -d ${DEST}${EFIDIR}
        # gummiboot requires these paths for configuration files
        # they are not customizable so no point in new vars
        install -d ${DEST}/loader
        install -d ${DEST}/loader/entries
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${EFI_IMAGE} ${DEST}${EFIDIR}/${DEST_EFI_IMAGE}
        EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
        printf 'fs0:%s\%s\n' "$EFIPATH" "$DEST_EFI_IMAGE" >${DEST}/startup.nsh
        install -m 0644 ${GUMMIBOOT_CFG} ${DEST}/loader/loader.conf
        for i in ${GUMMIBOOT_ENTRIES}; do
            install -m 0644 ${i} ${DEST}/loader/entries
        done
}

efi_iso_populate() {
        iso_dir=$1
        efi_populate $iso_dir
        mkdir -p ${EFIIMGDIR}/${EFIDIR}
        cp $iso_dir/${EFIDIR}/* ${EFIIMGDIR}${EFIDIR}
        cp $iso_dir/vmlinuz ${EFIIMGDIR}
        EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
        echo "fs0:${EFIPATH}\\${DEST_EFI_IMAGE}" > ${EFIIMGDIR}/startup.nsh
        if [ -f "$iso_dir/initrd" ] ; then
            cp $iso_dir/initrd ${EFIIMGDIR}
        fi
}

efi_hddimg_populate() {
        efi_populate $1
}

python build_efi_cfg() {
    s = d.getVar("S", True)
    labels = d.getVar('LABELS', True)
    if not labels:
        bb.debug(1, "LABELS not defined, nothing to do")
        return

    if labels == []:
        bb.debug(1, "No labels, nothing to do")
        return

    cfile = d.getVar('GUMMIBOOT_CFG', True)
    try:
         cfgfile = open(cfile, 'w')
    except OSError:
        bb.fatal('Unable to open %s' % cfile)

    cfgfile.write('# Automatically created by OE\n')
    cfgfile.write('default %s\n' % (labels.split()[0]))
    timeout = d.getVar('GUMMIBOOT_TIMEOUT', True)
    if timeout:
        cfgfile.write('timeout %s\n' % timeout)
    else:
        cfgfile.write('timeout 10\n')
    cfgfile.close()

    for label in labels.split():
        localdata = d.createCopy()

        overrides = localdata.getVar('OVERRIDES', True)
        if not overrides:
            bb.fatal('OVERRIDES not defined')

        entryfile = "%s/%s.conf" % (s, label)
        d.appendVar("GUMMIBOOT_ENTRIES", " " + entryfile)
        try:
            entrycfg = open(entryfile, "w")
        except OSError:
            bb.fatal('Unable to open %s' % entryfile)
        localdata.setVar('OVERRIDES', label + ':' + overrides)
        bb.data.update_data(localdata)

        entrycfg.write('title %s\n' % label)
        entrycfg.write('linux /vmlinuz\n')

        append = localdata.getVar('APPEND', True)
        initrd = localdata.getVar('INITRD', True)

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
