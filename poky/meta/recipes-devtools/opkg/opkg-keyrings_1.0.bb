SUMMARY = "Keyrings for verifying opkg packages and feeds"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# Distro-specific keys can be added to this package in two ways:
#
#   1) In a .bbappend, add .gpg and/or .asc files to SRC_URI and install them to
#      ${D}${datadir}/opkg/keyrings/ in a do_install:append function. These
#      files should not be named 'key-$name.gpg' to ensure they don't conflict
#      with keys exported as per (2).
#
#   2) In a .bbappend, distro config or local.conf, override the variable
#      OPKG_KEYRING_KEYS to contain a space-separated list of key names. For
#      each name, 'gpg --export $name' will be ran to export the public key to a
#      file named 'key-$name.gpg'. The public key must therefore be in the gpg
#      keyrings on the build machine.

OPKG_KEYRING_KEYS ?= ""

do_compile() {
    for name in ${OPKG_KEYRING_KEYS}; do
        gpg --export ${name} > ${B}/key-${name}.gpg
    done
}

do_install () {
    install -d ${D}${datadir}/opkg/keyrings/
    for name in ${OPKG_KEYRING_KEYS}; do
        install -m 0644 ${B}/key-${name}.gpg ${D}${datadir}/opkg/keyrings/
    done
}

FILES:${PN} = "${datadir}/opkg/keyrings"

# We need 'opkg-key' to run the postinst script
RDEPENDS:${PN} = "opkg"

pkg_postinst_ontarget:${PN} () {
    if test -x ${bindir}/opkg-key
    then
        ${bindir}/opkg-key populate
    fi
}
