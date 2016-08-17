HOMEPAGE = "http://www.newrelic.com"
SUMMARY = "New Relic Python Agent"
DESCRIPTION = "\
  Python agent for the New Relic web application performance monitoring \
  service. Check the release notes for what has changed in this version. \
  "
SECTION = "devel/python"
LICENSE = "BSD-3-Clause & MIT & Python-2.0 & BSD-2-Clause & NewRelic"
LIC_FILES_CHKSUM = "file://newrelic/LICENSE;md5=0f6cc160a8ed6759faa408a30b6ac978"

PR = "r0"
SRCNAME = "newrelic"

SRC_URI = "https://pypi.python.org/packages/source/n/newrelic/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "f8c9bf996d040a11847d14682b290eff"
SRC_URI[sha256sum] = "aa8869413c21aff441a77582df1e0fdc0f67342760eb7560d33ed3bbed7edf7b"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

FILES_${PN}-dbg += "\
  ${PYTHON_SITEPACKAGES_DIR}/newrelic-${PV}/newrelic/*/.debug \
  ${PYTHON_SITEPACKAGES_DIR}/newrelic-${PV}/newrelic/packages/*/.debug/ \
  "
