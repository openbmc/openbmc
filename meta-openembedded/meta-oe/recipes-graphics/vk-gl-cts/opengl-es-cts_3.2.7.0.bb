DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc
# opengl-es-cts-3.2.7.0
SRCREV_vk-gl-cts = "7cba7113c40f2ff03573c8c2c90661b2249e04fa"
SRCREV_amber = "4d0115cccfcb3b73d20b6513b1c40748e6403c50"
SRCREV_glslang = "ffccefddfd9a02ec0c0b6dd04ef5e1042279c97f"
SRCREV_spirv-headers = "104ecc356c1bea4476320faca64440cd1df655a3"
SRCREV_spirv-tools = "cd590fa3341284cd6d1ee82366155786cfd44c96"
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

	rm -r ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -r ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
	rm -r ${D}/${CTSDIR}/common/subgroups/*.a ${D}/${CTSDIR}/common/subgroups/cmake_install.cmake ${D}/${CTSDIR}/common/subgroups/CMakeFiles
}

SECURITY_CFLAGS:riscv64 = "${SECURITY_NOPIE_CFLAGS}"
LTO = ""

