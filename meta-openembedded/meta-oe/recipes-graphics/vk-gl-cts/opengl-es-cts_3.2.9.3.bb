DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc
# opengl-es-cts-3.2.9.3
SRCREV_vk-gl-cts = "7f5fb62245d535a1fc0bf50b2c7e5a342dc551fe"
SRCREV_amber = "933ecb4d6288675a92eb1650e0f52b1d7afe8273"
SRCREV_glslang = "a0ad0d7067521fff880e36acfb8ce453421c3f25"
SRCREV_spirv-headers = "87d5b782bec60822aa878941e6b13c0a9a954c9b"
SRCREV_spirv-tools = "f98473ceeb1d33700d01e20910433583e5256030"
SRCREV_ESExtractor = "ce5d7ebcf0ebb0d78385ee4cc34653eb6764bfc4"
# Not yet needed
SRCREV_jsoncpp = "9059f5cad030ba11d37818847443a53918c327b1"
SRCREV_vulkan-docs = "9a2e576a052a1e65a5d41b593e693ff02745604b"
SRCREV_video-parser = "7d68747d3524842afaf050c5e00a10f5b8c07904"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"

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

