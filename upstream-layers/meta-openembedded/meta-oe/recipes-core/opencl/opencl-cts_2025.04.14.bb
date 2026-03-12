SUMMARY = "OpenCL CTS"
DESCRIPTION = "OpenCL CTS test suite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pkgconfig cmake features_check

REQUIRED_DISTRO_FEATURES = "opencl"

DEPENDS += "opencl-headers virtual/libopencl1 spirv-tools-native"
RDEPENDS:${PN} += "python3-core python3-io"


SRC_URI = "git://github.com/KhronosGroup/OpenCL-CTS.git;protocol=https;branch=main;lfs=0 \
	   file://0001-Ignore-Compiler-Warnings.patch"

SRCREV = "e96edaef8b582c2412a2aab4b82f5c88af88617d"

EXTRA_OECMAKE:append = " -DENABLE_WERROR=OFF -DCL_INCLUDE_DIR=${STAGING_INCDIR} -DCL_LIB_DIR=${STAGING_LIBDIR} -DOPENCL_LIBRARIES=OpenCL"

PACKAGECONFIG = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl gles', '', d)} \
	${@bb.utils.filter('DISTRO_FEATURES', 'vulkan', d)} \
"
PACKAGECONFIG[opengl] = "-DGL_IS_SUPPORTED=ON,-DGL_IS_SUPPORTED=OFF,virtual/libgl glew freeglut"
PACKAGECONFIG[gles] = "-DGLES_IS_SUPPORTED=ON,-DGLES_IS_SUPPORTED=OFF,virtual/egl virtual/libgles2"
PACKAGECONFIG[vulkan] = "-DVULKAN_IS_SUPPORTED=ON,-DVULKAN_IS_SUPPORTED=OFF,vulkan-headers glslang-native"

SECURITY_STRINGFORMAT:remove = "-Werror=format-security"

do_install() {
        install -d ${D}${bindir}/opencl_test_conformance
        cp -r ${B}/test_conformance/* ${D}${bindir}/opencl_test_conformance
        sed -i 's:/usr/bin/python:/usr/bin/python3:g' ${D}${bindir}/opencl_test_conformance/run_conformance.py
	 find "${D}${bindir}/opencl_test_conformance" -name cmake_install.cmake -type f -delete
        find "${D}${bindir}/opencl_test_conformance" -name libvulkan_wrapper.a -type f -delete
        find "${D}${bindir}/opencl_test_conformance" -name CMakeFiles -type d -exec rm -rf "{}" \; -depth
}

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
