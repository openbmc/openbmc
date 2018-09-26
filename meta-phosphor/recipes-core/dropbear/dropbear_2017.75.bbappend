# TODO: Dropbear 2018.76 controls options in a different way.  See
# https://github.com/openbmc/openbmc/issues/3186
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://options.patch"
