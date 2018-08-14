require qemu.inc

inherit ptest

RDEPENDS_${PN}-ptest = "bash make"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913"

SRC_URI = "http://wiki.qemu-project.org/download/${BP}.tar.bz2 \
           file://powerpc_rom.bin \
           file://disable-grabs.patch \
           file://wacom.patch \
           file://add-ptest-in-makefile-v10.patch \
           file://run-ptest \
           file://qemu-enlarge-env-entry-size.patch \
           file://no-valgrind.patch \
           file://pathlimit.patch \
           file://qemu-2.5.0-cflags.patch \
           file://chardev-connect-socket-to-a-spawned-command.patch \
           file://apic-fixup-fallthrough-to-PIC.patch \
           file://linux-user-Fix-webkitgtk-hangs-on-32-bit-x86-target.patch \
           file://memfd.patch \
           file://0001-arm-translate-a64-treat-DISAS_UPDATE-as-variant-of-D.patch \
           file://check-PS2Queue-pointers-in-post_load-routine.patch \
           "
UPSTREAM_CHECK_REGEX = "qemu-(?P<pver>\d+\..*)\.tar"


SRC_URI_append_class-native = " \
            file://fix-libcap-header-issue-on-some-distro.patch \
            file://cpus.c-qemu_cpu_kick_thread_debugging.patch \
            "

SRC_URI[md5sum] = "61cf862b6007eba4ac98247776af2e27"
SRC_URI[sha256sum] = "d9df2213ceed32e91dab7bc9dd19c1af83f91ba72c7aeef7605dfaaf81732ccb"

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
	# Don't check the file genreated by configure
	sed -i -e '/wildcard config-host.mak/d' \
	       -e '$ {/endif/d}' ${D}${PTEST_PATH}/tests/Makefile.include
}
