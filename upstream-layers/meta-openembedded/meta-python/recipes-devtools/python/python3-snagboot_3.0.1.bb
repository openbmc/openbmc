SUMMARY = "Snagboot intends to be an open-source replacement vendor-specific tools used to recover and/or reflash embedded platforms."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI[sha256sum] = "280003da631bb47434d1d5428e713524d6cf8a5ee049ebc5990363af3721dd05"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    bash \
    python3-fcntl \
    python3-pyusb \
    python3-pyyaml \
    python3-setuptools \
"

do_install:append() {
    install -D -m 0644 ${S}/src/snagrecover/50-snagboot.rules ${D}${sysconfdir}/udev/rules.d/50-snagboot.rules
}

BBCLASSEXTEND = "native nativesdk"
