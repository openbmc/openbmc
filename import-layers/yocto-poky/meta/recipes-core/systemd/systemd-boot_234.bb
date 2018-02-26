require systemd.inc
FILESEXTRAPATHS =. "${FILE_DIRNAME}/systemd:"

DEPENDS = "intltool-native libcap util-linux gnu-efi gperf-native"

SRC_URI += "file://0007-use-lnr-wrapper-instead-of-looking-for-relative-opti.patch"

inherit autotools pkgconfig gettext
inherit deploy

EFI_CC ?= "${CC}"
# Man pages are packaged through the main systemd recipe
EXTRA_OECONF = " --enable-gnuefi \
                 --with-efi-includedir=${STAGING_INCDIR} \
                 --with-efi-ldsdir=${STAGING_LIBDIR} \
                 --with-efi-libdir=${STAGING_LIBDIR} \
                 --disable-manpages \
                 EFI_CC='${EFI_CC}' \
               "

# Imported from the old gummiboot recipe
TUNE_CCARGS_remove = "-mfpmath=sse"
COMPATIBLE_HOST = "(x86_64.*|i.86.*)-linux"
COMPATIBLE_HOST_linux-gnux32 = "null"

do_compile() {
	SYSTEMD_BOOT_EFI_ARCH="ia32"
	if [ "${TARGET_ARCH}" = "x86_64" ]; then
		SYSTEMD_BOOT_EFI_ARCH="x64"
	fi

	oe_runmake systemd-boot${SYSTEMD_BOOT_EFI_ARCH}.efi
}

do_install() {
	# Bypass systemd installation with a NOP
	:
}

do_deploy () {
	install ${B}/systemd-boot*.efi ${DEPLOYDIR}
}
addtask deploy before do_build after do_compile
