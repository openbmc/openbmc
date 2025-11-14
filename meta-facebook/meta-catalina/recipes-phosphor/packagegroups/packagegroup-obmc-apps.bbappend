FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# We do not currently want to use the whole PLDM daemon on Catalina, but
# just the `pldmtool` to be able to get some sensors out of devices.
RDEPENDS:${PN}-dmtf-pmci:remove:catalina = "pldm"
RDEPENDS:${PN}-dmtf-pmci:append = " pldmtool"
