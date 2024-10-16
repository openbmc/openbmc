# This is needed to guarantee that our whitelist takes
# precendence over the OpenBMC one
FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc:dev = " file://whitelist.dev"

do_install:append:gbmc:dev() {
    cat ${UNPACKDIR}/whitelist.dev >>${D}/whitelist
}

SRC_URI:append:gbmc:dev = " file://rwfs-clean-dev.patch"
# Required for the clean-dev patch to detect version changes
RDEPENDS:${PN}:append:gbmc:dev = " os-release"

do_install:append:gbmc:prod() {
    echo "clean-rwfs-filesystem" > ${D}/init-options-base
    chmod 0644 ${D}/init-options-base
}

FILES:${PN}:append:gbmc:prod = " /init-options-base"
