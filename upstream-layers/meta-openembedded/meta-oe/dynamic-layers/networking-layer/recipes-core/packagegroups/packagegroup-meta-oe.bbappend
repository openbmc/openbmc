RDEPENDS:packagegroup-meta-oe-support:append:x86 = "${@bb.utils.contains('BBFILE_COLLECTIONS', 'filesystems-layer', ' open-vm-tools', '', d)}"
RDEPENDS:packagegroup-meta-oe-support:append:x86-64 = "${@bb.utils.contains('BBFILE_COLLECTIONS', 'filesystems-layer', ' open-vm-tools', '', d)}"
