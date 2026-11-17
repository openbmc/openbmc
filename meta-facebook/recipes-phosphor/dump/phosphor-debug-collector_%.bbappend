FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:remove = "xz"
PACKAGECONFIG:append = " jffs-workaround dump-rotate-config zstd"
