SUMMARY = "Small and fast POSIX-compliant shell"
HOMEPAGE = "http://gondor.apana.org.au/~herbert/dash/"
SECTION = "System Environment/Shells"

LICENSE = "BSD & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b5262b4a1a1bff72b48e935531976d2e"

inherit autotools update-alternatives

SRC_URI = "http://gondor.apana.org.au/~herbert/${BPN}/files/${BP}.tar.gz \
           file://0001-Fix-printf-format-errors-with-clang.patch \
           "
SRC_URI[md5sum] = "6472702a8d9760d166ef8333dcb527a6"
SRC_URI[sha256sum] = "5ecd5bea72a93ed10eb15a1be9951dd51b52e5da1d4a7ae020efd9826b49e659"

EXTRA_OECONF += "--bindir=${base_bindir}"

ALTERNATIVE_${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/dash"
ALTERNATIVE_PRIORITY = "10"

pkg_postinst_${PN} () {
    grep -q "^${base_bindir}/dash$" $D${sysconfdir}/shells || echo ${base_bindir}/dash >> $D${sysconfdir}/shells
}

pkg_postrm_${PN} () {
    printf "$(grep -v "^${base_bindir}/dash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
