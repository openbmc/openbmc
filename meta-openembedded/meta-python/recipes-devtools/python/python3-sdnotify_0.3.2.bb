HOMEPAGE = "https://github.com/bb4242/sdnotify"
SUMMARY = "A pure Python implementation of systemd's service notification protocol (sd_notify)"

DESCRIPTION = "\
  sdnotify is a pure Python implementation of the systemd sd_notify protocol. \
  This protocol can be used to inform systemd about service start-up completion, \
  watchdog events, and other service status changes. \
  Thus, this package can be used to write system services in Python that play nicely with systemd. \
  "

SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cc572ccc4b18a4b7d13be5d01bc8213e"

SRC_URI[sha256sum] = "73977fc746b36cc41184dd43c3fe81323e7b8b06c2bb0826c4f59a20c56bb9f1"

SRC_URI += "file://0001-setup.py-switch-to-setuptools.patch"

inherit setuptools3 pypi

RDEPENDS:${PN} += "python3-io"
