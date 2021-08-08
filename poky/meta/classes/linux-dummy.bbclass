
python __anonymous () {
    if d.getVar('PREFERRED_PROVIDER_virtual/kernel') == 'linux-dummy':
        # copy part codes from kernel.bbclass
        kname = d.getVar('KERNEL_PACKAGE_NAME') or "kernel"

        # set an empty package of kernel-devicetree
        d.appendVar('PACKAGES', ' %s-devicetree' % kname)
        d.setVar('ALLOW_EMPTY:%s-devicetree' % kname, '1')

        # Merge KERNEL_IMAGETYPE and KERNEL_ALT_IMAGETYPE into KERNEL_IMAGETYPES
        type = d.getVar('KERNEL_IMAGETYPE') or ""
        alttype = d.getVar('KERNEL_ALT_IMAGETYPE') or ""
        types = d.getVar('KERNEL_IMAGETYPES') or ""
        if type not in types.split():
            types = (type + ' ' + types).strip()
        if alttype not in types.split():
            types = (alttype + ' ' + types).strip()

        # set empty packages of kernel-image-*
        for type in types.split():
            typelower = type.lower()
            d.appendVar('PACKAGES', ' %s-image-%s' % (kname, typelower))
            d.setVar('ALLOW_EMPTY:%s-image-%s' % (kname, typelower), '1')
}

