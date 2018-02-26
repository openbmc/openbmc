DESCRIPTION = "Collection of Vulkan examples"
LICENSE = "MIT"
DEPENDS = "zlib"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=dcf473723faabf17baa9b5f2207599d0 \
                    file://triangle/triangle.cpp;endline=12;md5=bccd1bf9cadd9e10086cf7872157e4fa"

SRC_URI = "git://github.com/SaschaWillems/Vulkan.git \
           file://0001-Support-installing-demos-support-out-of-tree-builds.patch \
           file://0001-Don-t-build-demos-with-questionably-licensed-data.patch \
           file://0001-Fix-build-on-x86.patch \
"
UPSTREAM_VERSION_UNKNOWN = "1"
SRCREV = "18df00c7b4677b0889486e16977857aa987947e2"
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
