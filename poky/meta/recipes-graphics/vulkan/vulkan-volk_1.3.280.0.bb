SUMMARY = "A meta-loader for Vulkan"
DESCRIPTION = "Volk allows one to dynamically load entrypoints required \
to use Vulkan without linking to vulkan-1.dll or statically linking Vulkan loader. \
"
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/zeux/volk"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=12e6af3a0e2a5e5dbf7796aa82b64626"

SRC_URI = "git://github.com/zeux/volk.git;branch=master;protocol=https"
SRCREV = "01986ac85fa2e5c70df09aeae9c907e27c5d50b2"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"

DEPENDS = "vulkan-headers"

EXTRA_OECMAKE = "\
    -DVOLK_INSTALL=ON \
    "

inherit cmake features_check pkgconfig

# These recipes need to be updated in lockstep with each other:
# glslang, vulkan-headers, vulkan-loader, vulkan-tools,
# vulkan-validation-layers, spirv-headers, spirv-tools,
# vulkan-utility-libraries.
# The tags versions should always be sdk-x.y.z, as this is what
# upstream considers a release.
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"

do_install:append() {
    sed -i -e 's,${STAGING_DIR_TARGET},,g' ${D}${libdir}/cmake/volk/volkTargets.cmake
}
