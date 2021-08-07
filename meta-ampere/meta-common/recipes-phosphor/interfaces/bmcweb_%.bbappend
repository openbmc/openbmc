FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
     -Dinsecure-tftp-update=enabled \
     -Dbmcweb-logging=enabled \
     -Dredfish-bmc-journal=enabled \
     "
