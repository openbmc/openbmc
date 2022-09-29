SUMMARY = "Glome Config"
DESCRIPTION = "Glome config file provides a glome config file"
PR = "r1"

# This is required to replace the glome/config that is removed in glome_git.bb

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Example Privkey: A0F1D0A0CB254839D04637F567325B850B5174850B129E811F5E203A42CC3B6C
GLOME_PUBLIC_KEY ?= "AC11D4582261F2D05CDDE1BD94383393D26C5C269642EE26D7EABD1EADC03C14"
GLOME_KEY_VERSION ?= "4"
GLOME_URL_PREFIX ?= "http://example-glome-service/"

SRC_URI = "file://config.in"

do_install:append() {
  if [ -z '${GLOME_PUBLIC_KEY}' ]; then
    echo 'Missing GLOME_PUBLIC_KEY' >&2
    exit 1
  fi
  if [ -z '${GLOME_KEY_VERSION}' ]; then
    echo 'Missing GLOME_KEY_VERSION' >&2
    exit 1
  fi
  if [ -z '${GLOME_URL_PREFIX}' ]; then
    echo 'Missing GLOME_URL_PREFIX' >&2
    exit 1
  fi

  sed ${WORKDIR}/config.in \
    -e 's#@PUBLIC_KEY@#${GLOME_PUBLIC_KEY}#' \
    -e 's#@KEY_VERSION@#${GLOME_KEY_VERSION}#' \
    -e 's#@URL_PREFIX@#${GLOME_URL_PREFIX}#' \
    > ${WORKDIR}/config

  install -d ${D}${sysconfdir}/glome
  install -m 0644 ${WORKDIR}/config ${D}${sysconfdir}/glome
}
