require optee-os_corstone1000_common.inc

DEPENDS += " secure-partitions"
EXTRA_OEMAKE +="'SP_PACKAGING_METHOD=embedded'"

TS_INSTALL_PREFIX_PATH="${RECIPE_SYSROOT}/firmware/sp/opteesp"
EXTRA_OEMAKE += "'TS_INSTALL_PREFIX=${TS_INSTALL_PREFIX_PATH}'"

# se-proxy secure partition
SP_MKFILE_PATH="${TS_INSTALL_PREFIX}/lib/make/se-proxy.mk"

# smm-gateway secure partition
SP_MKFILE_PATH += "${TS_INSTALL_PREFIX}/lib/make/smm-gateway.mk"

EXTRA_OEMAKE += "'CFG_SP_MKFILE_PATH=${SP_MKFILE_PATH}'"

EXTRA_OEMAKE += "CFG_EMBED_DTB_SOURCE_FILE=${EMBED_DTB_SOURCE_FILE}"

SRC_URI:append = " file://sp_manifest_combined_se.dts;"
EMBED_DTB_SOURCE_FILE = "${WORKDIR}/sp_manifest_combined_se.dts"
