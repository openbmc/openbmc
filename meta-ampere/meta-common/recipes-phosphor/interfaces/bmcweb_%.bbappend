FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"

EXTRA_OEMESON_append = " \
     -Dinsecure-tftp-update=enabled \
     -Dbmcweb-logging=enabled \
     -Dredfish-bmc-journal=enabled \
     "
