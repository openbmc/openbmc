SUMMARY = "Glome Login Scripts"
DESCRIPTION = "Glome Login Scripts"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

GLOME_FALLBACK_SERV ?= ""
GLOME_FALLBACK_OBJ ?= ""
GLOME_HOSTNAME_SUFFIX ?= ""
GLOME_BOARDSN_KEY ?= "bmc-boardsn"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "glome"
RDEPENDS:${PN} += "jq"
RDEPENDS:${PN} += "obmc-console"

SRC_URI += "file://glome-login.sh.in"

do_install:append() {
  if [ -z '${GLOME_FALLBACK_SERV}' ]; then
    echo 'Missing GLOME_FALLBACK_SERV' >&2
    exit 1
  fi

  if [ -z '${GLOME_FALLBACK_OBJ}' ]; then
    echo 'Missing GLOME_FALLBACK_OBJ' >&2
    exit 1
  fi

  if [ -z '${GLOME_HOSTNAME_SUFFIX}' ]; then
    echo 'Missing GLOME_HOSTNAME_SUFFIX' >&2
    exit 1
  fi

  sed ${UNPACKDIR}/glome-login.sh.in \
    -e 's#@INV_SERV@#${GLOME_FALLBACK_SERV}#' \
    -e 's#@INV_OBJ@#${GLOME_FALLBACK_OBJ}#' \
    -e 's#@HOSTNAME_SUFFIX@#${GLOME_HOSTNAME_SUFFIX}#' \
    -e 's#@BOARDSN_KEY@#${GLOME_BOARDSN_KEY}#' \
    > ${UNPACKDIR}/glome-login.sh

  install -d ${D}${bindir}
  install -m 0755 ${UNPACKDIR}/glome-login.sh ${D}${bindir}
}

# This is an example to override the glome login service in the bbappend for 'prod'
#
#FILES:${PN}:append:prod = " \
#  ${systemd_system_unitdir}/serial-to-bmc@.service.d/bmc-login-glome-override.conf \
#  ${systemd_system_unitdir}/serial-getty@.service.d/bmc-login-glome-override.conf \
#  "
#
#do_install:append:prod() {
#  install -D -m 0644 ${UNPACKDIR}/bmc-login-glome-override.conf \
#    ${D}${systemd_system_unitdir}/serial-to-bmc@.service.d/bmc-login-glome-override.conf
#  install -D -m 0644 ${UNPACKDIR}/bmc-login-glome-override.conf \
#    ${D}${systemd_system_unitdir}/serial-getty@.service.d/bmc-login-glome-override.conf
#}
