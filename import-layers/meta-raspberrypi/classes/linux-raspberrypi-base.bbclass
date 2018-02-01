inherit linux-kernel-base

def get_dts(d, ver=None):
    import re

    staging_dir = d.getVar("STAGING_KERNEL_BUILDDIR")
    dts = d.getVar("KERNEL_DEVICETREE")

    # d.getVar() might return 'None' as a normal string
    # leading to 'is None' check isn't enough.
    # TODO: Investigate if this is a bug in bitbake
    if ver is None or ver == "None":
        ''' if 'ver' isn't set try to grab the kernel version
        from the kernel staging '''
        ver = get_kernelversion_file(staging_dir)

    return dts


def split_overlays(d, out, ver=None):
    dts = get_dts(d, ver)
    if out:
        overlays = oe.utils.str_filter_out('\S+\-overlay\.dtb$', dts, d)
        overlays = oe.utils.str_filter_out('\S+\.dtbo$', overlays, d)
    else:
        overlays = oe.utils.str_filter('\S+\-overlay\.dtb$', dts, d) + \
                   " " + oe.utils.str_filter('\S+\.dtbo$', dts, d)

    return overlays
