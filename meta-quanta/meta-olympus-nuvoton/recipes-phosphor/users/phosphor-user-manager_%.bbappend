
FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://0001-Throw-NotAllowed-when-delete-or-disable-root.patch"