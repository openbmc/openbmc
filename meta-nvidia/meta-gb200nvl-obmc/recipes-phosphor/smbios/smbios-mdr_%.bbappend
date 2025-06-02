FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# smbios-ipmi-blob is used to collect SMBIOS information from IPMI blobs
PACKAGECONFIG:append = " smbios-ipmi-blob"

# cpuinfo collects CPU information through the Intel PECI interface
PACKAGECONFIG:remove = "cpuinfo"

# enable tpm-dbus to collect TPM information
PACKAGECONFIG:append = " tpm-dbus"

# enable firmware-inventory-dbus to collect firmware inventory information
PACKAGECONFIG:append = " firmware-inventory-dbus"
