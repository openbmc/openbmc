# 0001-Only-load-dropbear-default-host-keys-if-a-key-is-not.patch
# has been upstreamed.  This patch can be removed once we upgrade
# to yocto 2.5 or later which will pull in the latest dropbear code.
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://dropbearkey.service \
            file://localoptions.h \
            file://dropbear.default \
           "
