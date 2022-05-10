FILESEXTRAPATHS:append:nuvoton := "${THISDIR}/${PN}:"

# Fix static assert build break
SRC_URI:append:nuvoton = " file://0001-Use-numeric-limit-is-intergral-replace-std-is-intergral.patch"
