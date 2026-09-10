SUMMARY = "Y2038 safe version of wtmp"
HOMEPAGE = "https://github.com/thkukuk/wtmpdb"
DESCRIPTION = "last reports the login and logout times of users and when the machine got rebooted."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=020090a00b69dd2af9ab82eb0003ea2c"
SECTION = "libs"

SRCREV = "89c0861f74e576c5ad0ec9f5302aa635a4e50dc6"

SRC_URI = "git://github.com/thkukuk/wtmpdb.git;branch=main;protocol=https;tag=v${PV}"


inherit meson pkgconfig systemd features_check

DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} sqlite3 "
REQUIRED_DISTRO_FEATURES = "pam"

SYSTEMD_SERVICE:${PN} = "wtmpdb-update-boot.service wtmpdb-rotate.service"

EXTRA_OEMESON = " -Dpamlibdir=${base_libdir}/security"

do_install:append () {
      if [ -d ${D}${prefix}/lib/systemd -a ${D}${prefix}/lib != `dirname ${D}${systemd_unitdir}` ]; then
          # Fix makefile hardcoded path assumptions for systemd (assumes $prefix)
          # without usrmerge distro feature enabled
          install -d `dirname ${D}${systemd_unitdir}`
          mv ${D}${prefix}/lib/systemd `dirname ${D}${systemd_unitdir}`
      fi
}

FILES:${PN} += " ${systemd_system_unitdir} "
FILES:${PN} += " ${libdir} "
FILES:${PN} += " ${nonarch_libdir}/tmpfiles.d/* "
FILES:${PN} += " ${base_libdir}/security/*.so "

TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"
