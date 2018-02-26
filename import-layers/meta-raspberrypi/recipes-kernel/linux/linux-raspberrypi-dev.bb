python __anonymous() {
    if "linux-raspberrypi-dev" not in d.getVar("PREFERRED_PROVIDER_virtual/kernel"):
        msg = "Skipping linux-raspberrypi-dev as it is not the preferred " + \
              "provider of virtual/kernel."
        raise bb.parse.SkipRecipe(msg)
}

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-raspberrypi:"

LINUX_VERSION ?= "4.13"
LINUX_RPI_DEV_BRANCH ?= "rpi-4.13.y"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/raspberrypi/linux.git;protocol=git;branch=${LINUX_RPI_DEV_BRANCH} \
           file://0001-build-arm64-Add-rules-for-.dtbo-files-for-dts-overla.patch \
"
require linux-raspberrypi.inc

# Disable version check so that we don't have to edit this recipe every time
# upstream bumps the version
KERNEL_VERSION_SANITY_SKIP = "1"
