
FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " \
    file://0001-meta-quanta-meta-olympus-nuvoton-phosphor-user-manag.patch \
    file://0001-Throw-NotAllowed-when-delete-or-disable-root.patch \
    "
