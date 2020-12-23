SUMMARY = "Enable TLS with static CA"
DESCRIPTION = "Add static CA and only enable TLS authentication"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://certs/authority/ \
            file://bmcweb_persistent_data.json \
           "
do_install(){
    install -d ${D}${sysconfdir}/ssl/certs/authority
    install -m 0644 -D ${WORKDIR}/certs/authority/* \
                       ${D}${sysconfdir}/ssl/certs/authority

    install -d ${D}${ROOT_HOME}
    install -m 0640 ${WORKDIR}/bmcweb_persistent_data.json ${D}${ROOT_HOME}
}

FILES_${PN} = "${ROOT_HOME}/bmcweb_persistent_data.json \
               ${sysconfdir}/ssl/certs/authority/* \
              "
