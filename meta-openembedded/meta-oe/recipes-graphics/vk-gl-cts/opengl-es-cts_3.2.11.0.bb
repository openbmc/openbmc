DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc
# opengl-es-cts-3.2.11.0
SRCREV_vk-gl-cts = "66956d195169596472e956e3aebf2df8e3bd960d"
SRCREV_amber = "0f003c2785489f59cd01bb2440fcf303149100f2"
SRCREV_glslang = "4da479aa6afa43e5a2ce4c4148c572a03123faf3"
SRCREV_spirv-headers = "ff2afc3afc48dff4eec2a10f0212402a80708e38"
SRCREV_spirv-tools = "148c97f6876e427efd76d2328122c3075eab4b8f"
SRCREV_ESExtractor = "ce5d7ebcf0ebb0d78385ee4cc34653eb6764bfc4"
# Not yet needed
SRCREV_jsoncpp = "9059f5cad030ba11d37818847443a53918c327b1"
SRCREV_vulkan-docs = "ed4ba0242beb89a1795d6084709fa9e713559c94"
SRCREV_vulkan-validationlayers = "a92629196a4fed15e59c74aa965dd47bd5ece3b7"    
SRCREV_video-parser = "6821adf11eb4f84a2168264b954c170d03237699"
SRC_URI[renderdoc.sha256sum] = "e7b5f0aa5b1b0eadc63a1c624c0ca7f5af133aa857d6a4271b0ef3d0bdb6868e"

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
