DESCRIPTION = "Collection of Vulkan examples"
LICENSE = "MIT"
DEPENDS = "zlib"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=dcf473723faabf17baa9b5f2207599d0 \
                    file://examples/triangle/triangle.cpp;endline=12;md5=bccd1bf9cadd9e10086cf7872157e4fa"

SRCREV_glm = "01f9ab5b6d21e5062ac0f6e0f205c7fa2ca9d769"
SRCREV_gli = "8e43030b3e12bb58a4663d85adc5c752f89099c0"

SRC_URI = "git://github.com/SaschaWillems/Vulkan.git \
           git://github.com/g-truc/glm;destsuffix=git/external/glm;name=glm \
           git://github.com/g-truc/gli;destsuffix=git/external/gli;name=gli \
           file://0001-Don-t-build-demos-with-questionably-licensed-data.patch \
"
UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "ae0b59c6e2e8630a2ae26f4a0b7a72cbe7547948"
UPSTREAM_CHECK_GITTAGREGEX = "These are not the releases you're looking for"
S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = 'vulkan'

inherit cmake distro_features_check
DEPENDS = "vulkan assimp"

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
PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', 'xcb' ,d)}"
PACKAGECONFIG[wayland] = "-DUSE_WAYLAND_WSI=ON, -DUSE_WAYLAND_WSI=OFF, wayland"
PACKAGECONFIG[xcb] = ",,libxcb"
