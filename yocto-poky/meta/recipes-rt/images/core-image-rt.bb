require recipes-core/images/core-image-minimal.bb

DESCRIPTION = "A small image just capable of allowing a device to boot plus a \
real-time test suite and tools appropriate for real-time use."
DEPENDS = "linux-yocto-rt"

IMAGE_INSTALL += "rt-tests hwlatdetect"

LICENSE = "MIT"
