FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

NOROOTFS_PERSISTENT_DIRS:append = " mnt/data"
