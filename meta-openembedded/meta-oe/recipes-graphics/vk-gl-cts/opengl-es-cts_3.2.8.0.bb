DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc
# opengl-es-cts-3.2.8.0
SRCREV_vk-gl-cts = "317f467fac032b88a4789e4345421f92c4e8716d"
SRCREV_amber = "209d92e2c27a333e723d24497e8c7a07b2f2eb39"
SRCREV_glslang = "9158061398a96033c990e69156bd28c67114544b"
SRCREV_spirv-headers = "449bc986ba6f4c5e10e32828783f9daef2a77644"
SRCREV_spirv-tools = "ee30773650eca50b1cd3c913babcc2b50d7b91fd"
# Not yet needed
SRCREV_jsoncpp = "9059f5cad030ba11d37818847443a53918c327b1"
SRCREV_vulkan-docs = "d70e01c0be7b8a7d20b186b30b29a75b18bba75d"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"

SRC_URI += "file://0001-Remove-dead-variable-984.patch;patchdir=external/amber/src \
            file://0001-include-missing-cstdint.patch;patchdir=external/amber/src"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}/${CTSDIR}
	cp -r ${B}/external/openglcts/modules/* ${D}/${CTSDIR}

	install -m 0755 ${B}/modules/egl/deqp-egl ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/gles2/deqp-gles2 ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/gles3/deqp-gles3 ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/gles31/deqp-gles31 ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/internal/de-internal-tests ${D}/${CTSDIR}

	rm -rf ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -rf ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
	rm -rf ${D}/${CTSDIR}/common/subgroups/*.a ${D}/${CTSDIR}/common/subgroups/cmake_install.cmake ${D}/${CTSDIR}/common/subgroups/CMakeFiles
}

SECURITY_CFLAGS:riscv64 = "${SECURITY_NOPIE_CFLAGS}"
LTO = ""

