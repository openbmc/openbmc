SUMMARY = "Phosphor OpenBMC pre-init scripts"
DESCRIPTION = "Phosphor OpenBMC filesystem mount reference implementation."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI += "file://obmc-init.sh"
SRC_URI += "file://obmc-shutdown.sh"
SRC_URI += "file://obmc-update.sh"
SRC_URI += "file://whitelist"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch

do_install() {
        for f in init-download-url init-options
        do
                if test -e $f
                then
                        install -m 0755 ${UNPACKDIR}/$f ${D}/$f
                fi
        done
        install -m 0755 ${UNPACKDIR}/obmc-init.sh ${D}/init
        install -m 0755 ${UNPACKDIR}/obmc-shutdown.sh ${D}/shutdown
        install -m 0755 ${UNPACKDIR}/obmc-update.sh ${D}/update

        # verify white list entries
        # each entry should have no '//', '/./', '/../' or trailing '/', '/.', '/..'
        while read -r f
        do
                if test $(realpath -L -m ${UNPACKDIR}$f) != ${UNPACKDIR}$f
                then
                        bberror "Bad whitelist entry ${f}."
                fi
        done < ${UNPACKDIR}/whitelist
        install -m 0644 ${UNPACKDIR}/whitelist ${D}/whitelist
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_base-utils}"

FILES:${PN} += " /init /shutdown /update /whitelist /dev "
FILES:${PN} += " /init-options /init-download-url "
