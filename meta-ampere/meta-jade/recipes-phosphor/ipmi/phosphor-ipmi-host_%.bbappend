FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

DEPENDS_append_mtjade = " mtjade-yaml-config"

RRECOMMENDS_${PN} += "ipmitool"

EXTRA_OECONF_mtjade = " \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/mtjade-yaml-config/ipmi-fru-read.yaml \
    "

do_install_append_mtjade(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
}
