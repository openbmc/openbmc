python __anonymous() {
    if "linux-raspberrypi-dev" not in d.getVar("PREFERRED_PROVIDER_virtual/kernel"):
        msg = "Skipping linux-raspberrypi-dev as it is not the preferred " + \
              "provider of virtual/kernel."
        raise bb.parse.SkipRecipe(msg)
}

LINUX_VERSION ?= "5.10.y"
LINUX_RPI_BRANCH ?= "rpi-5.10.y"

SRCREV_machine = "${AUTOREV}"
SRCREV_meta = "${AUTOREV}"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;protocol=git;branch=${LINUX_RPI_BRANCH} \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=master;destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

KERNEL_EXTRA_ARGS += "DTC_FLAGS='-@ -H epapr'"

# Disable version check so that we don't have to edit this recipe every time
# upstream bumps the version
KERNEL_VERSION_SANITY_SKIP = "1"
