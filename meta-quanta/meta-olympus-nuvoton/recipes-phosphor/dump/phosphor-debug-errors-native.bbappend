FILESEXTRAPATHS:append:olympus-nuvoton := "${THISDIR}/${BPN}:"

SRC_URI:append:olympus-nuvoton = " file://olympus-nuvoton_errors_watch.yaml"

do_install:append:olympus-nuvoton() {
    DEST=${D}${datadir}/dump
    install olympus-nuvoton_errors_watch.yaml ${DEST}/errors_watch.yaml
}

