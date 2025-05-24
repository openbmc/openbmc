SUMMARY = "Snagboot intends to be an open-source replacement vendor-specific tools used to recover and/or reflash embedded platforms."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI[sha256sum] = "00f4933cb52fb73dcf61a0723d77d9d6bc509affb7cd5d5dcd875d2c333e8b08"

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
