SUMMARY = "Provides a small set of tools for development on the device"

PR = "r1"

inherit packagegroup

RPROVIDES_${PN} = "qemu-config"
RREPLACES_${PN} = "qemu-config"
RCONFLICTS_${PN} = "qemu-config"

RDEPENDS_${PN} = "\
    distcc-config \
    nfs-export-root \
    bash \
    binutils-symlinks \
    "
