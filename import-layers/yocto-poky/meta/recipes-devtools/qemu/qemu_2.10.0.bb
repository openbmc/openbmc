require qemu.inc

inherit ptest

RDEPENDS_${PN}-ptest = "bash make"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913"

SRC_URI = "http://wiki.qemu-project.org/download/${BP}.tar.bz2 \
           file://powerpc_rom.bin \
           file://disable-grabs.patch \
           file://exclude-some-arm-EABI-obsolete-syscalls.patch \
           file://wacom.patch \
           file://add-ptest-in-makefile-v10.patch \
           file://run-ptest \
           file://qemu-enlarge-env-entry-size.patch \
           file://no-valgrind.patch \
           file://pathlimit.patch \
           file://qemu-2.5.0-cflags.patch \
           file://glibc-2.25.patch \
           file://0001-Provide-support-for-the-CUSE-TPM.patch \
           file://0002-Introduce-condition-to-notify-waiters-of-completed-c.patch \
           file://0003-Introduce-condition-in-TPM-backend-for-notification.patch \
           file://0004-Add-support-for-VM-suspend-resume-for-TPM-TIS-v2.9.patch \
           file://apic-fixup-fallthrough-to-PIC.patch \
           file://CVE-2017-13711.patch \
           file://CVE-2017-13673.patch \
           file://CVE-2017-13672.patch \
           file://CVE-2017-14167.patch \
           file://ppc_locking.patch \
           file://memfd.patch \
           "
UPSTREAM_CHECK_REGEX = "qemu-(?P<pver>\d+\..*)\.tar"


SRC_URI_append_class-native = " \
            file://fix-libcap-header-issue-on-some-distro.patch \
            file://cpus.c-qemu_cpu_kick_thread_debugging.patch \
            "

SRC_URI[md5sum] = "ca73441de73a9b52c6c49c97190d2185"
SRC_URI[sha256sum] = "7e9f39e1306e6dcc595494e91c1464d4b03f55ddd2053183e0e1b69f7f776d48"

COMPATIBLE_HOST_mipsarchn32 = "null"
COMPATIBLE_HOST_mipsarchn64 = "null"

do_install_append() {
    # Prevent QA warnings about installed ${localstatedir}/run
    if [ -d ${D}${localstatedir}/run ]; then rmdir ${D}${localstatedir}/run; fi
    install -Dm 0755 ${WORKDIR}/powerpc_rom.bin ${D}${datadir}/qemu
}

do_compile_ptest() {
	make buildtest-TESTS
}

do_install_ptest() {
	cp -rL ${B}/tests ${D}${PTEST_PATH}
	find ${D}${PTEST_PATH}/tests -type f -name "*.[Sshcod]" | xargs -i rm -rf {}

	cp ${S}/tests/Makefile.include ${D}${PTEST_PATH}/tests
}
