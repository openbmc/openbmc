inherit allarch

SUMMARY = "Operating release identification"
DESCRIPTION = "The /etc/openembedded-release file contains operating system identification data."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
INHIBIT_DEFAULT_DEPS = "1"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"

VERSION = "0"
RELEASE_NAME = "${DISTRO_NAME} ${DISTRO} ${VERSION}"

def sanitise_version(ver):
    ret = ver.replace('+', '-').replace(' ','_')
    return ret.lower()

python do_compile () {
    import shutil
    release_name = d.getVar('RELEASE_NAME')		 
    with open(d.expand('${B}/openemebedded-release'), 'w') as f:
        f.write('%s\n' % release_name)
}
do_compile[vardeps] += "${RELEASE_NAME}"

do_install () {
    install -d ${D}${sysconfdir}
    install -m 0644 openemebedded-release ${D}${sysconfdir}/
}
