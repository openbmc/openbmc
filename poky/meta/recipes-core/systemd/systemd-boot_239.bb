require systemd.inc
FILESEXTRAPATHS =. "${FILE_DIRNAME}/systemd:"

DEPENDS = "intltool-native libcap util-linux gnu-efi gperf-native"

SRC_URI += "file://0002-use-lnr-wrapper-instead-of-looking-for-relative-opti.patch \
            file://0006-remove-nobody-user-group-checking.patch \
            file://0001-Fix-to-run-efi_cc-and-efi_ld-correctly-when-cross-co.patch \
            file://0001-meson-rename-Ddebug-to-Ddebug-extra.patch \
            "

inherit meson pkgconfig gettext
inherit deploy

EFI_CC ?= "${CC}"
EXTRA_OEMESON += "-Defi=true \
                  -Dgnu-efi=true \
                  -Defi-includedir=${STAGING_INCDIR}/efi \
                  -Defi-ldsdir=${STAGING_LIBDIR} \
                  -Defi-libdir=${STAGING_LIBDIR} \
                  -Dman=false \
                  -Defi-cc='${EFI_CC}' \
                  -Defi-ld='${LD}' \
                  "

# install to the image as boot*.efi if its the EFI_PROVIDER,
# otherwise install as the full name.
# This allows multiple bootloaders to coexist in a single image.
python __anonymous () {
    import re
    target = d.getVar('TARGET_ARCH')
    prefix = "" if d.getVar('EFI_PROVIDER') == "systemd-boot" else "systemd-"
    if target == "x86_64":
        systemdimage = prefix + "bootx64.efi"
    else:
        systemdimage = prefix + "bootia32.efi"
    d.setVar("SYSTEMD_BOOT_IMAGE", systemdimage)
    prefix = "systemd-" if prefix == "" else ""
    d.setVar("SYSTEMD_BOOT_IMAGE_PREFIX", prefix)
}

FILES_${PN} = "/boot/EFI/BOOT/${SYSTEMD_BOOT_IMAGE}"

RDEPENDS_${PN} += "virtual/systemd-bootconf"

# Imported from the old gummiboot recipe
TUNE_CCARGS_remove = "-mfpmath=sse"
COMPATIBLE_HOST = "(x86_64.*|i.86.*)-linux"
COMPATIBLE_HOST_x86-x32 = "null"

do_compile() {
	SYSTEMD_BOOT_EFI_ARCH="ia32"
	if [ "${TARGET_ARCH}" = "x86_64" ]; then
		SYSTEMD_BOOT_EFI_ARCH="x64"
	fi

	ninja src/boot/efi/${SYSTEMD_BOOT_IMAGE_PREFIX}${SYSTEMD_BOOT_IMAGE}
}

do_install() {
	install -d ${D}/boot
	install -d ${D}/boot/EFI
	install -d ${D}/boot/EFI/BOOT
	install ${B}/src/boot/efi/systemd-boot*.efi ${D}/boot/EFI/BOOT/${SYSTEMD_BOOT_IMAGE}
}

do_deploy () {
	install ${B}/src/boot/efi/systemd-boot*.efi ${DEPLOYDIR}
}
addtask deploy before do_build after do_compile
