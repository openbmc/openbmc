FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
     -Dredfish-bmc-journal=enabled \
     -Dhttp-body-limit=65 \
     -Dredfish-allow-deprecated-power-thermal=disabled \
     "
