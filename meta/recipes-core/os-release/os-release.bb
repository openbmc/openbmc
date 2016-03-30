inherit allarch

SUMMARY = "Operating system identification"
DESCRIPTION = "The /etc/os-release file contains operating system identification data."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
INHIBIT_DEFAULT_DEPS = "1"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"

# Other valid fields: BUILD_ID ID_LIKE ANSI_COLOR CPE_NAME
#                     HOME_URL SUPPORT_URL BUG_REPORT_URL
OS_RELEASE_FIELDS = "ID ID_LIKE NAME VERSION VERSION_ID PRETTY_NAME"

ID = "${DISTRO}"
NAME = "${DISTRO_NAME}"
VERSION = "${DISTRO_VERSION}${@' (%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
VERSION_ID = "${DISTRO_VERSION}"
PRETTY_NAME = "${DISTRO_NAME} ${VERSION}"
BUILD_ID ?= "${DATETIME}"
BUILD_ID[vardepsexclude] = "DATETIME"

python do_compile () {
    import shutil
    with open(d.expand('${B}/os-release'), 'w') as f:
        for field in d.getVar('OS_RELEASE_FIELDS', True).split():
            value = d.getVar(field, True)
            if value:
                f.write('{0}={1}\n'.format(field, value))
    if d.getVar('RPM_SIGN_PACKAGES', True) == '1':
        rpm_gpg_pubkey = d.getVar('RPM_GPG_PUBKEY', True)
        bb.utils.mkdirhier('${B}/rpm-gpg')
        distro_version = d.getVar('DISTRO_VERSION', True) or "oe.0"
        shutil.copy2(rpm_gpg_pubkey, d.expand('${B}/rpm-gpg/RPM-GPG-KEY-%s' % distro_version))
}
do_compile[vardeps] += "${OS_RELEASE_FIELDS}"
do_compile[depends] += "signing-keys:do_export_public_keys"

do_install () {
    install -d ${D}${sysconfdir}
    install -m 0644 os-release ${D}${sysconfdir}/

    if [ -d "rpm-gpg" ]; then
        install -d "${D}${sysconfdir}/pki"
        cp -r "rpm-gpg" "${D}${sysconfdir}/pki/"
    fi
}
