SUMMARY = "Encoding files for Poppler"
DESCRIPTION = "Encoding files for use with poppler that enable poppler to \
               correctly render CJK and Cyrrilic."
HOMEPAGE = "https://poppler.freedesktop.org/"
LICENSE = "BSD-3-Clause & GPL-2.0-only & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=96287d49ec274d9c3222b5f966c132fd \
                    file://COPYING.adobe;md5=a790726a74164c30b5de1ef93fd69e99 \
                    file://COPYING.gpl2;md5=751419260aa954499f7abaabaa882bbe \
"

inherit allarch

INHIBIT_DEFAULT_DEPS = "1"

CMAP_RESOURCES_BASE = "https://github.com/adobe-type-tools/cmap-resources/raw/0561ebca035813ed04c3485bca636a0aa7abdc1d/cmapresources_identity-0/CMap"

SRC_URI = "http://poppler.freedesktop.org/${BP}.tar.gz \
           ${CMAP_RESOURCES_BASE}/Identity-H;name=idh \
           ${CMAP_RESOURCES_BASE}/Identity-V;name=idv"

SRC_URI[sha256sum] = "c835b640a40ce357e1b83666aabd95edffa24ddddd49b8daff63adb851cdab74"
SRC_URI[idh.md5sum] = "009c93cf0141ab7bd6acb7eea14306cc"
SRC_URI[idh.sha256sum] = "ae702c203a82ea124e9b96590f821db6fbf8754e2c4547a9dba0e82f94739e95"
SRC_URI[idv.md5sum] = "2f32a45d43d001c26eeac6b878855fbf"
SRC_URI[idv.sha256sum] = "89a85daf7031e93c883e76b9168a226dfd585bf5506e9e1956772163f15cb082"

do_compile() {
}

do_install() {
    oe_runmake install DESTDIR=${D} prefix=${prefix} datadir=${datadir}
    install -d ${D}${datadir}/poppler/cMap
    install -m644 ${WORKDIR}/Identity-* ${D}${datadir}/poppler/cMap/
}

FILES:${PN} += "${datadir}"
