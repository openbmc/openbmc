DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc
# opengl-es-cts-3.2.6.1
SRCREV_vk-gl-cts = "7e023f81b4fff54b558882fe739d7c959d0a02a8"
SRCREV_amber = "d26ee22dd7faab1845a531d410f7ec1db407402a"
SRCREV_glslang = "c538b5d796fb24dd418fdd650c7f76e56bcc3dd8"
SRCREV_spirv-headers = "e4322e3be589e1ddd44afb20ea842a977c1319b8"
SRCREV_spirv-tools = "1eb89172a82b436d8037e8a8c29c80f7e1f7df74"
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

SECURITY_CFLAGS_riscv64 = "${SECURITY_NOPIE_CFLAGS}"
LTO = ""

