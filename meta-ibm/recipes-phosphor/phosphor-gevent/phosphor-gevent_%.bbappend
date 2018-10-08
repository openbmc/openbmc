SUMMARY = "Modifications to support Nginx"

# override service and socket file to use nginx
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
