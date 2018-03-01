FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI =+ "file://0001-4-byte-read-support-466.patch \
            file://0001-i2cget-Add-support-for-i2c-block-data.patch"
