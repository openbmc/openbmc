DESCRIPTION = "Collection of Vulkan examples"
LICENSE = "MIT"
DEPENDS = "zlib"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=dcf473723faabf17baa9b5f2207599d0 \
                    file://examples/triangle/triangle.cpp;endline=12;md5=bccd1bf9cadd9e10086cf7872157e4fa"

SRCREV_glm = "1ad55c5016339b83b7eec98c31007e0aee57d2bf"
SRCREV_ktx = "726d14d02c95bb21ec9e43807751b491d295dd3c"

SRC_URI = "git://github.com/SaschaWillems/Vulkan.git \
           git://github.com/g-truc/glm;destsuffix=git/external/glm;name=glm \
           git://github.com/KhronosGroup/KTX-Software;destsuffix=git/external/ktx;name=ktx;lfs=0 \
           file://0001-Don-t-build-demos-with-questionably-licensed-data.patch \
           "
UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "4818f85916bf88c1ca8c2ed1a46e0e758651489e"
UPSTREAM_CHECK_GITTAGREGEX = "These are not the releases you're looking for"
S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = 'vulkan'

inherit cmake features_check
DEPENDS = "vulkan-loader assimp wayland-protocols wayland-native"

do_install_append () {
    # Remove assets that have uncertain licenses
    rm ${D}${datadir}/vulkan-demos/models/armor/* \
       ${D}${datadir}/vulkan-demos/models/sibenik/* \
       ${D}${datadir}/vulkan-demos/models/vulkanscene* \
       ${D}${datadir}/vulkan-demos/models/plants.dae \
       ${D}${datadir}/vulkan-demos/textures/texturearray_plants*

    mv ${D}${bindir}/screenshot ${D}${bindir}/vulkan-screenshot
}

EXTRA_OECMAKE = "-DRESOURCE_INSTALL_DIR=${datadir}/vulkan-demos"

ANY_OF_DISTRO_FEATURES = "x11 wayland"

# Can only pick one of [wayland,xcb]
PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', 'xcb', d)}"
PACKAGECONFIG[wayland] = "-DUSE_WAYLAND_WSI=ON, -DUSE_WAYLAND_WSI=OFF, wayland"
PACKAGECONFIG[xcb] = ",,libxcb"
