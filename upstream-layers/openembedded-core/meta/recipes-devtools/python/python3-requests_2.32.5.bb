SUMMARY = "Python HTTP for Humans."
HOMEPAGE = "https://requests.readthedocs.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "dbba0bac56e100853db0ea71b82b4dfd5fe2bf6d3754a8893c3af500cec7d7cf"

SRC_URI += "file://CVE-2026-25645.patch"
SRC_URI:append:class-nativesdk = " file://environment.d-python3-requests.sh"

do_install:append:class-nativesdk() {
	mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
	install -m 644 ${UNPACKDIR}/environment.d-python3-requests.sh ${D}${SDKPATHNATIVE}/environment-setup.d/python3-requests.sh
}

RDEPENDS:${PN} += " \
    python3-certifi \
    python3-email \
    python3-json \
    python3-netserver \
    python3-pysocks \
    python3-urllib3 \
    python3-chardet \
    python3-idna \
    python3-compression \
"

FILES:${PN}:append:class-nativesdk = " ${SDKPATHNATIVE}/environment-setup.d/python3-requests.sh"

CVE_PRODUCT = "requests"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2024-35195] = "fixed-version: fixed since 2.32.0"
CVE_STATUS[CVE-2024-47081] = "fixed-version: fixed since 2.32.4"
