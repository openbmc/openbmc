SUMMARY = "A PKCS#11 interface for TPM2 hardware"
DESCRIPTION = "PKCS #11 is a Public-Key Cryptography Standard that defines a standard method to access cryptographic services from tokens/ devices such as hardware security modules (HSM), smart cards, etc. In this project we intend to use a TPM2 device as the cryptographic token."
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fc19f620a102768d6dbd1e7166e78ab"

DEPENDS = "autoconf-archive pkgconfig dstat sqlite3 openssl libtss2-dev tpm2-tools libyaml p11-kit python3-setuptools-native"

SRC_URI = "git://github.com/tpm2-software/tpm2-pkcs11.git;branch=master \
           file://bootstrap_fixup.patch \
           file://0001-remove-local-binary-checkes.patch \
           file://677.patch \
           "

SRCREV = "c2d53cc1af6b9df13c832715442853b21048c273"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig python3native

do_configure_prepend () {
    ${S}/bootstrap
}

do_compile_append() {
    cd ${S}/tools
    python3 setup.py build
}

do_install_append() {
    install -d ${D}${libdir}/pkcs11
    install -d ${D}${datadir}/p11-kit
    rm -f ${D}${libdir}/pkcs11/libtpm2_pkcs11.so

    cd ${S}/tools
    export PYTHONPATH="${D}${PYTHON_SITEPACKAGES_DIR}"
    ${PYTHON_PN} setup.py install --root="${D}" --prefix="${prefix}" --install-lib="${PYTHON_SITEPACKAGES_DIR}" --optimize=1 --skip-build

    sed -i -e "s:${PYTHON}:${USRBINPATH}/env ${PYTHON_PN}:g" "${D}${bindir}"/tpm2_ptool
}

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "\
    ${bindir}/tpm2_ptool \
    ${libdir}/${PYTHON_DIR}/* \
    "

FILES_${PN} += "\
    ${libdir}/pkcs11/* \
    ${datadir}/p11-kit/* \
    "

RDEPNDS_${PN} = "tpm2-tools"
RDEPENDS_${PN}-tools += "${PYTHON_PN}-setuptools ${PYTHON_PN}-pyyaml ${PYTHON_PN}-cryptography ${PYTHON_PN}-pyasn1-modules"
