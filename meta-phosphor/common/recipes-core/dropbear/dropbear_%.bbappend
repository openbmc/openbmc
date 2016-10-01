inherit obmc-phosphor-discovery-service

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://dropbearkey.service \
	    file://0001-dropbear-Add-c-command-option-to-force-a-specific-co.patch"

REGISTERED_SERVICES_${PN} += "ssh:_ssh._tcp:22 sftp:_sftp._tcp:21"
