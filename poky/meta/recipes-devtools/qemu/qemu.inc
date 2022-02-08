SUMMARY = "Fast open source processor emulator"
DESCRIPTION = "QEMU is a hosted virtual machine monitor: it emulates the \
machine's processor through dynamic binary translation and provides a set \
of different hardware and device models for the machine, enabling it to run \
a variety of guest operating systems"
HOMEPAGE = "http://qemu.org"
LICENSE = "GPLv2 & LGPLv2.1"

RDEPENDS_${PN}-ptest = "bash make"

require qemu-targets.inc
inherit pkgconfig ptest

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;endline=24;md5=8c5efda6cf1e1b03dcfd0e6c0d271c7f"

SRC_URI = "https://download.qemu.org/${BPN}-${PV}.tar.xz \
           file://powerpc_rom.bin \
           file://run-ptest \
           file://0001-qemu-Add-missing-wacom-HID-descriptor.patch \
           file://0002-Add-subpackage-ptest-which-runs-all-unit-test-cases-.patch \
           file://0003-qemu-Add-addition-environment-space-to-boot-loader-q.patch \
           file://0004-qemu-disable-Valgrind.patch \
           file://0005-qemu-native-set-ld.bfd-fix-cflags-and-set-some-envir.patch \
           file://0006-chardev-connect-socket-to-a-spawned-command.patch \
           file://0007-apic-fixup-fallthrough-to-PIC.patch \
           file://0008-linux-user-Fix-webkitgtk-hangs-on-32-bit-x86-target.patch \
           file://0009-Fix-webkitgtk-builds.patch \
           file://0010-configure-Add-pkg-config-handling-for-libgcrypt.patch \
           file://0011-hw-i386-pc-fix-regression-in-parsing-vga-cmdline-par.patch \
           file://0012-util-cacheinfo-fix-crash-when-compiling-with-uClibc.patch \
           file://CVE-2019-15890.patch \
           file://CVE-2020-1711.patch \
           file://CVE-2020-7039-1.patch \
           file://CVE-2020-7039-2.patch \
           file://CVE-2020-7039-3.patch \
           file://0001-Add-enable-disable-udev.patch \
           file://CVE-2020-7211.patch \
           file://0001-qemu-Do-not-include-file-if-not-exists.patch \
           file://CVE-2020-11102.patch \
           file://CVE-2020-11869.patch \
           file://CVE-2020-13361.patch \
           file://CVE-2020-10761.patch \
           file://CVE-2020-10702.patch \
           file://CVE-2020-13659.patch \
           file://CVE-2020-13800.patch \
           file://CVE-2020-13362.patch \
           file://CVE-2020-15863.patch \
           file://CVE-2020-14364.patch \
           file://CVE-2020-14415.patch \
           file://CVE-2020-16092.patch \
           file://0001-target-mips-Increase-number-of-TLB-entries-on-the-34.patch \
           file://CVE-2019-20175.patch \
           file://CVE-2020-24352.patch \
           file://CVE-2020-25723.patch \
           file://CVE-2021-20203.patch \
           file://CVE-2021-3392.patch \
           file://CVE-2020-25085.patch \
           file://CVE-2020-25624_1.patch \
           file://CVE-2020-25624_2.patch \
           file://CVE-2020-25625.patch \
           file://CVE-2020-29443.patch \
           file://CVE-2021-20221.patch \
           file://CVE-2021-20181.patch \
           file://CVE-2021-3416_1.patch \
           file://CVE-2021-3416_2.patch \
           file://CVE-2021-3416_3.patch \
           file://CVE-2021-3416_5.patch \
           file://CVE-2021-3416_6.patch \
           file://CVE-2021-3416_7.patch \
           file://CVE-2021-3416_8.patch \
           file://CVE-2021-3416_9.patch \
           file://CVE-2021-3416_10.patch \
           file://CVE-2021-20257.patch \
           file://CVE-2021-3544.patch \
           file://CVE-2021-3544_2.patch \
           file://CVE-2021-3544_3.patch \
           file://CVE-2021-3544_4.patch \
           file://CVE-2021-3544_5.patch \
           file://CVE-2021-3545.patch \
           file://CVE-2021-3546.patch \
           file://CVE-2021-3527-1.patch \
           file://CVE-2021-3527-2.patch \
           file://CVE-2021-3582.patch \
           file://CVE-2021-3607.patch \
           file://CVE-2021-3608.patch \
           file://CVE-2020-12829_1.patch \
           file://CVE-2020-12829_2.patch \
           file://CVE-2020-12829_3.patch \
           file://CVE-2020-12829_4.patch \
           file://CVE-2020-12829_5.patch \
           file://CVE-2020-27617.patch \
           file://CVE-2020-28916.patch \
           file://CVE-2021-3682.patch \
           "
UPSTREAM_CHECK_REGEX = "qemu-(?P<pver>\d+(\.\d+)+)\.tar"

SRC_URI[md5sum] = "278eeb294e4b497e79af7a57e660cb9a"
SRC_URI[sha256sum] = "d3481d4108ce211a053ef15be69af1bdd9dde1510fda80d92be0f6c3e98768f0"

# Applies against virglrender < 0.6.0 and not qemu itself
CVE_CHECK_WHITELIST += "CVE-2017-5957"

# The VNC server can expose host files uder some circumstances. We don't
# enable it by default.
CVE_CHECK_WHITELIST += "CVE-2007-0998"

# 'The issues identified by this CVE were determined to not constitute a vulnerability.'
# https://bugzilla.redhat.com/show_bug.cgi?id=1609015#c11
CVE_CHECK_WHITELIST += "CVE-2018-18438"

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
        sed -i -e 's,${HOSTTOOLS_DIR}/python3,${bindir}/python3,' \
            ${D}/${PTEST_PATH}/tests/qemu-iotests/common.env 
}

# QEMU_TARGETS is overridable variable
QEMU_TARGETS ?= "arm aarch64 i386 mips mipsel mips64 mips64el ppc ppc64 ppc64le riscv32 riscv64 sh4 x86_64"

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
    --with-git=/bin/false \
    --disable-git-update \
    ${PACKAGECONFIG_CONFARGS} \
    "

export LIBTOOL="${HOST_SYS}-libtool"

B = "${WORKDIR}/build"

EXTRA_OECONF_append = " --python=${HOSTTOOLS_DIR}/python3"

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
do_configure[cleandirs] += "${B}"

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

PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,libsdl2"
PACKAGECONFIG[virtfs] = "--enable-virtfs --enable-attr,--disable-virtfs,libcap attr,"
PACKAGECONFIG[aio] = "--enable-linux-aio,--disable-linux-aio,libaio,"
PACKAGECONFIG[xfs] = "--enable-xfsctl,--disable-xfsctl,xfsprogs,"
PACKAGECONFIG[xen] = "--enable-xen,--disable-xen,xen-tools,xen-tools-libxenstore xen-tools-libxenctrl xen-tools-libxenguest"
PACKAGECONFIG[vnc-sasl] = "--enable-vnc --enable-vnc-sasl,--disable-vnc-sasl,cyrus-sasl,"
PACKAGECONFIG[vnc-jpeg] = "--enable-vnc --enable-vnc-jpeg,--disable-vnc-jpeg,jpeg,"
PACKAGECONFIG[vnc-png] = "--enable-vnc --enable-vnc-png,--disable-vnc-png,libpng,"
PACKAGECONFIG[libcurl] = "--enable-curl,--disable-curl,curl,"
PACKAGECONFIG[nss] = "--enable-smartcard,--disable-smartcard,nss,"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,"
PACKAGECONFIG[gtk+] = "--enable-gtk,--disable-gtk,gtk+3 gettext-native"
PACKAGECONFIG[vte] = "--enable-vte,--disable-vte,vte gettext-native"
PACKAGECONFIG[libcap-ng] = "--enable-cap-ng,--disable-cap-ng,libcap-ng,"
PACKAGECONFIG[ssh] = "--enable-libssh,--disable-libssh,libssh,"
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
PACKAGECONFIG[bluez] = "--enable-bluez,--disable-bluez,bluez5"
PACKAGECONFIG[libiscsi] = "--enable-libiscsi,--disable-libiscsi"
PACKAGECONFIG[kvm] = "--enable-kvm,--disable-kvm"
PACKAGECONFIG[virglrenderer] = "--enable-virglrenderer,--disable-virglrenderer,virglrenderer"
# spice will be in meta-networking layer
PACKAGECONFIG[spice] = "--enable-spice,--disable-spice,spice"
# usbredir will be in meta-networking layer
PACKAGECONFIG[usb-redir] = "--enable-usb-redir,--disable-usb-redir,usbredir"
PACKAGECONFIG[snappy] = "--enable-snappy,--disable-snappy,snappy"
PACKAGECONFIG[glusterfs] = "--enable-glusterfs,--disable-glusterfs"
PACKAGECONFIG[xkbcommon] = "--enable-xkbcommon,--disable-xkbcommon,libxkbcommon"
PACKAGECONFIG[libudev] = "--enable-libudev,--disable-libudev,eudev"
PACKAGECONFIG[libxml2] = "--enable-libxml2,--disable-libxml2,libxml2"
PACKAGECONFIG[seccomp] = "--enable-seccomp,--disable-seccomp,libseccomp"

INSANE_SKIP_${PN} = "arch"

FILES_${PN} += "${datadir}/icons"
