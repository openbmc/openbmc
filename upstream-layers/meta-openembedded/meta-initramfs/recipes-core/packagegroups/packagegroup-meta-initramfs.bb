SUMMARY = "Meta-initramfs packagegroups"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-initramfs \
    packagegroup-meta-initramfs-devtools \
'

RDEPENDS:packagegroup-meta-initramfs = "\
    packagegroup-meta-initramfs-devtools \
"

RDEPENDS:packagegroup-meta-initramfs-devtools = "\
    dracut \
    ${@bb.utils.contains_any("TRANSLATED_TARGET_ARCH", "i586 x86-64", "grubby", "", d)} \
    "
