SUMMARY = "Snagboot intends to be an open-source replacement vendor-specific tools used to recover and/or reflash embedded platforms."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI[sha256sum] = "c2d21cadddecfd97dd62a8b66b6f88bd56272627b9b71272e9dda6f868ee8715"

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
    install -D -m 0644 ${S}/src/snagrecover/80-snagboot.rules ${D}${sysconfdir}/udev/rules.d/80-snagboot.rules
}
