SUMMARY = "Snagboot intends to be an open-source replacement vendor-specific tools used to recover and/or reflash embedded platforms."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI[sha256sum] = "8451ff4cb96f8a4dc7aca06cee8a54db900e93b4e0e946205e0856b7a4cf4632"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    bash \
    python3-fcntl \
    python3-pyusb \
    python3-pyyaml \
    python3-setuptools \
    python3-six \
"

do_install:append() {
    install -D -m 0644 ${S}/src/snagrecover/50-snagboot.rules ${D}${sysconfdir}/udev/rules.d/50-snagboot.rules
}

BBCLASSEXTEND = "native nativesdk"
