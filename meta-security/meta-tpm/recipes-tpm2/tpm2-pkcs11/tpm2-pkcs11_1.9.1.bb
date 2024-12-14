SUMMARY = "A PKCS#11 interface for TPM2 hardware"
DESCRIPTION = "PKCS #11 is a Public-Key Cryptography Standard that defines a standard method to access cryptographic services from tokens/ devices such as hardware security modules (HSM), smart cards, etc. In this project we intend to use a TPM2 device as the cryptographic token."
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fc19f620a102768d6dbd1e7166e78ab"

DEPENDS = "autoconf-archive pkgconfig sqlite3 openssl libtss2-dev tpm2-tools libyaml p11-kit python3-setuptools-native"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "ce24aa5ec2471545576e892b6f64fd873a424371bbf9be4ca3a0e689ea11c9b7"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

inherit autotools-brokensep pkgconfig python3native

EXTRA_OECONF += "--disable-ptool-checks"

do_compile:append() {
    cd ${S}/tools
    python3 setup.py build
}

do_install:append() {
    cd ${S}/tools
    export PYTHONPATH="${D}${PYTHON_SITEPACKAGES_DIR}"
    python3 setup.py install --root="${D}" --prefix="${prefix}" --install-lib="${PYTHON_SITEPACKAGES_DIR}" --optimize=1 --skip-build

    sed -i -e "s:${PYTHON}:${USRBINPATH}/env python3:g" "${D}${bindir}"/tpm2_ptool
}

PACKAGES =+ "${PN}-tools"

FILES:${PN}-tools = "\
    ${bindir}/tpm2_ptool \
    ${libdir}/${PYTHON_DIR}/* \
    "

FILES:${PN} += "\
    ${libdir}/pkcs11/* \
    ${datadir}/p11-kit/* \
    "

INSANE_SKIP:${PN}   += "dev-so"

RDEPENDS:${PN} = "p11-kit tpm2-tools "
RDEPENDS:${PN}-tools = "python3-pyyaml python3-cryptography python3-pyasn1-modules"

BBCLASSEXTEND = "native nativesdk"
