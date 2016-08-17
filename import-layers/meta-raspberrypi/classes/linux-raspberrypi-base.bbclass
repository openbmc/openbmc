inherit linux-kernel-base


def get_dts(d, ver):
    staging_dir = d.getVar("STAGING_KERNEL_BUILDDIR", True)
    dts = d.getVar("KERNEL_DEVICETREE", True)

    # d.getVar() might return 'None' as a normal string
    # leading to 'is None' check isn't enough.
    # TODO: Investigate if this is a bug in bitbake
    if ver is None or ver == "None":
        ''' if 'ver' isn't set try to grab the kernel version
        from the kernel staging '''
        ver = get_kernelversion_file(staging_dir)

    if ver is not None:
        min_ver = ver.split('.', 3)
    else:
        return dts

    # Always turn off device tree support for kernel's < 3.18
    try:
        if int(min_ver[0]) <= 3:
            if int(min_ver[1]) < 18:
                dts = ""
    except IndexError:
        min_ver = None

    return dts


def split_overlays(d, out):
    dts = get_dts(d, None)
    if out:
        overlays = oe.utils.str_filter_out('\S+\-overlay\.dtb$', dts, d)
    else:
        overlays = oe.utils.str_filter('\S+\-overlay\.dtb$', dts, d)

    return overlays
