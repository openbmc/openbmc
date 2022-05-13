FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

EXTRA_OECONF:append:olympus-nuvoton = " BMC_DUMP_TOTAL_SIZE=500 "
#SRC_URI += "file://0001-adjust-current-size-of-dump-directory-to-near-linux-.patch"
