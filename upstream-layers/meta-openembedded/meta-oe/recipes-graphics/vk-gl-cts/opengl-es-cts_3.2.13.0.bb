DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc

SRCREV_vk-gl-cts = "67ce2ed4a32658b6a80afc52dc4d2825cdfdda8a"

require opengl-es-cts-sources.inc

EXTRA_OECMAKE += "-DSELECTED_BUILD_TARGETS="cts-runner deqp-egl deqp-gles2 deqp-gles3 deqp-gles31 deqp-gl-shared de-internal-tests glcts" \
                  -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
"

do_install() {
	install -d ${D}/${CTSDIR}
	cp -r ${B}/external/openglcts/modules/* ${D}/${CTSDIR}
	cp -r ${S}/external/openglcts/data/gl_cts/data/mustpass/ ${D}/${CTSDIR}/mustpass/

	install -m 0755 ${B}/modules/egl/deqp-egl ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/gles2/deqp-gles2 ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/gles3/deqp-gles3 ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/gles31/deqp-gles31 ${D}/${CTSDIR}
	install -m 0755 ${B}/modules/internal/de-internal-tests ${D}/${CTSDIR}

	rm -rf ${D}/${CTSDIR}/*.a ${D}/${CTSDIR}/cmake_install.cmake ${D}/${CTSDIR}/CMakeFiles
	rm -rf ${D}/${CTSDIR}/*/*.a ${D}/${CTSDIR}/*/cmake_install.cmake ${D}/${CTSDIR}/*/CMakeFiles
	rm -rf ${D}/${CTSDIR}/common/*/*.a ${D}/${CTSDIR}/common/*/cmake_install.cmake ${D}/${CTSDIR}/common/*/CMakeFiles
}

SECURITY_CFLAGS:riscv64 = "${SECURITY_NOPIE_CFLAGS}"
LTO = ""
