FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

NOROOTFS_PERSISTENT_DIRS:append = " mnt/data"

# Bletchley currently uses /mnt/data as the emmc mount location.  We are in the
# process of moving this to /mnt/emmc but need to change some code in production
# before we can use /mnt/emmc.
NOROOTFS_PERSISTENT_DIRS:remove:bletchley = " mnt/data"
