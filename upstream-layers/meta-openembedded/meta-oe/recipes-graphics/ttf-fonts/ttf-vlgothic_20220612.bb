require ttf.inc

SUMMARY = "Japanese TrueType fonts from Vine Linux"
HOMEPAGE = "http://vlgothic.dicey.org/"

LICENSE = "BSD-3-Clause AND LicenseRef-ttf-mplus"
LIC_FILES_CHKSUM = "file://LICENSE.en;md5=cc06b20e7a20bdf6c989624405378303 \
                    file://LICENSE_E.mplus;md5=1c4767416f20215f1e61b970f2117db9 \
"

SRC_URI = "https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/fonts-vlgothic/${PV}-1ubuntu1/fonts-vlgothic_${PV}.orig.tar.xz"

SRC_URI[sha256sum] = "c201dd3fa73492a2551a339fe235608d3be771237fd5868a526f1b3eb3164d93"

# The Launchpad +sourcefiles URL has no browsable listing; the Debian pool
# carries the same orig tarballs and is indexable.
UPSTREAM_CHECK_URI = "https://deb.debian.org/debian/pool/main/f/fonts-vlgothic/"
UPSTREAM_CHECK_REGEX = "fonts-vlgothic_(?P<pver>\d+)\.orig\.tar"

S = "${UNPACKDIR}/VLGothic"

do_install:append () {
    install -D -m644 ${S}/LICENSE_E.mplus ${D}${datadir}/licenses/${PN}/COPYING_MPLUS.txt
    install -D -m644 ${S}/README.sazanami ${D}${datadir}/licenses/${PN}/COPYING_SAZANAMI.txt
    install -D -m644 ${S}/LICENSE.en ${D}${datadir}/licenses/${PN}/COPYING_VLGOTHIC.txt
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/fonts/truetype ${datadir}/licenses"
