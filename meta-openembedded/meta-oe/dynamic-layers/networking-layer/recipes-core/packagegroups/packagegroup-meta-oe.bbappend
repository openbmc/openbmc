RDEPENDS_packagegroup-meta-oe-devtools += "\
    valijson \
"

RDEPENDS_packagegroup-meta-oe-support_append_x86 = "${@bb.utils.contains('BBFILE_COLLECTIONS', 'filesystems-layer', ' open-vm-tools', '', d)}"
RDEPENDS_packagegroup-meta-oe-support_append_x86-64 = "${@bb.utils.contains('BBFILE_COLLECTIONS', 'filesystems-layer', ' open-vm-tools', '', d)}"
