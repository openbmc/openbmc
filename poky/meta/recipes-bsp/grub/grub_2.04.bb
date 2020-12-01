require grub2.inc

RDEPENDS_${PN}-common += "${PN}-editenv"
RDEPENDS_${PN} += "${PN}-common"
RDEPENDS_${PN}_class-native = ""

RPROVIDES_${PN}-editenv += "${PN}-efi-editenv"

PROVIDES_append_class-native = " grub-efi-native"

PACKAGES =+ "${PN}-editenv ${PN}-common"
FILES_${PN}-editenv = "${bindir}/grub-editenv"
FILES_${PN}-common = " \
    ${bindir} \
    ${sysconfdir} \
    ${sbindir} \
    ${datadir}/grub \
"

FILES_${PN}-common_append_aarch64 = " \
    ${libdir}/${BPN} \
"

do_install_append () {
    install -d ${D}${sysconfdir}/grub.d
    # Remove build host references...
    find "${D}" -name modinfo.sh -type f -exec \
        sed -i \
        -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
        -e 's|${DEBUG_PREFIX_MAP}||g' \
        -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
        {} +
}

INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"

BBCLASSEXTEND = "native nativesdk"
