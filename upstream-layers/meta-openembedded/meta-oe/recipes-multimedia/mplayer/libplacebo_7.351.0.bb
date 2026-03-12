SUMMARY = "Reusable library for GPU-accelerated video/image rendering primitives"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435ed639f84d4585d93824e7da3d85da"

DEPENDS += "fastfloat glad-native python3-mako-native python3-jinja2-native vulkan-headers"

SRC_URI = "git://code.videolan.org/videolan/libplacebo.git;protocol=https;branch=v7.351 \
           file://0001-vulkan-utils_gen-fix-for-python-3.14.patch \
          "
SRCREV = "3188549fba13bbdf3a5a98de2a38c2e71f04e21e"

inherit meson pkgconfig


PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'vulkan opengl', d)} lcms"

PACKAGECONFIG[vulkan] =  "-Dvulkan=enabled,-Dvulkan=disabled,vulkan-loader shaderc spirv-shader-generator"
PACKAGECONFIG[glslang] = "-Dglslang=enabled,-Dglslang=disabled,glslang"
PACKAGECONFIG[opengl] = "-Dopengl=enabled,-Dopengl=disabled"
PACKAGECONFIG[lcms] = "-Dlcms=enabled,-Dlcms=disabled,lcms"
PACKAGECONFIG[demos] = "-Ddemos=true,-Ddemos=false,ffmpeg virtual/libsdl2 libsdl2-image"

EXTRA_OEMESON = "-Dvulkan-registry=${STAGING_DATADIR}/vulkan/registry/vk.xml"

do_install:append(){
  if [ -f ${D}${libdir}/pkgconfig/libplacebo.pc ]; then
    sed -i "s,${RECIPE_SYSROOT}${libdir}/libSPIRV.so,-lSPIRV,g" ${D}${libdir}/pkgconfig/libplacebo.pc
    sed -i "s,${RECIPE_SYSROOT}${libdir}/libglslang.so,-lglslang,g" ${D}${libdir}/pkgconfig/libplacebo.pc
  fi
}
