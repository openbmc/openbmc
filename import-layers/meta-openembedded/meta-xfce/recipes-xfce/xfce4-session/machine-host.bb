SUMMARY = "Meta package adding machine name to known hosts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

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
