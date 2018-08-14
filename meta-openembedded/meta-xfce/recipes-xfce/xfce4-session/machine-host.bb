SUMMARY = "Meta package adding machine name to known hosts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

ALLOW_EMPTY_${PN} = "1"

LOCALHOSTMACHINE = "127.0.0.1    ${MACHINE}"

# on some machines starting applications as xfce4-terminal take ages without
# machine name in hosts
pkg_postinst_${PN} () {
if ! grep -q '${LOCALHOSTMACHINE}' $D/etc/hosts ; then
    echo '${LOCALHOSTMACHINE}' >> $D/etc/hosts
fi
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
