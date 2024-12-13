inherit allarch

SUMMARY = "Operating system identification"
DESCRIPTION = "The /usr/lib/os-release file contains operating system identification data."
HOMEPAGE = "https://www.freedesktop.org/software/systemd/man/os-release.html"
LICENSE = "MIT"
INHIBIT_DEFAULT_DEPS = "1"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"

# See: https://www.freedesktop.org/software/systemd/man/os-release.html
# Other valid fields: BUILD_ID ID_LIKE ANSI_COLOR CPE_NAME
#                     HOME_URL SUPPORT_URL BUG_REPORT_URL
OS_RELEASE_FIELDS = "\
    ID ID_LIKE NAME VERSION VERSION_ID VERSION_CODENAME PRETTY_NAME \
    CPE_NAME \
"
OS_RELEASE_UNQUOTED_FIELDS = "ID VERSION_ID VARIANT_ID"

ID = "${DISTRO}"
NAME = "${DISTRO_NAME}"
VERSION = "${DISTRO_VERSION}${@' (%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
VERSION_ID = "${DISTRO_VERSION}"
VERSION_CODENAME = "${@d.getVar('DISTRO_CODENAME') or ''}"
PRETTY_NAME = "${DISTRO_NAME} ${VERSION}"

# The vendor field is hardcoded to "openembedded" deliberately. We'd
# advise developers leave it as this value to clearly identify the
# underlying build environment from which the OS was constructed. We
# understand people will want to identify themselves as the people who
# built the image, we'd suggest using the DISTRO element to do this, so
# that is customisable.
# This end result combines to mean systems can be traced back to both who
# built them and which system was used, which is ultimately the goal of
# the CPE.

CPE_DISTRO ??= "${DISTRO}"
CPE_NAME="cpe:/o:openembedded:${CPE_DISTRO}:${VERSION_ID}"

BUILD_ID ?= "${DATETIME}"
BUILD_ID[vardepsexclude] = "DATETIME"

def sanitise_value(ver):
    # unquoted fields like VERSION_ID should be (from os-release(5)):
    #    lower-case string (mostly numeric, no spaces or other characters
    #    outside of 0-9, a-z, ".", "_" and "-")
    ret = ver.replace('+', '-').replace(' ','_')
    return ret.lower()

python do_compile () {
    with open(d.expand('${B}/os-release'), 'w') as f:
        for field in d.getVar('OS_RELEASE_FIELDS').split():
            unquotedFields = d.getVar('OS_RELEASE_UNQUOTED_FIELDS').split()
            value = d.getVar(field)
            if value:
                if field in unquotedFields:
                    value = sanitise_value(value)
                    f.write('{0}={1}\n'.format(field, value))
                else:
                    f.write('{0}="{1}"\n'.format(field, value))
}
do_compile[vardeps] += "${OS_RELEASE_FIELDS}"

do_install () {
    install -d ${D}${nonarch_libdir} ${D}${sysconfdir}
    install -m 0644 os-release ${D}${nonarch_libdir}/
    ln -rs ${D}${nonarch_libdir}/os-release ${D}${sysconfdir}/os-release
    ln -rs ${D}${nonarch_libdir}/os-release ${D}${sysconfdir}/initrd-release
}

FILES:${PN} = "${sysconfdir}/os-release ${nonarch_libdir}/os-release"

PACKAGES += "${PN}-initrd"
FILES:${PN}-initrd = "${sysconfdir}/initrd-release"
RDEPENDS:${PN}-initrd += "${PN}"
