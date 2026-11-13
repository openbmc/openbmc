SUMMARY = "'DING is not GNU' helper libraries for SSSD and FreeIPA"
HOMEPAGE = "https://github.com/SSSD/ding-libs"
SECTION = "base"
LICENSE = "GPL-3.0-or-later AND LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/SSSD/${BPN}/releases/download/${PV}/${BP}.tar.gz"

inherit autotools pkgconfig gettext

SRC_URI[sha256sum] = "5df2f202f892b4c1a6caf87f0056398a8288b281da9d7e65cd7637978ec20ef3"
