SUMMARY = "Small and fast POSIX-compliant shell"
HOMEPAGE = "http://gondor.apana.org.au/~herbert/dash/"
SECTION = "System Environment/Shells"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b5262b4a1a1bff72b48e935531976d2e"

inherit autotools update-alternatives

SRC_URI = "http://gondor.apana.org.au/~herbert/${BPN}/files/${BP}.tar.gz"
SRC_URI[sha256sum] = "d10dfd41cda59165560db39ca915c2c4a7636fff04281d8d2df77ad92c753e2b"

CVE_PRODUCT = "dash:dash"

EXTRA_OECONF += "--bindir=${base_bindir}"

ALTERNATIVE:${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/dash"
ALTERNATIVE_PRIORITY = "10"

pkg_postinst:${PN} () {
    grep -q "^${base_bindir}/dash$" $D${sysconfdir}/shells || echo ${base_bindir}/dash >> $D${sysconfdir}/shells
}

pkg_postrm:${PN} () {
    printf "$(grep -v "^${base_bindir}/dash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
