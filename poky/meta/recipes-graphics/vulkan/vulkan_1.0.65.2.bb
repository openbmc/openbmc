SUMMARY = "3D graphics and compute API common loader"
DESCRIPTION = "Vulkan is a new generation graphics and compute API \
that provides efficient access to modern GPUs. These packages \
provide only the common vendor-agnostic library loader, headers and \
the vulkaninfo utility."
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-LoaderAndValidationLayers"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=99c647ca3d4f6a4b9d8628f757aad156 \
                    file://loader/loader.c;endline=25;md5=a87cd5442291c23d1fce4eece4cfde9d"
SRC_URI = "git://github.com/KhronosGroup/Vulkan-LoaderAndValidationLayers.git;branch=sdk-1.0.65 \
           file://demos-Don-t-build-tri-or-cube.patch \
           "
SRCREV = "73486a1a169d862d5210e2ad520d95319a2383fa"
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"

inherit cmake python3native lib_package distro_features_check
ANY_OF_DISTRO_FEATURES = "x11 wayland"

EXTRA_OECMAKE = "-DBUILD_WSI_MIR_SUPPORT=OFF \
                 -DBUILD_LAYERS=OFF \
                 -DBUILD_TESTS=OFF"

# must choose x11 or wayland or both
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '' ,d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '' ,d)}"
PACKAGECONFIG[x11] = "-DBUILD_WSI_XLIB_SUPPORT=ON -DBUILD_WSI_XCB_SUPPORT=ON -DDEMOS_WSI_SELECTION=XCB, -DBUILD_WSI_XLIB_SUPPORT=OFF -DBUILD_WSI_XCB_SUPPORT=OFF -DDEMOS_WSI_SELECTION=WAYLAND, libxcb libx11 libxrandr"
PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland"

RRECOMMENDS_${PN} = "mesa-vulkan-drivers"
