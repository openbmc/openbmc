FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "libtspi"

SRC_URI_append = " file://0001-xfrmi-Only-build-if-libcharon-is-built.patch"

PACKAGECONFIG += " \
    aikgen \
    tpm \
"

PACKAGECONFIG[tpm] = "--enable-tpm,--disable-tpm,,"
PACKAGECONFIG[aikgen] = "--enable-aikgen,--disable-aikgen,,"

PACKAGECONFIG_ima += "\
    imc-test \
    imv-test \
    imc-scanner \
    imv-scanner \
    imc-os \
    imv-os \
    imc-attestation \
    imv-attestation \
    tnc-ifmap \
    tnc-imc \
    tnc-imv \
    tnc-pdp \
    tnccs-11 \
    tnccs-20 \
    tnccs-dynamic \
    "

EXTRA_OECONF += "--with-linux-headers=${STAGING_KERNEL_DIR}"

PACKAGECONFIG[imc-test] = "--enable-imc-test,--disable-imc-test,,"
PACKAGECONFIG[imc-scanner] = "--enable-imc-scanner,--disable-imc-scanner,,"
PACKAGECONFIG[imc-os] = "--enable-imc-os,--disable-imc-os,,"
PACKAGECONFIG[imc-attestation] = "--enable-imc-attestation,--disable-imc-attestation,,"
PACKAGECONFIG[imc-swima] = "--enable-imc-swima, --disable-imc-swima,,"
PACKAGECONFIG[imc-hcd] = "--enable-imc-hcd, --disable-imc-hcd,,"
PACKAGECONFIG[tnc-imc] = "--enable-tnc-imc,--disable-tnc-imc,,"

PACKAGECONFIG[imv-test] = "--enable-imv-test,--disable-imv-test,,"
PACKAGECONFIG[imv-scanner] = "--enable-imv-scanner,--disable-imv-scanner,,"
PACKAGECONFIG[imv-os] = "--enable-imv-os,--disable-imv-os,,"
PACKAGECONFIG[imv-attestation] = "--enable-imv-attestation,--disable-imv-attestation,,"
PACKAGECONFIG[imv-swima] = "--enable-imv-swima, --disable-imv-swima,,"
PACKAGECONFIG[imv-hcd] = "--enable-imv-hcd, --disable-imv-hcd,,"
PACKAGECONFIG[tnc-imv] = "--enable-tnc-imv,--disable-tnc-imv,,"

PACKAGECONFIG[tnc-ifmap] = "--enable-tnc-ifmap,--disable-tnc-ifmap,libxml2,"
PACKAGECONFIG[tnc-pdp] = "--enable-tnc-pdp,--disable-tnc-pdp,,"

PACKAGECONFIG[tnccs-11] = "--enable-tnccs-11,--disable-tnccs-11,libxml2,"
PACKAGECONFIG[tnccs-20] = "--enable-tnccs-20,--disable-tnccs-20,,"
PACKAGECONFIG[tnccs-dynamic] = "--enable-tnccs-dynamic,--disable-tnccs-dynamic,,"

#FILES_${PN} += "${libdir}/ipsec/imcvs/*.so ${datadir}/regid.2004-03.org.strongswan"
#FILES_${PN}-dbg += "${libdir}/ipsec/imcvs/.debug"
#FILES_${PN}-dev += "${libdir}/ipsec/imcvs/*.la"
#FILES_${PN}-staticdev += "${libdir}/ipsec/imcvs/*.a"
