FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
     -Dinsecure-tftp-update=disabled \
     -Dbmcweb-logging=enabled \
     -Dredfish-bmc-journal=enabled \
     -Dhttp-body-limit=65 \
     -Dredfish-new-powersubsystem-thermalsubsystem=enabled \
     -Dredfish-allow-deprecated-power-thermal=disabled \
     "
