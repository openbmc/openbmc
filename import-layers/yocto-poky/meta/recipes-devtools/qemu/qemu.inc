SUMMARY = "Fast open source processor emulator"
HOMEPAGE = "http://qemu.org"
LICENSE = "GPLv2 & LGPLv2.1"
DEPENDS = "glib-2.0 zlib pixman"
RDEPENDS_${PN}_class-target += "bash"
RDEPENDS_${PN}-ptest = "bash make"

require qemu-targets.inc
inherit autotools ptest
BBCLASSEXTEND = "native nativesdk"

PR = "r1"

# QEMU_TARGETS is overridable variable
QEMU_TARGETS ?= "arm aarch64 i386 mips mipsel mips64 mips64el ppc sh4 x86_64"

SRC_URI = "\
    file://powerpc_rom.bin \
    file://disable-grabs.patch \
    file://exclude-some-arm-EABI-obsolete-syscalls.patch \
    file://wacom.patch \
    file://add-ptest-in-makefile.patch \
    file://run-ptest \
    file://0001-target-mips-add-24KEc-CPU-definition.patch \
    "

SRC_URI_append_class-native = "\
    file://fix-libcap-header-issue-on-some-distro.patch \
    file://cpus.c-qemu_cpu_kick_thread_debugging.patch \
    "

EXTRA_OECONF += "--target-list=${@get_qemu_target_list(d)} --disable-werror  --disable-bluez --disable-libiscsi --with-system-pixman --extra-cflags='${CFLAGS}'"

EXTRA_OECONF_class-nativesdk = "--target-list=${@get_qemu_target_list(d)} --disable-werror"

EXTRA_OEMAKE_append_class-native = " LD='${LD}' AR='${AR}' OBJCOPY='${OBJCOPY}' LDFLAGS='${LDFLAGS}'"

LDFLAGS_append_class-native = " -fuse-ld=bfd"

export LIBTOOL="${HOST_SYS}-libtool"

do_configure_prepend_class-native() {
	# Append build host pkg-config paths for native target since the host may provide sdl
	BHOST_PKGCONFIG_PATH=$(PATH=/usr/bin:/bin pkg-config --variable pc_path pkg-config || echo "")
	if [ ! -z "$BHOST_PKGCONFIG_PATH" ]; then
		export PKG_CONFIG_PATH=$PKG_CONFIG_PATH:$BHOST_PKGCONFIG_PATH
	fi

	# Alter target makefiles to accept CFLAGS set via env
	sed -i -r \
		-e 's/^(C|OP_C|HELPER_C)FLAGS=/\1FLAGS+=/' \
		"${S}"/Makefile "${S}"/Makefile.target
}

KVMENABLE = "--enable-kvm"
KVMENABLE_darwin = "--disable-kvm"
KVMENABLE_mingw32 = "--disable-kvm"

do_configure() {
    # Handle distros such as CentOS 5 32-bit that do not have kvm support
    KVMOPTS="--disable-kvm"
    if [ "${PN}" != "qemu-native" -a "${PN}" != "nativesdk-qemu" ] \
       || [ -f /usr/include/linux/kvm.h ] ; then
       KVMOPTS="${KVMENABLE}"
    fi

    ${S}/configure --prefix=${prefix} --sysconfdir=${sysconfdir} --libexecdir=${libexecdir} --localstatedir=${localstatedir} --disable-strip ${EXTRA_OECONF} $KVMOPTS
    test ! -e ${S}/target-i386/beginend_funcs.sh || chmod a+x ${S}/target-i386/beginend_funcs.sh
}

do_compile_ptest() {
	make buildtest-TESTS
}

do_install_ptest() {
	cp -rL ${B}/tests ${D}${PTEST_PATH}
	find ${D}${PTEST_PATH}/tests -type f -name "*.[Sshcod]" | xargs -i rm -rf {}

	cp ${S}/tests/Makefile.include ${D}${PTEST_PATH}/tests
}

do_install () {
	export STRIP="true"
	autotools_do_install
	install -d ${D}${datadir}/qemu
	install -m 0755 ${WORKDIR}/powerpc_rom.bin ${D}${datadir}/qemu
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

PACKAGECONFIG ??= " \
	fdt sdl \
	${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen', '', d)} \
	"
PACKAGECONFIG_class-native ??= "fdt alsa uuid"
PACKAGECONFIG_class-nativesdk ??= "fdt sdl"

PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,libsdl"
PACKAGECONFIG[virtfs] = "--enable-virtfs --enable-attr,--disable-virtfs,libcap attr,"
PACKAGECONFIG[aio] = "--enable-linux-aio,--disable-linux-aio,libaio,"
PACKAGECONFIG[xfs] = "--enable-xfsctl,--disable-xfsctl,xfsprogs,"
PACKAGECONFIG[xen] = "--enable-xen,--disable-xen,xen,xen-libxenstore xen-libxenctrl xen-libxenguest"
PACKAGECONFIG[vnc-sasl] = "--enable-vnc --enable-vnc-sasl,--disable-vnc-sasl,cyrus-sasl,"
PACKAGECONFIG[vnc-jpeg] = "--enable-vnc --enable-vnc-jpeg,--disable-vnc-jpeg,jpeg,"
PACKAGECONFIG[vnc-png] = "--enable-vnc --enable-vnc-png,--disable-vnc-png,libpng,"
PACKAGECONFIG[libcurl] = "--enable-curl,--disable-curl,libcurl,"
PACKAGECONFIG[nss] = "--enable-smartcard,--disable-smartcard,nss,"
PACKAGECONFIG[uuid] = "--enable-uuid,--disable-uuid,util-linux,"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,"
PACKAGECONFIG[gtk+] = "--enable-gtk --with-gtkabi=3.0 --enable-vte,--disable-gtk --disable-vte,gtk+3 vte"
PACKAGECONFIG[libcap-ng] = "--enable-cap-ng,--disable-cap-ng,libcap-ng,"
PACKAGECONFIG[ssh2] = "--enable-libssh2,--disable-libssh2,libssh2,"
PACKAGECONFIG[gcrypt] = "--enable-gcrypt,--disable-gcrypt,gcrypt,"
PACKAGECONFIG[nettle] = "--enable-nettle,--disable-nettle,nettle"
PACKAGECONFIG[libusb] = "--enable-libusb,--disable-libusb,libusb1"
PACKAGECONFIG[fdt] = "--enable-fdt,--disable-fdt,dtc"
PACKAGECONFIG[alsa] = ",,alsa-lib"
PACKAGECONFIG[glx] = "--enable-opengl,--disable-opengl,mesa"
PACKAGECONFIG[lzo] = "--enable-lzo,--disable-lzo,lzo"
PACKAGECONFIG[numa] = "--enable-numa,--disable-numa,numactl"
PACKAGECONFIG[gnutls] = "--enable-gnutls,--disable-gnutls,gnutls"
PACKAGECONFIG[bzip2] = "--enable-bzip2,--disable-bzip2,bzip2"

EXTRA_OECONF += "${@bb.utils.contains('PACKAGECONFIG', 'alsa', '--audio-drv-list=oss,alsa', '', d)}"

INSANE_SKIP_${PN} = "arch"
