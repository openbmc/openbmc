inherit allarch

SUMMARY = "Operating system identification"
DESCRIPTION = "The /usr/lib/os-release file contains operating system identification data."
LICENSE = "MIT"
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

def sanitise_version(ver):
    # VERSION_ID should be (from os-release(5)):
    #    lower-case string (mostly numeric, no spaces or other characters
    #    outside of 0-9, a-z, ".", "_" and "-")
    ret = ver.replace('+', '-').replace(' ','_')
    return ret.lower()

python do_compile () {
    import shutil
    with open(d.expand('${B}/os-release'), 'w') as f:
        for field in d.getVar('OS_RELEASE_FIELDS').split():
            value = d.getVar(field)
            if value and field == 'VERSION_ID':
                value = sanitise_version(value)
            if value:
                f.write('{0}="{1}"\n'.format(field, value))
}
do_compile[vardeps] += "${OS_RELEASE_FIELDS}"

do_install () {
    install -d ${D}${nonarch_libdir} ${D}${sysconfdir}
    install -m 0644 os-release ${D}${nonarch_libdir}/
    lnr ${D}${nonarch_libdir}/os-release ${D}${sysconfdir}/os-release
}

FILES_${PN} += "${nonarch_libdir}/os-release"
