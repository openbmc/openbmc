SUMMARY = "Fast open source processor emulator"
HOMEPAGE = "http://qemu.org"
LICENSE = "GPLv2 & LGPLv2.1"

RDEPENDS_${PN}-ptest = "bash make"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913"

SRC_URI = "https://download.qemu.org/${BPN}-${PV}.tar.xz \
           file://powerpc_rom.bin \
           file://0001-sdl.c-allow-user-to-disable-pointer-grabs.patch \
           file://0002-qemu-Add-missing-wacom-HID-descriptor.patch \
           file://0003-Add-subpackage-ptest-which-runs-all-unit-test-cases-.patch \
           file://run-ptest \
           file://0004-qemu-Add-addition-environment-space-to-boot-loader-q.patch \
           file://0005-qemu-disable-Valgrind.patch \
           file://0006-qemu-Limit-paths-searched-during-user-mode-emulation.patch \
           file://0007-qemu-native-set-ld.bfd-fix-cflags-and-set-some-envir.patch \
           file://0008-chardev-connect-socket-to-a-spawned-command.patch \
           file://0009-apic-fixup-fallthrough-to-PIC.patch \
           file://0010-linux-user-Fix-webkitgtk-hangs-on-32-bit-x86-target.patch \
           file://0011-Revert-linux-user-fix-mmap-munmap-mprotect-mremap-sh.patch \
           file://0001-Add-a-missing-X11-include.patch \
           file://0001-egl-headless-add-egl_create_context.patch \
           file://0014-fix-CVE-2018-16872.patch \
           file://0015-fix-CVE-2018-20124.patch \
           file://0016-fix-CVE-2018-20125.patch \
           file://0017-fix-CVE-2018-20126.patch \
           file://0018-fix-CVE-2018-20191.patch \
           file://0019-fix-CVE-2018-20216.patch \
           file://CVE-2019-3812.patch \
           "
UPSTREAM_CHECK_REGEX = "qemu-(?P<pver>\d+(\.\d+)+)\.tar"

SRC_URI[md5sum] = "fb687ce0b02d3bf4327e36d3b99427a8"
SRC_URI[sha256sum] = "6a0508df079a0a33c2487ca936a56c12122f105b8a96a44374704bef6c69abfc"

COMPATIBLE_HOST_mipsarchn32 = "null"
COMPATIBLE_HOST_mipsarchn64 = "null"

do_install_append() {
    # Prevent QA warnings about installed ${localstatedir}/run
    if [ -d ${D}${localstatedir}/run ]; then rmdir ${D}${localstatedir}/run; fi
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


require qemu-targets.inc
inherit pkgconfig bluetooth ptest

# QEMU_TARGETS is overridable variable
QEMU_TARGETS ?= "arm aarch64 i386 mips mipsel mips64 mips64el ppc riscv32 riscv64 sh4 x86_64"

EXTRA_OECONF = " \
    --prefix=${prefix} \
    --bindir=${bindir} \
    --includedir=${includedir} \
    --libdir=${libdir} \
    --mandir=${mandir} \
    --datadir=${datadir} \
    --docdir=${docdir}/${BPN} \
    --sysconfdir=${sysconfdir} \
    --libexecdir=${libexecdir} \
    --localstatedir=${localstatedir} \
    --with-confsuffix=/${BPN} \
    --disable-strip \
    --disable-werror \
    --extra-cflags='${CFLAGS}' \
    ${PACKAGECONFIG_CONFARGS} \
    "

export LIBTOOL="${HOST_SYS}-libtool"

B = "${WORKDIR}/build"

do_configure_prepend_class-native() {
	# Append build host pkg-config paths for native target since the host may provide sdl
	BHOST_PKGCONFIG_PATH=$(PATH=/usr/bin:/bin pkg-config --variable pc_path pkg-config || echo "")
	if [ ! -z "$BHOST_PKGCONFIG_PATH" ]; then
		export PKG_CONFIG_PATH=$PKG_CONFIG_PATH:$BHOST_PKGCONFIG_PATH
	fi
}

do_configure() {
    ${S}/configure ${EXTRA_OECONF}
}

do_install () {
	export STRIP=""
	oe_runmake 'DESTDIR=${D}' install
}

# The following fragment will create a wrapper for qemu-mips user emulation
# binary in order to work around a segmentation fault issue. Basically, by
# default, the reserved virtual address space for 32-on-64 bit is set to 4GB.
# This will trigger a MMU access fault in the virtual CPU. With this change,
# the qemu-mips works fine.
# IMPORTANT: This piece needs to be removed once the root cause is fixed!
do_install_append() {
	if [ -e "${D}/${bindir}/qemu-mips" ]; then
		create_wrapper ${D}/${bindir}/qemu-mips \
			QEMU_RESERVED_VA=0x0
	fi
}
# END of qemu-mips workaround

make_qemu_wrapper() {
        gdk_pixbuf_module_file=`pkg-config --variable=gdk_pixbuf_cache_file gdk-pixbuf-2.0`

        for tool in `ls ${D}${bindir}/qemu-system-*`; do
                create_wrapper $tool \
                        GDK_PIXBUF_MODULE_FILE=$gdk_pixbuf_module_file \
                        FONTCONFIG_PATH=/etc/fonts \
                        GTK_THEME=Adwaita
        done
}

# Disable kvm/virgl/mesa on targets that do not support it
PACKAGECONFIG_remove_darwin = "kvm virglrenderer glx gtk+"
PACKAGECONFIG_remove_mingw32 = "kvm virglrenderer glx gtk+"

PACKAGECONFIG[sdl] = "--enable-sdl --with-sdlabi=2.0,--disable-sdl,libsdl2"
PACKAGECONFIG[virtfs] = "--enable-virtfs --enable-attr,--disable-virtfs,libcap attr,"
PACKAGECONFIG[aio] = "--enable-linux-aio,--disable-linux-aio,libaio,"
PACKAGECONFIG[xfs] = "--enable-xfsctl,--disable-xfsctl,xfsprogs,"
PACKAGECONFIG[xen] = "--enable-xen,--disable-xen,xen,xen-libxenstore xen-libxenctrl xen-libxenguest"
PACKAGECONFIG[vnc-sasl] = "--enable-vnc --enable-vnc-sasl,--disable-vnc-sasl,cyrus-sasl,"
PACKAGECONFIG[vnc-jpeg] = "--enable-vnc --enable-vnc-jpeg,--disable-vnc-jpeg,jpeg,"
PACKAGECONFIG[vnc-png] = "--enable-vnc --enable-vnc-png,--disable-vnc-png,libpng,"
PACKAGECONFIG[libcurl] = "--enable-curl,--disable-curl,libcurl,"
PACKAGECONFIG[nss] = "--enable-smartcard,--disable-smartcard,nss,"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,"
PACKAGECONFIG[gtk+] = "--enable-gtk --enable-vte,--disable-gtk --disable-vte,gtk+3 vte gettext-native"
PACKAGECONFIG[libcap-ng] = "--enable-cap-ng,--disable-cap-ng,libcap-ng,"
PACKAGECONFIG[ssh2] = "--enable-libssh2,--disable-libssh2,libssh2,"
PACKAGECONFIG[gcrypt] = "--enable-gcrypt,--disable-gcrypt,libgcrypt,"
PACKAGECONFIG[nettle] = "--enable-nettle,--disable-nettle,nettle"
PACKAGECONFIG[libusb] = "--enable-libusb,--disable-libusb,libusb1"
PACKAGECONFIG[fdt] = "--enable-fdt,--disable-fdt,dtc"
PACKAGECONFIG[alsa] = "--audio-drv-list='oss alsa',,alsa-lib"
PACKAGECONFIG[glx] = "--enable-opengl,--disable-opengl,mesa"
PACKAGECONFIG[lzo] = "--enable-lzo,--disable-lzo,lzo"
PACKAGECONFIG[numa] = "--enable-numa,--disable-numa,numactl"
PACKAGECONFIG[gnutls] = "--enable-gnutls,--disable-gnutls,gnutls"
PACKAGECONFIG[bzip2] = "--enable-bzip2,--disable-bzip2,bzip2"
PACKAGECONFIG[bluez] = "--enable-bluez,--disable-bluez,${BLUEZ}"
PACKAGECONFIG[libiscsi] = "--enable-libiscsi,--disable-libiscsi"
PACKAGECONFIG[kvm] = "--enable-kvm,--disable-kvm"
PACKAGECONFIG[virglrenderer] = "--enable-virglrenderer,--disable-virglrenderer,virglrenderer"
# spice will be in meta-networking layer
PACKAGECONFIG[spice] = "--enable-spice,--disable-spice,spice"
# usbredir will be in meta-networking layer
PACKAGECONFIG[usb-redir] = "--enable-usb-redir,--disable-usb-redir,usbredir"

INSANE_SKIP_${PN} = "arch"
