FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://defconfig file://hwmon.cfg"
SRC_URI += " \
 file://0001-Fix-directory-hardlinks-from-deleted-directories.patch \
 file://0001-Revert-jffs2-Fix-lock-acquisition-order-bug-in-jffs2.patch \
 file://0002-jffs2-Fix-page-lock-f-sem-deadlock.patch \
 "
