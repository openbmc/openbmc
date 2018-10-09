SUMMARY = "Phosphor certificate manager configuration for an nslcd authority service"

PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/LICENSE;md5=19407077e42b1ba3d653da313f1f5b4e"

RRECOMMENDS_${PN} = "phosphor-certificate-manager"

inherit allarch
inherit obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} = ""
SYSTEMD_ENVIRONMENT_FILE_${PN} = "obmc/cert/authority"
SYSTEMD_LINK_${PN} = "../phosphor-certificate-manager@.service:${SYSTEMD_DEFAULT_TARGET}.wants/phosphor-certificate-manager@authority.service"
