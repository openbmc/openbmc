require recipes-core/images/core-image-minimal.bb

# Skip processing of this recipe if linux-yocto-rt is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers.
python () {
    if d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-yocto-rt":
        raise bb.parse.SkipRecipe("Set PREFERRED_PROVIDER_virtual/kernel to linux-yocto-rt to enable it")
}

DESCRIPTION = "Small image capable of booting a device with a test suite and \
tools for real-time use. It includes the full meta-toolchain, development \
headers and libraries to form a standalone SDK."
DEPENDS += "linux-yocto-rt"

IMAGE_FEATURES += "dev-pkgs tools-sdk tools-debug eclipse-debug tools-profile tools-testapps debug-tweaks"

IMAGE_INSTALL += "rt-tests hwlatdetect kernel-dev"

LICENSE = "MIT"
