SUMMARY = "Small and fast POSIX-compliant shell"
HOMEPAGE = "http://gondor.apana.org.au/~herbert/dash/"
SECTION = "System Environment/Shells"

LICENSE = "BSD & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b5262b4a1a1bff72b48e935531976d2e"

inherit autotools update-alternatives

SRC_URI = "http://gondor.apana.org.au/~herbert/${BPN}/files/${BP}.tar.gz"
SRC_URI[md5sum] = "027236e48b9202607b1418fee42c473e"
SRC_URI[sha256sum] = "4dd9a6ed5fe7546095157918fe5d784bb0b7887ae13de50e1e2d11e1b5a391cb"

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
