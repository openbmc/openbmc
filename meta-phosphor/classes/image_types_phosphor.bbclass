# Base image class extension, inlined into every image.

def build_uboot(d):
    fstypes = d.getVar('IMAGE_FSTYPES', True).split()
    if any([x.endswith('u-boot') for x in fstypes]):
        return 'image_types_uboot'
    return ''


# Inherit u-boot classes if legacy uboot images are in use.
IMAGE_TYPE_uboot = '${@build_uboot(d)}'
inherit ${IMAGE_TYPE_uboot}

# Inherit the overlay class if overlay is in use.
IMAGE_TYPE_overlay = '${@bb.utils.contains("IMAGE_FSTYPES", "overlay", "image-overlay", "", d)}'
inherit ${IMAGE_TYPE_overlay}
