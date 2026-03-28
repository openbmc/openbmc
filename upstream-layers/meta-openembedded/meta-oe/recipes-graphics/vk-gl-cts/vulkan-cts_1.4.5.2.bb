DESCRIPTION = "Vulkan CTS"

require khronos-cts.inc

SRCREV_vk-gl-cts = "a02761593aedeb1d3941ddd344ca35d7a2f9de46"

require vulkan-cts-sources.inc

# Workaround an optimization bug that breaks createMeshShaderMiscTestsEXT
OECMAKE_CXX_FLAGS:remove:toolchain-gcc = "-O2"

REQUIRED_DISTRO_FEATURES = "vulkan"
inherit features_check

DEPENDS += " vulkan-loader"

EXTRA_OECMAKE += "-DSELECTED_BUILD_TARGETS="deqp-vk deqp-vksc""

do_install() {
	install -d ${D}/${CTSDIR}/mustpass
	cp -r ${B}/external/vulkancts/modules/vulkan/* ${D}/${CTSDIR}/
        cp -r ${S}/external/vulkancts/mustpass/main/ ${D}/${CTSDIR}/mustpass/
	rm -rf ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -rf ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
}
