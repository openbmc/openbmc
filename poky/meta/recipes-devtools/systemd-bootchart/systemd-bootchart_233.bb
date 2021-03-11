SUMMARY = "Boot performance graphing tool"
DESCRIPTION = "For systemd-bootchart, several proc debug interfaces are required in the kernel config: \
  CONFIG_SCHEDSTATS \
below is optional, for additional info: \
  CONFIG_SCHED_DEBUG"
HOMEPAGE = "https://github.com/systemd/systemd-bootchart"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://github.com/systemd/systemd-bootchart.git;protocol=https \
           file://0001-architecture-Recognise-RISCV-32-RISCV-64.patch \
           file://mips64.patch \
"

SRC_URI_append_libc-musl = " \
    file://0001-comparison_fn_t-is-glibc-specific-use-raw-signature-.patch \
    file://0002-musl-does-not-provide-printf-h.patch \
    file://0003-musl-does-not-provide-canonicalize_file_name.patch \
    "


# Modify these as desired
PV = "233+git${SRCPV}"
SRCREV = "fe1c5e41e6bdb78043dad8fa863fc2df66d1dadf"

S = "${WORKDIR}/git"

DEPENDS = "systemd libxslt-native xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native intltool"

inherit pkgconfig autotools systemd features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE_${PN} = "systemd-bootchart.service"

do_configure_prepend() {
    # intltool.m4 is a soft link to /usr/share/aclocal/m4, delete it and use the one in our sysroot
    rm -f ${S}/m4/intltool.m4
}

FILES_${PN} += "${systemd_unitdir}/systemd-bootchart"

EXTRA_OECONF = " --with-rootprefix=${root_prefix} \
                 --with-rootlibdir=${base_libdir}"
