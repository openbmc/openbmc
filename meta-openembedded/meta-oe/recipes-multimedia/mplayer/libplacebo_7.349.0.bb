SUMMARY ="Reusable library for GPU-accelerated video/image rendering primitives"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435ed639f84d4585d93824e7da3d85da"

DEPENDS += "fastfloat glad-native python3-mako-native python3-jinja2-native vulkan-headers"

SRC_URI = "git://code.videolan.org/videolan/libplacebo.git;protocol=https;branch=v7.349"
SRCREV = "1fd3c7bde7b943fe8985c893310b5269a09b46c5"

inherit meson pkgconfig

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'vulkan opengl', d)} lcms"

PACKAGECONFIG[vulkan] =  "-Dvulkan=enabled,-Dvulkan=disabled,vulkan-loader shaderc spirv-shader-generator"
PACKAGECONFIG[glslang] = "-Dglslang=enabled,-Dglslang=disabled,glslang"
PACKAGECONFIG[opengl] = "-Dopengl=enabled,-Dopengl=disabled"
PACKAGECONFIG[lcms] = "-Dlcms=enabled,-Dlcms=disabled,lcms"
PACKAGECONFIG[demos] = "-Ddemos=true,-Ddemos=false,ffmpeg libsdl2 libsdl2-image"

EXTRA_OEMESON = "-Dvulkan-registry=${STAGING_DATADIR}/vulkan/registry/vk.xml"
