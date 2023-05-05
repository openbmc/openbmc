SUMMARY = "DMI (Desktop Management Interface) table related utilities"
HOMEPAGE = "http://www.nongnu.org/dmidecode/"
DESCRIPTION = "Dmidecode reports information about your system's hardware as described in your system BIOS according to the SMBIOS/DMI standard (see a sample output)."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/dmidecode/${BP}.tar.xz"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm|powerpc|powerpc64).*-linux"

do_install() {
    oe_runmake \
        DESTDIR="${D}" \
        prefix="${prefix}" \
        sbindir="${sbindir}" \
        docdir="${docdir}/${BPN}" \
        mandir="${mandir}" \
        install
}

SRC_URI[sha256sum] = "79d76735ee8e25196e2a722964cf9683f5a09581503537884b256b01389cc073"
