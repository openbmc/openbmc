##################################################################
# Specific image creation and rootfs population info.
##################################################################

IMAGE_BASENAME ?= "${PN}"
IMAGE_VERSION_SUFFIX ?= "-${DATETIME}"
IMAGE_VERSION_SUFFIX[vardepsexclude] += "DATETIME SOURCE_DATE_EPOCH"
IMAGE_NAME ?= "${IMAGE_BASENAME}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
IMAGE_LINK_NAME ?= "${IMAGE_BASENAME}-${MACHINE}"

# IMAGE_NAME is the base name for everything produced when building images.
# The actual image that contains the rootfs has an additional suffix (.rootfs
# by default) followed by additional suffices which describe the format (.ext4,
# .ext4.xz, etc.).
IMAGE_NAME_SUFFIX ??= ".rootfs"

python () {
    if bb.data.inherits_class('deploy', d) and d.getVar("IMAGE_VERSION_SUFFIX") == "-${DATETIME}":
        import datetime
        d.setVar("IMAGE_VERSION_SUFFIX", "-" + datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).strftime('%Y%m%d%H%M%S'))
        d.setVarFlag("IMAGE_VERSION_SUFFIX", "vardepvalue", "")
}
