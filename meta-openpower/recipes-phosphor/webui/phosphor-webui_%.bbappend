FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:df-openpower = " file://config.json"

do_configure:append:df-openpower() {
    # Overwrite the config.json to turn off/on Web UI panels
    # E.g. Turn off the Redfish Event Log Panel since openpower systems use
    # the D-Bus Event Log Panel
    cp ${UNPACKDIR}/config.json ${S}/
}
