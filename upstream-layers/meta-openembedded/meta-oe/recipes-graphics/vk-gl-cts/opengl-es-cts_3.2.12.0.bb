DESCRIPTION = "OpenGL CTS"

require khronos-cts.inc

SRCREV_vk-gl-cts = "42e61858e862e153cd0fe36593a8c3f7c16c3275"

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
	rm -rf ${D}/${CTSDIR}/common/subgroups/*.a ${D}/${CTSDIR}/common/subgroups/cmake_install.cmake ${D}/${CTSDIR}/common/subgroups/CMakeFiles
}

SECURITY_CFLAGS:riscv64 = "${SECURITY_NOPIE_CFLAGS}"
# GCC-15 segfaults see - https://gcc.gnu.org/bugzilla/show_bug.cgi?id=120119
TUNE_CCARGS:remove:aarch64 = "-mcpu=cortex-a57+crc"
LTO = ""
