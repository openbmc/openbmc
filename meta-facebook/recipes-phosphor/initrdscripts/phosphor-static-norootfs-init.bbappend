FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SOURCE_FILES:append = "\
    999-reduce-printk \
"

NOROOTFS_PERSISTENT_DIRS:append = " mnt/data"
