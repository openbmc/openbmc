SECTION = "base"
require uclibc.inc
require uclibc-git.inc

DEPENDS = "linux-libc-headers ncurses-native virtual/${TARGET_PREFIX}gcc-initial libgcc-initial kern-tools-native"
PROVIDES = "virtual/${TARGET_PREFIX}libc-initial"

PACKAGES = ""
PACKAGES_DYNAMIC = ""

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${TARGET_ARCH}"

do_install() {
	# Install initial headers into the cross dir
	make PREFIX=${D} DEVEL_PREFIX=${prefix}/ RUNTIME_PREFIX=/ \
		install_headers install_startfiles

        # add links to linux-libc-headers: final uclibc build need this.
        for t in linux asm asm-generic; do
                if [ -d ${D}${includedir}/$t ]; then
                    rm -rf ${D}${includedir}/$t
                fi
                ln -sf ${STAGING_DIR_TARGET}${includedir}/$t ${D}${includedir}/
        done

}
do_compile() {
	:
}

do_siteconfig () {
        :
}

do_populate_sysroot[sstate-outputdirs] = "${STAGING_DIR_TCBOOTSTRAP}/"
